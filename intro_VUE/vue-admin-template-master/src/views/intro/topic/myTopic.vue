<template>
  <div >
    <!-- 上部分查询表栏-->
    <div class="show" style="margin-top: 10px;" >
        <el-form :inline="true" class="demo-form-inline" label-width="800px">
            
                <el-form-item label-width="150px" label="我发布的题目数：" prop="pass" style="margin-left: 10px; float:left">
                        {{this.total}}
                </el-form-item>
                <el-form-item>
                    <el-button style="margin-right: 20px; float:right" type="default" @click="saveOrUpdate()" >我来出题</el-button>
                
                </el-form-item>
        </el-form>
    </div>
    
    <!-- 表格 -->
    <div style="margin-bottom: 10px; margin-top: 0px;">
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
                width="80"
                align="center">
                <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>

            <el-table-column label="我的题目"  align="center">
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

            <el-table-column label="创建时间" width="180" align="center">
                <template slot-scope="scope">
                {{formatDate(scope.row.gmtCreate)}}
                </template> 
            </el-table-column>
            <el-table-column prop="commentCount" label="讨论数量" width="80" />
            <el-table-column label="操作" width="120" align="center">
                <template slot-scope="scope">
                    <el-button-group>
                        <el-button type="primary" size="mini" icon="el-icon-edit" @click="saveOrUpdate(scope.row.id)"></el-button>
                        <el-button type="danger" size="mini" icon="el-icon-delete" @click="deleteTopic(scope.row.id)"></el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table> 
            <!-- 分页
        <el-pagination
        background
        :current-page="page"
        :page-size="limit"
        :total="total"
        style="padding: 30px 0; text-align: center;"
        layout="total, prev, pager, next, jumper"
        @current-change="getList"
        /> -->
    </div>

    <!-- 修改/添加题目信息弹窗 -->
    <div>
        <!-- 弹出的做题框 -->
        <el-dialog 
        :title="'添加题目信息'" 
        :visible.sync="dialogFormVisible"  
        :before-close="handleClose">
            <el-form ref="postForm" :model="topicInfo" class="form-container">

                <!-- 所属章节：级联下拉列表 -->
                <el-form-item style="margin-bottom: 5px;" label-width="200px" label="修改题目时不能修改章节信息!" v-if="updateInfo"/>
                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="选择章节:" v-if="!updateInfo">
                    <!-- 一级分类 章 -->
                    <el-select
                    v-model="topicInfo.chapterId"
                    placeholder="选择章"
                    @change="LevelOneChanged"
                    >
                    <el-option
                        v-for="chapter in chapterOneList"
                        :key="chapter.id"
                        :label="chapter.title"
                        :value="chapter.id"/>
                    </el-select>
                    <!-- 二级分类 节 -->
                    <el-select 
                        v-model="topicInfo.sectionId" 
                        placeholder="选择节">
                        <el-option
                            v-for="section in sectionTwoList"
                            :key="section.id"
                            :label="section.title"
                            :value="section.id"/>
                    </el-select>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="内容:">
                    <el-input :rows="2" v-model="topicInfo.content" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="题目类型">
                    <el-radio-group v-model="topicInfo.type">
                        <el-radio v-for="item in topicTypeList" :label="item.value" :key="item.value">
                            {{item.label}}
                        </el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="选项:">
                    <el-input :rows="2" v-model="topicInfo.options" type="textarea" class="article-textarea" autosize placeholder="添加题目选项"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="答案:">
                    <el-input :rows="1" v-model="topicInfo.answer" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="解析:">
                    <el-input :rows="1" v-model="topicInfo.explaining" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                </el-form-item>
            

                <template #footer style="margin-bottom: 10px;">
                    <span class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">返回</el-button>
                    <el-button type="primary" @click="saveTopic()">提交</el-button>
                    </span>
                </template>

            </el-form>
        </el-dialog>
    </div>

  
  </div>
</template>

<script>

