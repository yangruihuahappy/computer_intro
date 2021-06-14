<template>
  <div>
    <div v-if="flag">
      <code>
        本周已经添加过总结任务，请勿重复添加，如需更改请先删除原先总结，再进行添加！
      </code>
    </div>

    <div v-else>
      <code>
        添加总结时仅需要填写标题，截止日期默认为当前时间往后顺延七日！
      </code>

      <el-form ref="postForm" :model="summaryInfo" class="form-container">

        <el-form-item style="margin-bottom: 5px;" label-width="100px" label="标题:">
            <el-input :rows="1" v-model="summaryInfo.title" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
        </el-form-item>

        <template style="float:right;">
            <el-button type="primary" @click="saveSummary()">提交</el-button>
        </template>

      </el-form>

    </div>
  </div>
</template>

<script>

import summary from '@/api/intro/summary'

export default {
  data() {
    return{
        flag: false, //本周是否发布过总结
        summaryInfo: {}, //添加的总结信息
    }
  },
  created(){
      //先查询本周是否发布过总结
      this.initInfo()
  },
  methods: {
    initInfo(){
      //获取当前时间
      summary.judgePublish().then( response =>{
        this.flag = response.data.flag
      })
    },
    saveSummary(){
      this.summaryInfo.gmtCreate = new Date()
      summary.addSummary(this.summaryInfo).then( ()=>{
        this.$message({
            type: 'success',
            message: '添加成功!'
        });
        this.$router.push({ path:'/summary/index'})
      })
    }
  }

}
</script>

<style>

</style>