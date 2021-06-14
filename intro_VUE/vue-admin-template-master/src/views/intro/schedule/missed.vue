<template>
  <div class="app-container">

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="ID" width="80">
        <template slot-scope="scope">
          <span> {{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column min-width="300px" label="Title">
        <template slot-scope="scope">
          <router-link :to="'/example/edit/'+scope.row.id" class="link-type">
            <span>{{ scope.row.title }}</span>
          </router-link>
        </template>
      </el-table-column>

      <el-table-column width="180px" align="center" label="创建时间">
        <template slot-scope="scope">
          <span>{{ scope.row.begin}}</span>
        </template>
      </el-table-column>

      <el-table-column class-name="status-col" label="分类" width="110">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status | statusFilter">{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>



      <el-table-column width="180px" align="center" label="截止时间">
        <template slot-scope="scope">
          <span>{{ scope.row.end}}</span>
        </template>
      </el-table-column>


    </el-table>

    <!-- <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" /> -->

  </div>
</template>

<script>


import schedule from '@/api/intro/schedule'
import cookie from 'js-cookie'

export default {
  name: 'ToDoList',
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20
      },
      student:{
        // id:"1376870881904656385", //先用默认学生 改好登陆之后再用学生信息
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      //从cookie获取用户信息
      var userStr = cookie.get('intro_roles')
      console.log("userStr:====="+userStr);
      if(userStr){
        this.student = JSON.parse(userStr)
        this.listLoading = true
        console.log(this.student);
        schedule.getMissedList(this.student)
        .then(response =>{
          this.list = response.data.MissedList
          console.log(this.list);
          this.listLoading = false
        })
      }
    },

    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },

    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    judge(date){
      return false;
    },
  }
}
</script>

<style scoped>
.edit-input {
  padding-right: 100px;
}
.cancel-btn {
  position: absolute;
  right: 15px;
  top: 10px;
}
</style>
