<template>
  
  <div>
     <!-- 前排展示题目信息 -->
    <div>
      <el-form :model="topicInfo">
        <el-form-item style="text-align:center;">
            <p>{{topicInfo.content}}</p>
            <p>提供者：{{topicInfo.provider}}&emsp;&emsp; 类型：{{judgeType(topicInfo.type)}}  &emsp;&emsp;</p>
            <p>章节：{{topicInfo.chapterId}}&emsp;{{topicInfo.sectionId}}</p>
            <p>提供选项：{{topicInfo.options}}</p>
            <p>拟定答案：{{topicInfo.answer}}</p>
            <p>拟定解析：{{topicInfo.explaining}}</p>
            <div v-if="admin" :inline="true">
              <el-button type="success" plain :disabled='topicInfo.adopted' @click="adoptedTopic">
                {{topicInfo.adopted==true?'已采纳':'采纳'}}
              </el-button>
            </div>
        </el-form-item>
      </el-form>
    </div>

    

    <div>
    <!-- 评论添加模块 -->
      <el-form>
        <el-form-item style="margin-bottom: 5px; margin-left: 100px; margin-right: 100px;" >
          <el-input :rows="4" v-model="comment.content" type="textarea" class="article-textarea" placeholder="请输入内容"/>
        </el-form-item>
        <template style="margin-bottom: 10px;">
            <el-button type="primary" @click="commitComment()" style="margin-right: 100px;float:right">评论</el-button>
        </template>
      </el-form>

    <!-- 评论展示模块 -->
      <div style="margin-top: 80px; margin-left: 100px; margin-right: 100px;">
        <div v-for="(item, index) in commentList" :key="index" style="margin-bottom: 40px;">
          <el-divider content-position="left">#{{item.sort}} —  {{item.stuId}}  —  {{formatDate(item.gmtCreate)}}</el-divider>
          <span style="margin-left: 100px;">{{item.content}}</span>
          <span style="float:right; margin-right: 50px;">
            <el-button type="primary" icon="el-icon-caret-top" size="mini" circle @click="praise(item.sort,item.id)"></el-button>
            {{item.praise}}
            <a @click="openReply(item.sort)">回复</a>
          </span>

          <!-- <el-form v-if="replyVisible">
            <el-form-item style="margin-bottom: 5px; margin-left: 100px; margin-right: 100px;" >
              <el-input :rows="4" v-model="comment.content" type="textarea" class="article-textarea" placeholder="请输入内容"/>
            </el-form-item>
            <template style="margin-bottom: 10px;">
                <el-button type="primary" @click="commitComment()" style="margin-right: 100px;float:right">评论</el-button>
            </template>
          </el-form> -->
        </div>
      </div>

      <!-- 修改/添加题目信息弹窗 -->
      <div>
          <!-- 弹出的做题框 -->
          <el-drawer
            title="评论"
            :visible.sync="replyVisible"
            :direction="direction"
            :before-close="handleClose">
         

            <div class="demo-drawer__content">
              <el-form :model="form">
                <el-form-item label="评论内容" :label-width="formLabelWidth">
                  <el-input v-model="form.content" :rows="2" type="textarea" autocomplete="off"></el-input>
                </el-form-item>
              </el-form>
              <div class="demo-drawer__footer" style="float:right; margin-right: 10px;">
                <el-button @click="cancelForm">取 消</el-button>
                <el-button type="primary" @click="beforeCommitComment(form.sort)" :loading="loading">{{ loading ? '提交中 ...' : '确 定' }}</el-button>
              </div>
            </div>

          </el-drawer>
        
          

      </div>
    </div>
  </div>
</template>

<script>

//时间格式
import * as dateUtils from "@/utils/date"
import topicApi from '@/api/intro/topic/topic'
import commentApi from '@/api/intro/topic/comment'
import cookie from 'js-cookie'

export default {
  data() {
    return {
      topicId: "",
      topicInfo: {}, // 所属课程
      comment: {}, //输入框里的评论内容
      student: {}, //登陆的学生信息
      commentList: [], //评论列表
      isPrise: [], //点赞
      commentId: "", //点赞的评论
      replyVisible: false, //回复窗口
      direction: 'rtl', //回复窗口打开方向
      loading: false,
      form: {
        content: '',
        sort: 0,
      },
      formLabelWidth: '80px',
      admin: false, //是否是管理员
    }
  },
  created(){
    if(this.$route.params && this.$route.params.id){
      console.log("当前url有课程id");
      this.topicId = this.$route.params.id
    }
    this.initInfo(this.topicId)
    //获取登陆的学生信息
    this.admin = (cookie.get('intro_admin') === "true")
    
    this.student = JSON.parse(cookie.get('intro_roles'))
   
  },
  template:"#temp",
  methods: {
    //初始化信息
    initInfo(id){
      topicApi.getTopicByTopicId(id).then( response=>{
        this.topicInfo = response.data.topicInfo
        this.topicInfo.provider = response.data.provider
        commentApi.findCommentList(id).then( response=>{
          this.commentList = response.data.commentList
          console.log(this.commentList);
        })
        
      })
      
    },
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
    //提交评论
    beforeCommitComment(sort){
      var commentContent = `回复第#${sort}层 ：`+this.form.content
      this.comment.content = commentContent
      this.commitComment()
     
    },
    commitComment(){
      if(this.comment.content!=null){
        //先修改当前的comment信息
        this.comment.stuId = this.student.id
        this.comment.topicId = this.topicId
        console.log(this.comment);
        commentApi.addComment(this.comment).then( () =>{
          this.$message({
            type: 'success',
            message: '评论成功!'
          });
          this.resetComment()
        })
      }else{
         this.$message({
            type: 'danger',
            message: '请输入内容!'
          });
      }
      
    },
    //重置评论
    resetComment(){
      this.comment = {};
      this.form = {};
      this.replyVisible = false;
    },

    //给评论点赞
    praise(index,id){
      if(this.isPrise[index] == true){ //点过赞了
        this.$message({
          type: 'danger',
          message: '该评论已经点过赞了!'
        });
      }else{
        var commentInfo = {}
        commentInfo.id = id
        this.isPrise[index] = true
        commentApi.praiseComment(commentInfo).then( () =>{
        this.$message({
          type: 'success',
          message: '点赞成功!'
        });
      })
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

    openReply(sort){
      this.replyVisible = true
      this.form.sort = sort
    },
    //关闭弹窗
    handleClose(done) {
      if (this.loading) {
        return;
      }
      this.$confirm('确定要提交表单吗？')
        .then(_ => {
          this.loading = true;
          this.timer = setTimeout(() => {
            done();
            // 动画关闭需要一定的时间
            setTimeout(() => {
              this.loading = false;
            }, 400);
          }, 2000);
        })
        .catch(_ => {});
    },
    cancelForm() {
      this.loading = false;
      this.replyVisible = false;
    },
    adoptedTopic(){
      this.topicInfo.adopted = true
      topicApi.addTopic(this.topicInfo).then( ()=>{
        this.$message({
          type: 'success',
          message: '收藏成功!'
        });
        this.initInfo(this.topicInfo.id)
      })
    }
 
  }

}
</script>

<style>

</style>