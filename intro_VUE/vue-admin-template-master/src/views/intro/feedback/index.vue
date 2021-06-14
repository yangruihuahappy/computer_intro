<template>
  <div class="app-container">
    <!-- 顶部描述 -->
    <div v-if="!admin">
      <div>
        <code>
          针对这门课程或本网站你有任何意见或者建议，欢迎随时通过意见反馈模块，我们也会在收到消息的第一时间对您的意见做出回应
        </code>
      </div>
      
      <div style="margin-bottom: 20px;">
        <el-input :rows="6" type="textarea" v-model="feedbackInfo.content" placeholder="请填写反馈意见"></el-input>
      </div>
      
      <div style="margin-bottom: 50px;">
        <el-switch
          v-model="feedbackInfo.anonymous"
          active-text="匿名反馈"
          inactive-text="">
        </el-switch>

        <el-button type="primary" icon="el-icon" @click="publish()">发表</el-button>
      </div>
      <div slot="footer">
        <code>
          此外你还可以通过以下方式和我们取得联系<br>
          email： te7719248@qq.com
        </code>
      </div>
    </div>
    <div v-else class="show">
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
                width="80"
                align="center">
                <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>
            
            <el-table-column prop="content" label="反馈意见" align="center"/>
            <el-table-column label="提供者" align="center" width="160">
              <template slot-scope="scope">
                    {{nameList[scope.$index]}}
                </template> 
            </el-table-column>
            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    {{formatDate(scope.row.gmtCreate)}}
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
  </div>
</template>
<script>

import cookie from 'js-cookie'
import * as dateUtils from "@/utils/date"
import feedback from '@/api/intro/feedback'

export default {
  data(){
    return {
      page:1, //当前页
      limit:10, //每页记录数
      total:0, //总记录数
      listLoading: true, // 是否显示loading信息
      student: {}, //登陆的学生
      teacher: {}, //也有可能是教师
      feedbackInfo:{}, //反馈内容
      list: null, //教师端展示用到
      admin: false, //是否是管理员 
      nameList: {},//用来显示姓名
    }
  },
  created(){
    this.admin = (cookie.get('intro_admin') === "true")
    if(this.admin){
        this.teacher = JSON.parse(cookie.get('intro_roles')) //初始化登陆的教师信息
        this.getList()
    }else{
        this.student = JSON.parse(cookie.get('intro_roles')) //初始化登陆的学生信息
    }
  },
  methods:{
    //查询集合
    getList(page=1){
        this.page = page
        this.listLoading = true
        feedback.getFeedbackList(this.page,this.limit)
        .then(response =>{
            this.list = response.data.feedbackList
            this.total = response.data.total
            this.nameList = response.data.nameList
        })
        .catch(error=>{
            console.log(error);
        })
        this.listLoading = false
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

    //发布反馈意见
    publish(){
      this.feedbackInfo.stuId = this.student.id
      console.log(this.feedbackInfo);
      feedback.publishFeedback(this.feedbackInfo).then(
        ()=>{
          //添加
          this.$message({
              type: 'success',
              message: '感谢您的反馈!'
          });
          //清空
          this.resetFeedback()
        }
      )

    },
    //清空
    resetFeedback(){
      this.feedbackInfo.content = '',
      this.feedbackInfo.anonymous = false;
    }
  }

}
</script>

<style>
  .app-container {
    padding: 30px;
    position: relative;
    height: 100vh;
  }
</style>