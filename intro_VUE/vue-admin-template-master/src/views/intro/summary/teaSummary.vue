<template>
  <div class="app-container">
    <!-- 上部分查询表栏-->
    <div class="show" style="margin-top: 10px;" >
        <el-form :inline="true" class="demo-form-inline" label-width="800px">
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

            <el-form-item label-width="150px" label="已发布的每周总结：" prop="pass" style="margin-left: 10px;">
                    {{this.total}}
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

            <el-table-column prop="title" label="练习标题" align="center"/> 
            
            <el-table-column prop="description" label="总结描述" align="center" width="160"/>

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
                      <el-button type="success" size="mini" icon="el-icon-search" @click="toShowSummary(scope.row.id)"></el-button>
                      <el-button type="danger" size="mini" icon="el-icon-delete" @click="deleteSummary(scope.row.id)"></el-button>
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
  </div>
</template>

<script>

import summary from '@/api/intro/summary'
import * as dateUtils from "@/utils/date"
import cookie from 'js-cookie'

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
            summaryQuery:{}, //封装查询对象 
        }
    },
  created(){
      //获取列表
      this.getList()
  },
  methods: {
   
    //刷新列表
    getList(page=1){
        this.teacher = JSON.parse(cookie.get('intro_roles'))
        this.page = page
        this.listLoading = true
        summary.findSummaryPage(this.page,this.limit,this.summaryQuery)
        .then(response =>{
            this.list = response.data.summaryPage
            this.total = response.data.total
            this.completion = response.data.completion
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

    // 跳转到某一课前预习下
    toShowSummary(id){
      console.log("查看跳转的课前预习"+id);
      this.$router.push({ path:'/summary/showSummary/'+id})
    },

    deleteSummary(id){
      console.log(id);
      this.$confirm('确认删除该每周总结？同时学生端的本次总结也会被删除',{
                confirmButtonText: '确定',
                cancelButtonText: '取消',})
      .then(_ => {
          //确认删除
        summary.deleteSummaryById(id).then( ()=>{
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
        this.previewInfo = {}
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
