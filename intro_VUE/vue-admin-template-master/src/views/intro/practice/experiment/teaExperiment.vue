<template>
  <div class="app-container">
    <!-- 上部分查询表栏-->
    <div class="show" style="margin-top: 10px;" >
        <el-form :inline="true" class="demo-form-inline" label-width="800px">
            
                <el-form-item label-width="150px" label="已发布的实验练习：" prop="pass" style="margin-left: 10px; float:left">
                        {{this.total}}
                </el-form-item>
                <el-form-item>
                    <el-button style="margin-right: 20px; float:right" type="default" @click="saveOrUpdate()" >发布新的实验</el-button>
                </el-form-item>
        </el-form>
    </div>

    <div class="show">
        <!-- 显示数据栏 -->
        <!-- 表格 -->
        <el-table
            v-loading="listLoading"
            :data="list"
            element-loading-text="数据加载中"
            border
            fit
            highlight-current-row>

            <el-table-column
                label="序号"
                width="50"
                align="center">
                <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>

            <el-table-column prop="title" label="实验标题" align="center"/> 

            <el-table-column label="所属章节"  width="160" align="center">
                <template slot-scope="scope">
                    {{chapterNameList[scope.$index]}}
                </template>
            </el-table-column>

            <el-table-column label="所属小节" width="170" align="center">
                <template slot-scope="scope">
                    {{sectionNameList[scope.$index]}}
                </template>
            </el-table-column>
            
            <el-table-column :show-overflow-tooltip = "true" prop="description" label="实验描述" align="center" width="160"/>

            <el-table-column label="完成情况" width="80" align="center">
                <template slot-scope="scope">
                    {{completion[scope.$index]}}
                </template> 
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    {{formatDate(scope.row.gmtCreate)}}
                </template> 
            </el-table-column>

            <el-table-column label="截止时间" width="160" align="center">
                <template slot-scope="scope">
                    {{formatDate(scope.row.gmtDeadline)}}
                </template> 
            </el-table-column>

            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <el-button-group>
                      <el-button type="success" size="mini" icon="el-icon-search" @click="toShowExperiment(scope.row.id)"></el-button>
                      <el-button type="primary" size="mini" icon="el-icon-edit" @click="saveOrUpdate(scope.row.id)"></el-button>
                      <el-button type="danger" size="mini" icon="el-icon-delete" @click="deleteExperiment(scope.row.id)"></el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table> 
        <!-- 分页 -->
        <el-pagination
            background
            :current-page="page"
            :page-size="limit"
            :total="total"
            style="padding: 30px 0; text-align: center;"
            layout="total, prev, pager, next, jumper"
            @current-change="getList"
        />
    </div>

    <!-- 添加实验练习弹窗 -->
    <div>
        <!-- 弹出的做题框 -->
        <el-dialog 
        :title="'添加实验练习'" 
        :visible.sync="dialogFormVisible"  
        :before-close="handleClose">
            <el-form ref="postForm" :model="experimentInfo" class="form-container">

                <!-- 所属章节：级联下拉列表 -->
                <el-form-item style="margin-bottom: 5px;" label-width="200px" label="修改题目时不能修改章节信息!" v-if="updateInfo"/>
                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="选择章节:" v-if="!updateInfo">
                    <!-- 一级分类 章 -->
                    <el-select
                    v-model="experimentInfo.chapterId"
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
                        v-model="experimentInfo.sectionId" 
                        placeholder="选择节">
                        <el-option
                            v-for="section in sectionTwoList"
                            :key="section.id"
                            :label="section.title"
                            :value="section.id"/>
                    </el-select>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="标题:">
                    <el-input :rows="1" v-model="experimentInfo.title" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="实验描述:">
                    <el-input :rows="1" v-model="experimentInfo.description" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="100px" label="报告模板:">
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
                    <div slot="tip" class="el-upload__tip">上传实验报告模板</div>
                    </el-upload>
                </el-form-item>

                <el-form-item style="margin-left: 4px;" label-width="100px" label="截止日期:">
                  <el-date-picker
                    v-model="experimentInfo.gmtDeadline"
                    type="datetime"
                    placeholder="选择截止时间"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    default-time="00:00:00"
                  />
                </el-form-item>
            

                <template #footer style="float:right;">
                    <span class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">返回</el-button>
                    <el-button type="primary" @click="saveExperiment()">提交</el-button>
                    </span>
                </template>

            </el-form>
        </el-dialog>
    </div>

  </div>
</template>

<script>

import chapter from '@/api/intro/chapter'
import experiment from '@/api/intro/practice/experiment'
import * as dateUtils from "@/utils/date"
import cookie from 'js-cookie'
import ossApi from '@/api/oss/fileOss'

