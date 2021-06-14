<template>
  <div class="dashboard-editor-container">

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="16">
        <all-list/>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <el-row :gutter="10">
            <pie-chart/>
        </el-row>
        <el-row :gutter="10">
            <hot-list/>
        </el-row>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import AllList from './components/AllList'
import PieChart from './components/PieChart'
import HotList from './components/HotList'

export default {
  name: 'Topic',
  components: {
    AllList,
    PieChart,
    HotList,
  },
  data() {
    return {
      
    }
  },
  methods: {
    
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}
</style>
