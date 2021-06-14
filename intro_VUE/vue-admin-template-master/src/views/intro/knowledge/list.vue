<template>
  
  <div class="app-container">
    <div>
    <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:12px;margin-bottom:30px;">
      <!-- 搜索输入框 -->
      <div v-if="!admin">
        <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />
      </div>
      <div v-else>
        <el-row :gutter="10">
          <el-col :span="20">
            <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />
          </el-col>
          <el-col :span="4">
            <el-button type="primary" plain @click="addKnow()">添加知识</el-button>
          </el-col>
        </el-row>
        
        
      </div>

      <el-tree
        ref="tree2"
        :data="knowledgeData"
        :props="defaultProps"
        :filter-node-method="filterNode"
        @node-click="handleNodeClick"
        class="filter-tree"
        default-expand-all
      />
    </el-col>

    <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 30}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;padding-right:8px">
      <!-- 显示详情 -->
        <details-info :knowledgeId='this.knowledgeId'></details-info>  
    </el-col>
    </div>

    <!-- 添加知识点 -->
    <div>
        <!-- 弹出的知识点框 -->
        <el-dialog 
        :title="'添加知识点'" 
        :visible.sync="dialogVisible"  
        :before-close="handleClose">
            <el-form ref="postForm" :model="knowInfo" class="form-container">

                <!-- 所属章节：级联下拉列表 -->
                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="选择章节:">
                    <!-- 一级分类 章 -->
                    <el-select
                    v-model="knowInfo.chapterId"
                    placeholder="选择章"
                    @change="LevelOneChanged"
                    >
                    <el-option
                        v-for="chapter in chapterOneList"
                        :key="chapter.id"
                        :label="chapter.title"
                        :value="chapter.id"/>
                    </el-select>
                    <!-- 二级分类 节 -->
                    <el-select 
                        v-model="knowInfo.sectionId" 
                        placeholder="选择节">
                        <el-option
                            v-for="section in sectionTwoList"
                            :key="section.id"
                            :label="section.title"
                            :value="section.id"/>
                    </el-select>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="标题:">
                    <el-input :rows="1" v-model="knowInfo.title" type="textarea" class="article-textarea" autosize placeholder="请输入标题"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="内容:">
                    <el-input :rows="10" maxlength="500"  v-model="knowInfo.content" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="上传资料">
                    <el-upload
                        class="upload-demo"
                        :on-success="handleUploadSuccess"
                        :on-remove="handleRemove"
                        :before-remove="beforeVodRemove"
                        :on-exceed="handleUploadExceed"
                        :file-list="fileList"
                        :action="BASE_API+'/introOss/fileOss/uploadFile'"
                        :limit="1">
                        <el-button size="small" type="primary">点击上传</el-button>
                    <el-tooltip placement="right-end">
                        <div slot="content">上传课堂资料，最大支持500MB，支持word、pdf、excel格式</div>
                        <i class="el-icon-question"/>
                      </el-tooltip>
                    </el-upload>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="上传视频">
                  <el-upload
                        :on-success="handleVodUploadSuccess"
                        :on-remove="handleVodRemove"
                        :before-remove="beforeVodRemove"
                        :on-exceed="handleVodUploadExceed"
                        :file-list="fileListVod"
                        :action="BASE_API+'/introVideo/video/uploadAlyVideo'"
                        :limit="1"
                        class="upload-demo">
                    <el-button size="small" type="primary">上传视频</el-button>
                    <el-tooltip placement="right-end">
                        <div slot="content">最大支持1G，<br>
                            支持3GP、ASF、AVI、DAT、DV、FLV、F4V、<br>
                            GIF、M2T、M4V、MJ2、MJPEG、MKV、MOV、MP4、<br>
                            MPE、MPG、MPEG、MTS、OGG、QT、RM、RMVB、<br>
                            SWF、TS、VOB、WMV、WEBM 等视频格式上传</div>
                        <i class="el-icon-question"/>
                    </el-tooltip>
                  </el-upload>
              </el-form-item>

                <template #footer style="float:right;">
                    <span class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">返回</el-button>
                    <el-button type="primary" @click="saveKnowledge()">提交</el-button>
                    </span>
                </template>

            </el-form>
        </el-dialog>
    </div>

  </div>
</template>

<script>

