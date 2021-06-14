<template>
  <div class="app-container">
  
    <!-- 上部分查询表栏-->
    <div class="search">
      <el-form :inline="true" class="demo-form-inline" label-width="120px">

        <!-- 标题 -->
        <el-form-item>
          <el-input v-model="experimentQuery.title" placeholder="练习标题"/> 
        </el-form-item>

          <!-- 所属章节：级联下拉列表 -->
        <el-form-item>
          <!-- 一级分类 章 -->
          <el-select
          v-model="experimentQuery.chapter"
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
              v-model="experimentQuery.section" 
              placeholder="选择节">
              <el-option
                  v-for="section in sectionTwoList"
                  :key="section.id"
                  :label="section.title"
                  :value="section.id"/>
            </el-select>
          </el-form-item>
          

          <!-- 起止时间 -->
          <el-form-item>
              <el-date-picker
                v-model="experimentQuery.begin"
                type="datetime"
                placeholder="选择开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                default-time="00:00:00"
              />
          </el-form-item>
          <el-form-item>
            <el-date-picker
              v-model="experimentQuery.end"
              type="datetime"
              placeholder="选择截止时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              default-time="00:00:00"
            />
          </el-form-item>
          
        <!-- 操作 -->
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
          <el-button type="default" @click="resetData()">清空</el-button>
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
              width="70"
              align="center">
              <template slot-scope="scope">
                  {{ (page - 1) * limit + scope.$index + 1 }}
              </template>
          </el-table-column>

          <el-table-column label="练习标题" align="center"> 
           <template slot-scope="scope">
              {{toShowName[scope.$index]}}
            </template> 
          </el-table-column>
          
          <el-table-column label="完成情况" width="100" align="center">
              <template slot-scope="scope">
                {{ scope.row.finish===true?'已完成':'未完成' }}
              </template>
          </el-table-column>

          <el-table-column prop="score" label="练习成绩" width="80" align="center"/>
              

          <el-table-column label="截止时间" width="200" align="center">
            <template slot-scope="scope">
              {{formatDate(scope.row.gmtDeadline)}}
            </template> 
          </el-table-column>

          <el-table-column label="完成时间" width="200" align="center">
            <template slot-scope="scope">
              {{formatDate(scope.row.gmtFinish)}}
            </template> 
          </el-table-column>

          <el-table-column label="操作" width="200" align="center">
              <template slot-scope="scope">
              <el-button type="primary" size="mini" icon="el-icon-upload" :disabled='scope.row.finish || judge(scope.row.gmtDeadline)' @click="addReport(scope.row.id)">上传报告</el-button>
              <router-link :to="'/practice/experiment/'">
                  <el-button type="primary" size="mini" icon="el-icon-download" @click="getBaseFileById(scope.row.experimentId)">下载</el-button>
              </router-link>
              </template>
          </el-table-column>
      </el-table> 

      <!-- 分页 -->
      <!-- <el-pagination
        background
        :current-page="page"
        :page-size="limit"
        :total="total"
        style="padding: 30px 0; text-align: center;"
        layout="total, prev, pager, next, jumper"
        @current-change="getList"
      /> -->

      <!-- 上传文件弹窗 -->
      <el-dialog :visible.sync="dialoguploadFileVisible" title="上传实验报告">
        <el-form :model="stuExperiment" label-width="120px">
          <el-form-item label="自测评分">
            <el-rate v-model="importance" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" :max="5" style="margin-top:8px;"/>
          </el-form-item>

          <el-upload
                :on-success="handleUploadSuccess"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                :on-exceed="handleUploadExceed"
                :file-list="fileList"
                :action="BASE_API+'/introOss/fileOss/uploadFile'"
                :limit="1"
                class="upload-demo">
            <el-button size="small" type="primary">上传文件</el-button>
            <el-tooltip placement="right-end">
                <div slot="content">最大支持32MB，建议使用PDF格式上传<br>
                    支持WORD、PDF等格式上传</div>
                <i class="el-icon-question"/>
            </el-tooltip>
          </el-upload>
          
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="dialoguploadFileVisible = false">取 消</el-button>
          <el-button :disabled="saveFileBtnDisabled" type="primary" @click="saveFile">确 定</el-button>
        </div>
      </el-dialog>



    </div>
  </div>
</template>

<script>

