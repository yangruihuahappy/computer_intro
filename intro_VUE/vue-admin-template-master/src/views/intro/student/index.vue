<template>
    <div class="app-container">
        <!-- 上部分查询表栏-->
        <div class="show" style="margin-top: 10px;" >
            <el-form :inline="true" class="demo-form-inline" label-width="800px">
                <!-- 学号 -->
                <el-form-item>
                    <el-input v-model="studentQuery.stuNum" placeholder="学生学号"/> 
                </el-form-item>
                <!-- 姓名 -->
                <el-form-item>
                    <el-input v-model="studentQuery.name" placeholder="学生姓名"/> 
                </el-form-item>
                <!-- 操作 -->
                <el-form-item>
                <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
                <el-button type="default" @click="resetData()">清空</el-button>
                <el-button type="default" @click="toAddStudent()">单个添加学生</el-button>
                <el-button type="default" @click="toAddStudentBatch()">批量添加学生</el-button>
                </el-form-item>

                <el-form-item label-width="150px" label="当前学生人数：" prop="pass" style="margin-left: 10px;">
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

                <el-table-column prop="stuNum" label="学号" align="center"/> 
                
                <el-table-column prop="name" label="姓名" align="center" width="100"/>
                
                <el-table-column prop="grade" label="年级" align="center" width="80"/>

                <el-table-column prop="classes" label="班级" align="center" width="80"/>

                <el-table-column prop="groupId" label="小组" align="center" width="80"/>

                <el-table-column prop="email" label="邮箱" align="center" width="150"/>

                <el-table-column prop="telephone" label="联系方式" align="center" width="120"/>

                <el-table-column prop="groupId" label="小组" align="center" width="60"/>

                <el-table-column :show-overflow-tooltip = "true" prop="skill" label="学生技能" align="center" width="150"/>

                <el-table-column label="创建时间" width="160" align="center">
                    <template slot-scope="scope">
                        {{formatDate(scope.row.gmtCreate)}}
                    </template> 
                </el-table-column>

                <el-table-column label="更新时间" width="160" align="center">
                    <template slot-scope="scope">
                        {{formatDate(scope.row.gmtModified)}}
                    </template> 
                </el-table-column>

                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <el-button-group>
                        <el-button type="success" size="mini" icon="el-icon-search" @click="toShowStudent(scope.row.id)"></el-button>
                        <el-button type="primary" size="mini" icon="el-icon-edit" @click="toEditStudent(scope.row.id)"></el-button>
                        <el-button type="danger" size="mini" icon="el-icon-delete" @click="deleteStudent(scope.row.id)"></el-button>
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

        <!-- 查看学生信息以及编辑和新建 -->
        <div>
            <!-- 弹出的界面框 -->
            <el-dialog 
            :title="'添加/编辑/查看 学生信息'" 
            :visible.sync="dialogFormVisible"  
            :before-close="handleClose">

                <!-- 如果是查看学生信息 -->
                <div v-if="isShow">
                    <el-card>
                        <div>
                            <!-- <el-image
                            style="width: 100px; height: 100px"
                            :src="studentInfo.avatar"
                            :fit="fit"></el-image> -->
                            <h1>{{studentInfo.name}}</h1>
                            <p>学号：{{studentInfo.stuNum}}</p>
                            <p>{{studentInfo.grade}}级&nbsp;&nbsp;&nbsp;{{studentInfo.classes}}班</p>
                            <p>所在小组：{{studentInfo.groupId}}</p>
                            <p>email：{{studentInfo.email}}</p>
                            <p>联系方式：{{studentInfo.telephone}}</p>
                            <p>个人介绍：</p>
                            <p>{{studentInfo.skill}}</p>
                            <p>添加时间: {{formatDate(studentInfo.gmtCreate)}}</p>
                            <p>上次更新时间：{{formatDate(studentInfo.gmtModified)}}</p>
                        </div>
                    </el-card>
                </div>

                <!-- 如果是编辑学生信息 -->
                <div v-if="isEdit">
                    <el-form ref="postForm" :model="studentInfo" class="form-container">
                        <el-form-item style="margin-bottom: 5px;" label-width="45px" label="姓名:">
                            <el-input :rows="1" v-model="studentInfo.name" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="45px" label="学号:">
                            <el-input :rows="1" v-model="studentInfo.stuNum" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="90px" label="年级班级:">
                            <el-input :rows="1" v-model="studentInfo.grade" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                            <el-input :rows="1" v-model="studentInfo.classes" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="90px" label="所在小组:">
                            <el-input :rows="1" v-model="studentInfo.groupId" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="90px" label="email:">
                            <el-input :rows="1"  v-model="studentInfo.email" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="90px" label="联系方式:">
                            <el-input :rows="1"  v-model="studentInfo.telephone" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="90px" label="个人技能:">
                            <el-input :rows="2"  v-model="studentInfo.skill" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>

                        <template #footer style="margin-bottom: 10px;">
                            <span class="dialog-footer">
                            <el-button @click="dialogFormVisible = false">返回</el-button>
                            <el-button type="primary" @click="toUpdateInfo()">提交</el-button>
                            </span>
                        </template>
                    </el-form>
                </div>

                <!-- 如果是添加学生信息 -->
                <div v-if="isAdd">
                    <el-form ref="postForm" :model="studentInfo" class="form-container">
                        <el-form-item style="margin-bottom: 5px;" label-width="50px" label="姓名:">
                            <el-input :rows="1" v-model="studentInfo.name" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="50px" label="学号:">
                            <el-input :rows="1" v-model="studentInfo.stuNum" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="50px" label="年级:">
                            <el-input :rows="1" v-model="studentInfo.grade" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="50px" label="班级:">
                            <el-input :rows="1" v-model="studentInfo.classes" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                        <el-form-item style="margin-bottom: 5px;" label-width="50px" label="小组:">
                            <el-input :rows="1" v-model="studentInfo.groupId" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        </el-form-item>
                       
                        <template #footer style="margin-bottom: 10px;">
                            <span class="dialog-footer">
                            <el-button @click="dialogFormVisible = false">返回</el-button>
                            <el-button type="primary" @click="toAddInfo()">提交</el-button>
                            </span>
                        </template>
                    </el-form>
                </div>

                <!-- 如果是批量上传 -->
                <div v-if="isBatch">
                    <el-form ref="postForm" :model="studentInfo" class="form-container">
                        <el-button size="small" type="info" style="margin-bottom: 10px;" @click="downloadBaseExcel">下载模板</el-button>
                        <el-tooltip placement="right-end">
                            <div slot="content">
                                请根据模板提示设计表头，格式不对将会导致批量添加失败！
                            </div>
                            <i class="el-icon-warning"/>
                        </el-tooltip>

                        <el-upload
                            :on-success="handleUploadSuccess"
                            :on-remove="handleRemove"
                            :before-remove="beforeRemove"
                            :on-exceed="handleUploadExceed"
                            :file-list="fileList"
                            :action="BASE_API+'/introService/student/addStuBatch'"
                            :limit="1"
                            class="upload-demo">
                            <el-button size="small" type="primary" style="margin-bottom: 10px;">上传文件</el-button>
                            <el-tooltip placement="right-end">
                                <div slot="content">
                                    最大支持32MB，仅支持excel格式上传
                                </div>
                                <i class="el-icon-question"/>
                            </el-tooltip>
                        </el-upload>
                        <template #footer style="margin-bottom: 10px;">
                            <span class="dialog-footer">
                                <el-button @click="dialoguploadFileVisible = false">取 消</el-button>
                                <el-button :disabled="saveFileBtnDisabled" type="primary" @click="addStudentBatch">确 定</el-button>
                            </span>
                        </template>
                    </el-form>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>