//引入题目topic.js
import topic from '@/api/intro/topic/topic'
import chapter from '@/api/intro/chapter'
//时间格式
import * as dateUtils from "@/utils/date"
import cookie from 'js-cookie'

export default {

    data(){
        return{
            listLoading: true, 
            list:null, //查询之后接口返回集合
            page:1, //当前页
            limit:10, //每页记录数
            total:0, //总记录数
            topicQuery: {}, //条件封装对象
            student: {}, //登陆的学生
            dialogFormVisible: false, //弹窗是否可视化
            updateInfo: false, //是否修改
            topicInfo: {}, //弹窗里面的题目信息
            chapterOneList:{}, //一级章节目录
            sectionTwoList:{}, //二级小节目录
            topicTypeList: [
                {'label': '单选题', 'value': 0},
                {'label': '多选题', 'value': 1},
                {'label': '填空题', 'value': 2},
            ],
            chapterNameList: {}, //章节名称
            sectionNameList: {}, //小节名称
        }
    },
    created(){
        //先获取登陆的学生信息
        this.student = JSON.parse(cookie.get('intro_roles'))
        //调用
        this.getList()
        this.initChapterOneList()
    },
    
    methods:{
        //打开弹窗判断是更新还是新建
        saveOrUpdate(topicId){
            //打开弹窗
            this.dialogFormVisible = true
             //判断更新还是保存 进行数据回显
            if(topicId != null){ //进行数据回显
                this.updateInfo = true
                topic.getTopicByTopicId(topicId).then( response=>{
                    this.topicInfo = response.data.topicInfo
                    console.log(this.topicInfo);
                })
            }
        },
        //提交更新或新建
        saveOrUpdateInfo(topicId){
            //判断更新还是保存
            this.saveTopic()
        },

        //新建
        saveTopic(){
            this.topicInfo.providerId = this.student.id
            console.log(this.topicInfo);
            topic.addTopic(this.topicInfo).then( () =>{
                this.$message({
                    type: 'success',
                    message: '提交成功!'
                });
                this.resetTopicInfo()
                this.dialogFormVisible = false
            })
        },
        //删除
        deleteTopic(topicId){
            //删除时先确认用户是否删除，确认后再删除
            this.$confirm('确认删除该题目？改题目下的评论也会被删除',{
                confirmButtonText: '确定',
                cancelButtonText: '取消',})
            .then(_ => {
                //确认删除
                console.log("确认删除"+topicId+"?");
                topic.removeTopicById(topicId).then( ()=>{
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
        //清空并刷新页面
        resetTopicInfo(){
            this.topicInfo = {}
            this.getList()
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
        //初始化一级章节目录
        initChapterOneList(){
            chapter.getChapterList().then(response =>{
                this.chapterOneList = response.data.chapterList
                console.log();
            })
        },
    
        //一级目录更换
        LevelOneChanged(value){
            for(let i=0;i<this.chapterOneList.length; i++){
                if (this.chapterOneList[i].id === value) {
                    this.sectionTwoList = this.chapterOneList[i].children
                    // this.PreviewQuery.sectionId = '' //把二级分类清空
                }
            }
        },

        //初始化列表
        getList(page=1){
        this.listLoading = false
        this.page = page
        topic.getTopicListByProId(this.student.id)
            .then(response =>{ //请求成功
            this.list = response.data.topicList
            this.total = response.data.total
            this.chapterNameList = response.data.chapterNameList
            this.sectionNameList = response.data.sectionNameList
            })
            .catch(error=>{ //请求失败
            console.log(error);
            }) 
        },
        toShowTopic(topicId){
            console.log("到这里了吗"+topicId);
            this.$router.push({ path:'/topic/showTopic/'+ topicId})
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
        //关闭弹窗
        handleClose(){
            this.dialogFormVisible = false
            this.updateInfo = false
            this.topicInfo = {}
        },

    }

}
</script>

<style>

</style>    