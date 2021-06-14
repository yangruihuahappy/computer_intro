<template>
  <el-table :data="list" border fit highlight-current-row style="width: 100%">

    <el-table-column
      v-loading="loading"
      align="center"
      label="ID"
      width="65"
      element-loading-text="请给我点时间！">
      <template slot-scope="scope">
        <span> {{ scope.$index + 1 }}</span>
      </template>
    </el-table-column>

    <el-table-column width="180px" align="center" label="截止时间">
      <template slot-scope="scope">
        <span>{{ scope.row.end }}</span>
      </template>
    </el-table-column>

    <el-table-column min-width="300px" label="练习标题">
      <template slot-scope="scope">
        <span >{{ scope.row.title }}</span>
        <el-tag :type="scope.row.remaining | statusFilter">{{ scope.row.title }}</el-tag>
      </template>
    </el-table-column>

    <el-table-column align="center" label="剩余天数" width="95">
      <template slot-scope="scope">
        <span>{{ scope.row.remaining }}</span>
      </template>
    </el-table-column>

    <el-table-column align="center" label="操作" width="120">
      <template slot-scope="scope">
        <router-link :to="'/practice/'+change(scope.row.type)">
            <el-button type="primary" size="small" icon="el-icon-edit">去完成</el-button>
          </router-link>
        <!-- <router-link :to="'/example/edit/'+scope.row.id">
          <el-button type="primary" size="small" icon="el-icon-edit">ToDo</el-button>
        </router-link> -->
      </template>
    </el-table-column>
    

  </el-table>
</template>

<script>
import urgent from '@/api/intro/schedule'
import cookie from 'js-cookie'

export default {
  filters: {
    statusFilter(remaining) {
      if(remaining>5){
        return 'success'
      }else if(remaining>3){
        return 'info'
      }else{
        return 'danger'
      }
    }
  },
  props: {
    type: {
      type: String,
      default: 'Pre'
    },
    
  },
  data() {
    return {
      list: null,
      student: {},
      listQuery: {
        stuId: "",
        type: this.type,
      },
      loading: false,
      id: 1,

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
        this.listQuery.stuId = this.student.id
        this.loading = true
        this.$emit('create') // for test
        urgent.getUrgentList(this.listQuery).then(response => {
          this.list = response.data.urgentList
          this.loading = false
        })
      }
    },
    change(type){
      return type.substring(0,1).toUpperCase()+type.substring(1);
    },
  }
}
</script>

