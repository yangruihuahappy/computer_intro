<template>
  <div class="app-container">
  
  <!-- 上部分查询表栏-->
  <div class="search">
    <el-form :inline="true" class="demo-form-inline" label-width="120px">

      <!-- 标题 -->
      <el-form-item>
        <el-input v-model="PreviewQuery.title" placeholder="练习标题"/> 
      </el-form-item>

        <!-- 所属章节：级联下拉列表 -->
      <el-form-item>
        <!-- 一级分类 章 -->
        <el-select
        v-model="PreviewQuery.chapter"
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
            v-model="PreviewQuery.section" 
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
              v-model="PreviewQuery.begin"
              type="datetime"
              placeholder="选择开始时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              default-time="00:00:00"
            />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="PreviewQuery.end"
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

          <el-table-column prop="PreviewName" label="练习标题" align="center"> 
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

          <el-table-column label="操作" width="120" align="center">
              <template slot-scope="scope">
              <el-button type="primary" size="mini" icon="el-icon-edit" :disabled='scope.row.finish || judge(scope.row.gmtDeadline)' @click="toComplete(scope.row.id)">去完成</el-button>
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

  </div>

  <div>
    <!-- 弹出的做题框 -->
    <el-dialog 
      :title="'完成课前预习'" 
      :visible.sync="dialogFormVisible"  
      :before-close="handleClose">
      <div class="div" v-for="(son,index) in topicList" :key="index">
        <div class="question">问题:{{son.content}}</div>
        <div class="type">类型：{{judgeType(son.type)}}</div>
        <!-- 如果是选择题 -->
        <div v-if="son.type === 0" class="option">
          <li v-for="(op,index1) in cutOffOptions(son.options)" :key="index1" >
            <span>{{op}}</span>
            <input type="radio" :name="son.content"  :value="op" @change="get_radio_value(index)" v-model="checkedValue[index]" style="float:left;margin-bottom: 5px;">
          </li>
          <div class="tableTitle"></div>
        </div>
        <!-- 如果是多选题 -->
        <div v-if="son.type === 1" class="option">
          <li v-for="(op,index1) in cutOffOptions(son.options)" :key="index1">
            <span>{{op}}</span>
            <input type="checkbox" :name="son.content" :value="op" @change="get_checkbox_value(index)" v-model="checkedValue1" >
          </li>
        </div>
        <!-- 如果是简答题 -->
        <div v-if="son.type === 2" class="shortWritten">
          <input type="text" :name="son.content" @change="get_input_value(index)" v-model="inputValue[index]" style="float:left;margin-bottom: 5px;margin-top: 5px;">
        </div>
        <div style="clear: both"></div>
        <div style="clear: both"></div>
      </div>  

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">返回</el-button>
          <el-button type="primary" :disabled='!this.reviewButton' @click="btnfun()">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>

  </div>
</template>

<script>

