<template>
  <div class="app-container">
  
    <!-- 上部分查询表栏-->
    <div class="search">
      <el-form :inline="true" class="demo-form-inline" label-width="120px">

        <!-- 标题 -->
        <el-form-item>
          <el-input v-model="summaryQuery.title" placeholder="练习标题"/> 
        </el-form-item>

        <!-- 起止时间 -->
        <el-form-item>
            <el-date-picker
              v-model="summaryQuery.begin"
              type="datetime"
              placeholder="选择开始时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              default-time="00:00:00"
            />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="summaryQuery.end"
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

          <el-table-column prop="score" label="总结成绩" width="80" align="center"/>
              

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
              <router-link :to="'/summary/index/'">
                <el-button type="primary" size="mini" icon="el-icon-edit" :disabled='scope.row.finish || judge(scope.row.gmtDeadline)' @click="handleCreate(scope.row.summaryId)">去完成</el-button>
              </router-link>
              <el-button type="primary" size="mini" icon="el-icon-edit" :disabled='!scope.row.finish' @click="toSee(scope.row.id)">查看</el-button>
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

    <!-- 弹出的新增和编辑框 -->
    <el-dialog :title="'添加每周总结'" :visible.sync="dialogFormVisible"  :close-on-click-modal='false'>
      <el-form ref="dataForm"  :value="temp" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
        <el-form-item >
          自我评价
          <el-rate v-model="temp.importance" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" :max="5" style="margin-top:8px;"/>
        </el-form-item>
        <el-form-item >
          <div class="summaryInput" style="width: 100%">
            本周总结
            <el-input id="input1" type="textarea" 
                      :rows="10" maxlength="500" 
                      style="width: 100%" show-word-limit 
                      placeholder="请输入内容" v-model="temp.content"/>
          </div>
        </el-form-item>
        
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">返回</el-button>
        <el-button type="primary" :disabled='!this.reviewButton' @click="createData()">确认</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import summary from '@/api/intro/summary'
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
      summaryQuery:{}, //封装查询对象 
      student: {}, //登陆的学生
      dialogFormVisible: false, //弹框
      reviewButton: false, //弹筐里的确定键是否可点
      temp: {  //弹框数据
        stuId: '',
        summaryId: undefined,
        content: '',
        importance: 1,
      },
      toShowName: {}, //显示的名称
    }
  },
  created(){
      //获取列表
      this.getList()

  },
  methods: {
    //刷新列表
    getList(page=1){
      this.student = JSON.parse(cookie.get('intro_roles'))
      console.log("当前学生信息："+this.student.id);
      console.log(this.summaryQuery);
      this.page = page
      this.listLoading = true
      summary.findAllSummaryByStu(this.page,this.limit,this.student.id,this.summaryQuery)
      .then(response =>{
        this.list = response.data.stuSummaryPage
        console.log(this.list);
        this.toShowName = response.data.toShowName
        this.total = response.data.total
        this.summaryQuery = {}
      })
      .catch(error=>{
        console.log(error);
      })
      this.listLoading = false
    },

    //清空功能
    resetData() {
        this.summaryQuery = {}
        this.getList()
    },
    judge(date){
      var now = new Date()
      var past = new Date(date)
      return past<now
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

    //添加每周总结
    handleCreate(id) {
      this.resetTemp()
      console.log(id);
      this.temp.summaryId = id;
      this.dialogFormVisible = true
      this.reviewButton = true
      //TODO 查询总结标题
    },

    resetTemp() {
      this.temp = {
        summaryId: undefined,
        importance: 1,
        content: '',
      }
    },

    createData(){
      this.temp.stuId = this.student.id
      this.temp.content = "我给自己的评分为："+this.temp.importance+"。 "+this.temp.content
      summary.updateStuSummary(this.temp).then( ()=>{
          //下载成功
          this.$message({
              type: 'success',
              message: '添加成功!'
          });
          this.resetTemp()
          this.dialogFormVisible = false
          this.getList(this.page)
      })
    },
    //查看已完成的总结内容，不可更改
    toSee(id){
      console.log(id);
      this.dialogFormVisible = true
      this.reviewButton = false
      summary.findStuSummaryById(id).then(response =>{
        this.temp = response.data.stuSummaryInfo
        this.temp.importance = this.findImportance(this.temp.content)
      })
    },

    //回显时，找到当前总结自己的评分
    findImportance(content){
      for(var i = 0;i<content.length;i++){
        if(content.charAt(i)>'0' && content.charAt(i)<='5'){
          return parseInt(content.charAt(i));
        }
      }
      return 0;
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
.summaryInput /deep/ #input1 {
    min-height: 30px;
    margin: 0px;
    width: 450px;
}
</style>