import chapter from '@/api/intro/chapter'
import experiment from '@/api/intro/practice/experiment'
import oss from '@/api/oss/fileOss'
import * as dateUtils from "@/utils/date"
import cookie from 'js-cookie'
export default {
  data(){
    return {
      page: 1,
      limit: 10,
      total:0, //总记录数
      listLoading: true, // 是否显示loading信息
      list:null,//查询之后接口返回集合
      experimentQuery:{}, //封装查询对象 
      chapterOneList:{}, //一级章节目录
      sectionTwoList:{}, //二级小节目录
      student:{}, //登陆的学生信息
      fileUrl:'', //模板下载地址
      BASE_API: process.env.BASE_API, // 接口API地址
      saveFileBtnDisabled: true, // 上传文件保存按钮
      dialoguploadFileVisible: false, //上传文件弹框是否显示
      fileList: [],//上传文件列表
      importance: 1,
      stuExperiment: {
        id: '',
        stuId: '',
        ossUrl: '', //阿里云地址
      },
      toShowName: {}, //显示的名称
    }
  },
  created(){
      //获取列表  
      this.getList()
      //初始化一级章节目录
      this.initChapterOneList()

  },
  methods: {
    //===========================上传文件的方法==========================
    handleUploadSuccess(response, file, fileList){  //提交成功之后
      console.log("===============这里是上传文件操作=================");
      console.log(response.data.url);
      //获取上传后的阿里云地址
      this.stuExperiment.ossUrl = response.data.url
      console.log(this.stuExperiment.ossUrl);
      console.log(this.stuExperiment);
      this.saveFileBtnDisabled = false;
    },
    handleUploadExceed(files, fileList){
        this.$message.warning('想要重新上传文件，请先删除已上传的文件')
    },
    //确认删除
    handleRemove(file, fileList) {
      console.log(file)
      oss.removeOssFile(this.stuExperiment.ossUrl).then(response=>{
        this.stuExperiment.ossUrl = ''
        //清空文件列表
        this.fileList = []
        this.$message({
          type: 'success',
          message: response.message
        })
        this.saveFileBtnDisabled = true;
      })
    },
    //确认删除提示
    beforeRemove(file, fileList){
        return this.$confirm(`确定移除 ${file.name}？`)
    },
    // ======================================================================

    //刷新列表
    getList(page=1){
      this.student = JSON.parse(cookie.get('intro_roles'))
      console.log("当前学生信息："+this.student.id);
      console.log(this.experimentQuery);
      this.page = page
      this.listLoading = true
      experiment.findAllByStudent(this.page,this.limit,this.student.id,this.experimentQuery)
      .then(response =>{
        this.list = response.data.stuExperimentPage
        this.toShowName = response.data.toShowName
        this.total = response.data.total
        this.experimentQuery = {}
      })
      .catch(error=>{
        console.log(error);
      })
      this.listLoading = false
    },

    //清空功能
    resetData() {
        this.experimentQuery = {}
        this.getList()
    },
    judge(date){
      var now = new Date()
      var past = new Date(date)
      return past<now
    },

    //初始化一级章节目录
    initChapterOneList(){
      chapter.getChapterList().then(response =>{
        this.chapterOneList = response.data.chapterList
      })
    },
   
    //一级目录更换
    LevelOneChanged(value){
      console.log(this.experimentQuery.chapterId);
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

    //通过实验id获取实验的url
    getBaseFileById(experimentId){
      console.log(experimentId);
      experiment.getUrlById(experimentId).then(response =>{
        this.fileUrl = response.data.fileUrl
        console.log(this.fileUrl);
        oss.loadOssFile(this.fileUrl).then(()=>{
          //下载成功
          this.$message({
              type: 'success',
              message: '下载成功!'
          });
        })
      })
      .catch(error=>{
        console.log(error);
      })
    },

    //打开上传文件弹框
    addReport(id){
      this.dialoguploadFileVisible = true;
      this.stuExperiment.id = id;
      this.stuExperiment.stuId = this.stuId;
      this.stuExperiment.ossUrl = '';
      this.fileList = []
    },

    saveFile(){
      console.log(this.stuExperimrnt);
      experiment.updateStuExperiment(this.stuExperiment).then(()=>{
        //保存成功
        this.$message({
            type: 'success',
            message: '保存成功!'
        });
        this.dialoguploadFileVisible = false;
        this.stuExperiment.ossUrl = '';
        this.fileList = [];

      })
    }


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
</style>