export default {
    data(){
        return {
            page:1, //当前页
            limit:10, //每页记录数
            total:0, //总记录数
            listLoading: true, // 是否显示loading信息
            list:null,//查询之后接口返回集合
            teacher: {}, //登陆的教师
            dialogFormVisible: false, //弹框是否可视
            completion: {}, //显示完成情况
            updateInfo: false, //是否是修改
            experimentInfo: {},
            chapterNameList: {}, //章节名称
            sectionNameList: {}, //小节名称
            chapterOneList:{}, //一级章节目录
            sectionTwoList:{}, //二级小节目录
            BASE_API: process.env.BASE_API, // 接口API地址
            fileList: [], //文件列表
        }
    },
  created(){
      //获取列表
      this.getList()
      this.initChapterOneList()
  },
  methods: {
   
    //刷新列表
    getList(page=1){
        this.teacher = JSON.parse(cookie.get('intro_roles'))
        this.page = page
        this.listLoading = true
        experiment.findExperimentPage(this.page,this.limit)
        .then(response =>{
            this.list = response.data.experimentList
            console.log("=====实验列表=====");
            console.log(this.list);
            this.total = response.data.total
            this.completion = response.data.completion
            this.chapterNameList = response.data.chapterNameList
            this.sectionNameList = response.data.sectionNameList
            console.log(this.sectionNameList);
        })
        .catch(error=>{
            console.log(error);
        })
        this.listLoading = false
    },

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
 
    //格式化日期
    formatDate(dateStr){
        if(dateStr==null){
          return "--"
        }else{
          let date = new Date(dateStr);
          return dateUtils.formatDate(date,'yyyy-MM-dd hh:mm:ss');
        }
    },

    saveOrUpdate(id){
      //打开弹窗
      this.dialogFormVisible = true
      
        //判断更新还是保存 进行数据回显
      if(id != null){ //进行数据回显
          this.updateInfo = true
          experiment.getExperimentById(id).then( response=>{
              this.experimentInfo = response.data.experimentInfo
              console.log("========="+this.experimentInfo);
          })
      }
    },

    //提交更新或新建
    saveOrUpdateExperiment(){
      console.log(this.experimentInfo);
      if(this.experimentInfo.id!=null){
        experiment.updateExperiment(this.experimentInfo).then( () =>{
          this.$message({
              type: 'success',
              message: '更新成功!'
          });
          this.handleClose()
          this.getList()
          
        }).catch(_ => {
          console.log("取消更新");
        });
      }else{
        experiment.addExperiment(this.experimentInfo).then( () =>{
          this.$message({
              type: 'success',
              message: '添加成功!'
          });
          this.handleClose()
          this.getList()
          
        }).catch(_ => {
          console.log("取消添加");
        });
      }
    },
    toShowExperiment(id){
      console.log("查看跳转的课前预习"+id);
      this.$router.push({ path:'/practice/showExperiment/'+ id})
    },

    deleteExperiment(id){
      console.log(id);
      this.$confirm('确认删除该课前预习？同时学生端的本次练习也会被删除',{
                confirmButtonText: '确定',
                cancelButtonText: '取消',})
      .then(_ => {
          //确认删除
        experiment.deleteExperimentById(id).then( ()=>{
          this.$message({
              type: 'success',
              message: '删除成功!'
          });
          this.getList()
        })
      })
      .catch(_ => {
          console.log("取消删除");
      });
    },

    //关闭弹窗
    handleClose(){
        this.dialogFormVisible = false
        this.updateInfo = false
        this.experimentInfo = {}
    },

    //==========上传文件相关操作==================
    //更换头像
    handleUploadSuccess(response, file, fileList){  //提交成功之后
        console.log("===============这里是上传文件操作=================");
        console.log(response.data.url);
        //上传头像url赋值
        this.experimentInfo.originalUrl = response.data.url
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
    saveExperiment(){
        this.$message.success('保存成功')
        this.saveOrUpdateExperiment()
    },


    
  }

}
</script>


<style scoped>
.myClassList .info {
    width: 450px;
    overflow: hidden;
}
.myClassList .info .pic {
    width: 150px;
    height: 90px;
    overflow: hidden;
    float: left;
}
.myClassList .info .pic a {
    display: block;
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
}
.myClassList .info .pic img {
    display: block;
    width: 100%;
}
.myClassList td .info .title {
    width: 280px;
    float: right;
    height: 90px;
}
.myClassList td .info .title a {
    display: block;
    height: 48px;
    line-height: 24px;
    overflow: hidden;
    color: #00baf2;
    margin-bottom: 12px;
}
.myClassList td .info .title p {
    line-height: 20px;
    margin-top: 5px;
    color: #818181;
}

.search .el-input  {
  width: 160px;
}
.search .el-select{
  width: 160px;
}
.search .el-date-picker{
  width: 100px;
}

.option li{
  width:100%;
  height: 20px;
}
.option span{
  float: left;
}
.shortWritten input{
  float: right;
}
.tableTitle {
    position: relative;
    margin: 0 auto;
    width: 600px;
    height: 2px;
    background-color: #d4d4d4;
    text-align: center;
    font-size: 16px;
    color: rgba(101, 101, 101, 1);
  }
</style>
