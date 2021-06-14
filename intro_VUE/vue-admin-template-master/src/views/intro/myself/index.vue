<template>
  <div class="dashboard-editor-container">

    <el-row :gutter="8">
      
      <box-card/>
    </el-row>

  </div>
</template>

<script>
import BoxCard from './components/BoxCard'

export default {
  name: 'Myself',
  components: {
    BoxCard,
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