import chapter from '@/api/intro/chapter'
import preview from '@/api/intro/practice/preview'
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
      PreviewQuery:{}, //封装查询对象 
      chapterOneList:{}, //一级章节目录
      sectionTwoList:{}, //二级小节目录
      student: {}, //登陆的学生
      stuPreviewInfo: {}, //待显示的课前预习内容
      topicList: { //打开的课前预习的题目
        type: 0, //题目默认类型
        content: "", //题目默认内容
        options: "", //题目的选项集
        answer: "", //题目的答案
      },
      answerList: {}, //学生的答案集
      dialogFormVisible: false, //弹框是否可视
      reviewButton: false, //弹筐里的确定键是否可点
      checkedValue: [], // 绑定单选框的值
      checkedValue1: [], // 绑定复选框的值
      inputValue: [], //填空题的值
      lastAnswerList: [], //最终存储答案的集合
      defaultOp :["A.","B.","C.","D."], //默认选项避免题目的选项出现空的情况
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
    //判断类型
    judgeType(type){
      if(type == 0){
        return "单选题"
      }else if(type == 1){
        return "多选题"
      }else{
        return "简答题"
      }
    },
    judge(date){
      var now = new Date()
      var past = new Date(date)
      return past<now
    },
    //刷新列表
    getList(page=1){
      this.student = JSON.parse(cookie.get('intro_roles'))
      console.log("当前学生信息："+this.student.id);
      console.log(this.PreviewQuery);
      this.page = page
      this.listLoading = true
      preview.findAllByStudent(this.page,this.limit,this.student.id,this.PreviewQuery)
      .then(response =>{
        this.list = response.data.stuPreviewPage
        this.toShowName = response.data.toShowName
        this.total = response.data.total
        this.PreviewQuery = {}
      })
      .catch(error=>{
        console.log(error);
      })
      this.listLoading = false
    },

    //清空功能
    resetData() {
        this.PreviewQuery = {}
        this.getList()
    },

    //初始化一级章节目录
    initChapterOneList(){
      chapter.getChapterList().then(response =>{
        this.chapterOneList = response.data.chapterList
      })
    },
   
    //一级目录更换
    LevelOneChanged(value){
      console.log(this.PreviewQuery.chapterId);
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

    //点击去完成按钮后 传入id为stu_previewId
    toComplete(id){
      this.reviewButton = true, //弹筐里的确定键是否可点
      this.dialogFormVisible = true
      preview.findStuPreviewById(id).then(response =>{
        this.stuPreviewInfo = response.data.stuPreviewInfo
        this.topicList = response.data.topicList
        this.answerList = response.data.answerList
        console.log(this.stuPreviewInfo);
        console.log(this.topicList);
        console.log(this.answerList);
      })
    },

    
    //处理选项操作 选项之间通过_op_连接的
    cutOffOptions(options){

      console.log(options);
      if(options!=null){
        var optionList = options.split("_op_");
        console.log(optionList);
        return optionList;
      }else{
        return this.defaultOp;
      }
    },

    //修改传递过来的值
    get_radio_value(index){
      this.lastAnswerList[index] = this.checkedValue[index]
    },
    //复选框
    get_checkbox_value(index){
      var tempValue = this.checkedValue1
      if(this.lastAnswerList[index]==null){
        this.checkedValue1 = [];
        this.lastAnswerList[index] = tempValue
      }else{
        this.lastAnswerList[index] = this.checkedValue1
      }
    },
    //填空题
    get_input_value(index){
      this.lastAnswerList[index] = this.inputValue[index]
    },

    //确认关闭弹窗？
    handleClose(done) {
      this.$confirm('关闭窗口会直接提交本次答题，是否确认？')
        .then(_ => {
          done();
          console.log("用户关闭了自动提交当前的答案");
          this.btnfun();
        })
        .catch(_ => {});
    },

    //弹窗提交
    btnfun(){
      console.log(this.lastAnswerList);
      //对所有checkValue进行拼接存到属性中
      var answerStr = "";
      console.log("长度："+this.lastAnswerList.length);
      for(var i=0;i<this.lastAnswerList.length;i++){
        if(i<this.lastAnswerList.length-1){
          answerStr =answerStr + this.lastAnswerList[i]+"_ans_";
        }else{
          answerStr =answerStr + this.lastAnswerList[i]
        }
      }
      console.log(this.stuPreviewInfo);
      console.log("答案看这里"+answerStr);
      this.stuPreviewInfo.stuAnswers = answerStr;
      console.log(this.stuPreviewInfo);
      preview.addStuPreview(this.stuPreviewInfo).then( ()=>{
         this.$message({
              type: 'success',
              message: '提交成功!'
          });
          this.resetStuPreview();
      })
    },

    //重置提交信息
    resetStuPreview(){
      this.stuPreviewInfo = ""
      this.topicList = {
        type: 0, //题目默认类型
        content: "", //题目默认内容
        options: "", //题目的选项集
        answer: "", //题目的答案}
      }
      this.dialogFormVisible= false //弹框是否可视
      this.checkedValue = []
      this.checkedValue1 = []
      this.lastAnswerList = []
      this.inputValue = []
      this.getList(1);
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
