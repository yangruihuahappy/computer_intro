<template>
  <div>
    
    <!-- 表格 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      stripe
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

        <el-table-column label="所有题目"  align="center">
          <template slot-scope="scope">
            <a @click="toShowTopic(scope.row.id)">{{scope.row.content}}</a>
          </template>
        </el-table-column> 


        <el-table-column label="题目类型" width="80">
            <template slot-scope="scope">
              {{judgeType(scope.row.type)}}
            </template>
        </el-table-column>

        <el-table-column label="所属章节"  width="180">
          <template slot-scope="scope">
              {{chapterNameList[scope.$index]}}
            </template>
        </el-table-column>

        <el-table-column label="所属小节" width="180">
        <template slot-scope="scope">
              {{sectionNameList[scope.$index]}}
            </template>
        </el-table-column>

        <el-table-column prop="commentCount" label="讨论数量" width="80" />
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
</template>

<script>

//引入题目topic.js
import topic from '@/api/intro/topic/topic'
//时间格式
import { formatDate } from '@/utils/date'

export default {

  data(){
    return{
      listLoading: true, 
      list:null, //查询之后接口返回集合
      page:1, //当前页
      limit:10, //每页记录数
      total:0, //总记录数
      topicQuery:{}, //条件封装对象
      chapterNameList: {},
      sectionNameList: {},
    }
  },
  created(){
    //调用
      this.getList()
  },
  
  filters: {
    formatDate(time) {
      var date = new Date(time);
      return formatDate(date, 'yyyy-MM-dd');
    }
  },
  
  methods:{
    //判断题型
    judgeType(type){
      if(type == 0){
        return "单选题"
      }else if(type == 1){
        return "多选题"
      }else{
        return "简答题"
      }
    },
    getList(page=1){
      this.listLoading = false
      this.page = page
      topic.getTopicPageList(this.page,this.limit)
        .then(response =>{ //请求成功
          this.list = response.data.rows
          this.total = response.data.total
          this.chapterNameList = response.data.chapterNameList
          this.sectionNameList = response.data.sectionNameList
          console.log(this.chapterNameList);
        })
        .catch(error=>{ //请求失败
          console.log(error);
        }) 
    },
    toShowTopic(topicId){
      //打开当前的题目查看
      console.log(topicId);
      this.$router.push({ path:'/topic/showTopic/'+ topicId})
    }

  }

}
</script>

<style>

</style>