import * as dateUtils from "@/utils/date"
import ImageCropper from '@/components/ImageCropper'
import studentApi from '@/api/intro/student'
import cookie from 'js-cookie'
import oss from '@/api/oss/fileOss'

export default {
    components: { ImageCropper },
    data(){
        return {
            page:1, //当前页
            limit:10, //每页记录数
            total:0, //总记录数
            listLoading: true, // 是否显示loading信息
            studentQuery: {}, //学生查询实体
            list: null, //展示的学生列表
            teacher: {}, //登陆的教师
            studentInfo: {}, //用于操作的学生信息
            dialogFormVisible: false, //弹框是否可视
            isShow: false, //是否是查看信息
            isEdit: false, //是否是编辑信息
            isAdd: false, //是否是添加信息
            isBatch: false, //是否是批量添加
            BASE_API: process.env.BASE_API, // 接口API地址
            fileList: [],//上传文件列表
            saveFileBtnDisabled: true, // 上传文件保存按钮
        }
    },
    created(){
        //获取列表
        this.teacher = JSON.parse(cookie.get('intro_roles'))
        this.getList()
    },
    methods: {
        //刷新列表
        getList(page=1){
            this.page = page
            this.listLoading = true
            console.log(this.studentQuery);
            studentApi.getStudentList(this.page,this.limit,this.studentQuery)
            .then(response =>{
                this.list = response.data.studentList
                this.total = response.data.total
            })
            .catch(error=>{
                console.log(error);
            })
            this.listLoading = false
        },
        handleClose(){
            this.dialogFormVisible = false
            this.isAdd = false
            this.isEdit = false
            this.isShow = false
            this.isBatch = false
            this.studentInfo = {}
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

        //添加学生信息
        toAddStudent(){
            this.isAdd = true
            this.dialogFormVisible = true
        },
        toAddInfo(){
            console.log(this.studentList);
            studentApi.saveStudent(this.studentInfo).then( ()=>{
                this.$message({
                    type: 'success',
                    message: '添加成功!'
                });
                this.handleClose()
                this.getList()
            }).catch(_ => {
                console.log("取消添加");
            });
        },

        //批量添加学生
        toAddStudentBatch(){
            this.isBatch = true
            this.dialogFormVisible = true
        },
        addStudentBatch(){
            this.$message({
                type: 'success',
                message: '添加成功!'
            });
            this.handleClose()
            this.getList()
        },

        //查看学生信息
        toShowStudent(id){
            this.isShow = true
            this.dialogFormVisible = true
            studentApi.findInfoByStuId(id).then( response=>{
                this.studentInfo = response.data.stuInfo
            })
        },

        //编辑学生信息
        toEditStudent(id){
            this.isEdit = true
            this.dialogFormVisible = true
            studentApi.findInfoByStuId(id).then( response=>{
                this.studentInfo = response.data.stuInfo
            })
        },
        toUpdateInfo(){
            studentApi.updateInfo(this.studentInfo).then( () =>{
                this.$message({
                    type: 'success',
                    message: '修改成功!'
                });
                this.handleClose()
            }).catch(_ => {
                console.log("取消修改");
            })
        },

        //删除学生
        deleteStudent(id){
            console.log(id);
            this.$confirm('确认删除该学生信息？同时该学生的所有信息也会被删除',{
                confirmButtonText: '确定',
                cancelButtonText: '取消',})
            .then(_ => {
                //确认删除
                studentApi.removeStudent(id).then( ()=>{
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
        //===========================上传文件的方法==========================
        handleUploadSuccess(response, file, fileList){  //提交成功之后
            console.log("===============这里是上传文件操作=================");
            console.log(response.data.url);
            
            
            this.saveFileBtnDisabled = false;
        },
        handleUploadExceed(files, fileList){
            this.$message.warning('想要重新上传文件，请先删除已上传的文件')
        },
        //确认删除
        handleRemove(file, fileList) {
            console.log(file)
            oss.removeOssFile(this.stuExperiment.ossUrl).then(response=>{
               
                //清空文件列表
                this.fileList = []
                this.$message({
                    type: 'success',
                    message: response.message
                })
                this.saveFileBtnDisabled = true;
            })
        },
        //确认删除提示
        beforeRemove(file, fileList){
            return this.$confirm(`确定移除 ${file.name}？`)
        },


        //下载excel模板
        downloadBaseExcel(){

        },


    }

}
</script>

<style>

</style>