import chapter from '@/api/intro/chapter'
import DetailsInfo from './details'
import cookie from 'js-cookie'
import video from '@/api/video/video'
import ossApi from '@/api/oss/fileOss'
import know from '@/api/intro/knowledge'

export default {
  components:{
    DetailsInfo,
  },
  data(){
    return {
      filterText: '',
      knowledgeData: [],  //返回所有数据
      defaultProps: {
        children: 'children',
        label: 'title'
      },
      knowledgeId: '', //要显示的id
      dialogVisible: false,
      admin: false, //是否是管理员
      //添加的知识点信息
      knowInfo:{},
      fileList: [],//上传文件列表
      fileListVod: [],//上传视频列表
      chapterOneList:{}, //一级章节目录
      sectionTwoList:{}, //二级小节目录
      BASE_API: process.env.BASE_API, // 接口API地址
    }
  },
  created(){
    this.admin = (cookie.get('intro_admin') === "true")
    this.getAllChapterList()
    this.initChapterOneList()
    
  },
  watch: {
    filterText(val) {
      
      this.$refs.tree2.filter(val)
    }
  },
  methods: {
    getAllChapterList(){
        chapter.getChapterList()
            .then(response =>{
                this.knowledgeData = response.data.chapterList
            })
        console.log(this.knowledgeData);
    },

    filterNode(value, data) {
      
      if (!value) return true
      return data.title.toLowerCase().indexOf(value.toLowerCase()) !== -1
    },
    handleNodeClick(data) {
      if(data.children==null){
        this.knowledgeId = data.id;
      }
    },

    addKnow(){
      this.dialogVisible = true
    },

    //========================================添加新的知识点方法=========================================
    //===============文件操作==============
    handleUploadSuccess(response, file, fileList){  //提交成功之后
        console.log("===============这里是上传文件操作=================");
        console.log(response.data.url);
        //上传头像url赋值
        this.knowInfo.fileUrl = response.data.url
        console.log(this.student);
    },
    handleRemove(file, fileList) {
        ossApi.removeOssFile(file).then(response =>{
            //清空文件列表
            this.fileList = []
            this.$message({
                type: 'success',
                message: response.message
            })
        })
        console.log(file, fileList);
    },
    handleUploadExceed(files, fileList){
        this.$message.warning('想要重新上传文件，请先删除已上传的文件')
    },
    //确认删除提示
    beforeVodRemove(file, fileList){
        return this.$confirm(`确定移除 ${file.name}？`)
    },
    //=================视频操作==============
    handleVodUploadSuccess(response, file, fileListVod){  //提交成功之后
      console.log("===============这里是上传视频操作=================");
      console.log(response.data.videoId);
      //上传视频id赋值
      this.knowInfo.videoSourceId = response.data.videoId
      console.log(this.knowInfo.videoSourceId);
      //上传视频名称赋值
      this.knowInfo.videoOriginalName = file.name
      console.log(this.knowInfo.videoSourceId);
      console.log(this.knowInfo);

    },
    handleVodUploadExceed(files, fileListVod){
        this.$message.warning('想要重新上传视频，请先删除已上传的视频')
    },
    //确认删除
    handleVodRemove(file, fileListVod) {
      console.log(file)
      video.removeById(this.knowInfo.videoSourceId).then(response=>{
        this.knowInfo.videoSourceId = ''
        this.knowInfo.videoOriginalName = ''
        //清空文件列表
        this.fileListVod = []
        this.$message({
          type: 'success',
          message: response.message
        })
      })
    },
    //========章节方法=========
     //初始化一级章节目录
    initChapterOneList(){
      chapter.getChapterList().then(response =>{
        this.chapterOneList = response.data.chapterList
      })
    },
   
    //一级目录更换
    LevelOneChanged(value){
      for(let i=0;i<this.chapterOneList.length; i++){
        if (this.chapterOneList[i].id === value) {
            this.sectionTwoList = this.chapterOneList[i].children
            // this.PreviewQuery.sectionId = '' //把二级分类清空
        }
      }
    },

    //===============添加方法=============
    saveKnowledge(){
      console.log(this.knowInfo);
      know.addKnowledge(this.knowInfo).then( () =>{
          this.$message({
              type: 'success',
              message: '添加成功!'
          });
          this.handleClose()
          
        }).catch(_ => {
          console.log("取消添加");
      });
    },
    handleClose(){
      this.knowInfo = {}
      this.dialogVisible = false
      this.getAllChapterList()
    }
  }

}
</script>
