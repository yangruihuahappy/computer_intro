<template>
    <div>
        <el-card>
            <div v-if="!admin">
                <h1>{{student.name}}</h1>
                <p>学号：{{student.stuNum}}</p>
                <p>{{student.grade}}级&nbsp;&nbsp;&nbsp;{{student.classes}}班</p>
                <p>所在小组：{{student.groupId}}</p>
                <p>email：{{student.email}}</p>
                <p>联系方式：{{student.telephone}}</p>
                <p>个人介绍：</p>
                <p>{{student.skill}}</p>
                <p>上次更新时间：{{formatDate(student.gmtModified)}}</p>
                <el-button plain icon="el-icon-edit" @click="toEdit()">去编辑</el-button>
                <el-button plain icon="el-icon-edit" @click="toEditPassword()">改密码</el-button>
                <el-button plain icon="el-icon-edit" @click="imagecropperShow=true">换头像</el-button>
            </div>
            <div v-else>
                <h1>{{teacher.name}}</h1>
                <p>教职工号：{{teacher.username}}</p>
                <p>email：{{teacher.email}}</p>
                <p>个人介绍：</p>
                <p>{{teacher.intro}}</p>
                <p>上次更新时间：{{formatDate(teacher.gmtModified)}}</p>
                <el-button plain icon="el-icon-edit" @click="toEdit()">去编辑</el-button>
                <el-button plain icon="el-icon-edit" @click="toEditPassword()">改密码</el-button>
                <el-button plain icon="el-icon-edit" @click="imagecropperShow=true">换头像</el-button>
            </div>
        </el-card>
        <div>
        <!-- 修改信息弹窗 -->
            <div v-if="!admin">
                <!-- 弹出的做题框 -->
                <el-dialog 
                :title="'修改个人信息'" 
                :visible.sync="dialogFormVisible"  
                :before-close="handleClose">
                <el-form ref="postForm" :model="student" class="form-container">
                    <el-form-item style="margin-bottom: 5px;" label-width="45px" label="姓名:">
                        <el-input :rows="1" :disabled="true" v-model="student.name" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="45px" label="学号:">
                        <el-input :rows="1" :disabled="true" v-model="student.stuNum" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="年级班级:">
                        <el-input :rows="1" :disabled="true" v-model="student.grade" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                        <el-input :rows="1" :disabled="true" v-model="student.classes" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="所在小组:">
                        <el-input :rows="1" :disabled="true" v-model="student.groupId" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="email:">
                        <el-input :rows="1"  v-model="student.email" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="联系方式:">
                        <el-input :rows="1"  v-model="student.telephone" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="个人技能:">
                        <el-input :rows="2"  v-model="student.skill" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>

                    <template #footer style="margin-bottom: 10px;">
                        <span class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">返回</el-button>
                        <el-button type="primary" @click="toUpdateInfo()">提交</el-button>
                        </span>
                    </template>
                    </el-form>
                </el-dialog>
            </div>
            <div v-else>
                <!-- 弹出的修改框 -->
                <el-dialog 
                :title="'修改个人信息'" 
                :visible.sync="dialogFormVisible"  
                :before-close="handleClose">
                <el-form ref="postForm" :model="teacher" class="form-container">
                    <el-form-item style="margin-bottom: 5px;" label-width="45px" label="姓名:">
                        <el-input :rows="1" v-model="teacher.name" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="45px" label="教职工号:">
                        <el-input :rows="1" v-model="teacher.username" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="email:">
                        <el-input :rows="1"  v-model="teacher.email" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="个人介绍:">
                        <el-input :rows="2"  v-model="teacher.intro" type="textarea" class="article-textarea" autosize placeholder="请输入内容"/>
                    </el-form-item>

                    <template #footer style="margin-bottom: 10px;">
                        <span class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">返回</el-button>
                        <el-button type="primary" @click="toUpdateInfo()">提交</el-button>
                        </span>
                    </template>
                    </el-form>
                </el-dialog>
            </div>
        </div>
        <!-- 修改密码弹窗 -->
        <div>
            <!-- 弹出的修改密码框 -->
            <el-dialog 
            :title="'修改密码'" 
            :visible.sync="dialogPasswordVisible"  
            :before-close="handleClose">
             <el-form :model="ruleForm" status-icon :rules="rules" :ref="ruleForm" label-width="100px" class="demo-ruleForm">
                <div v-if="!admin">
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="姓名:">
                        <el-input :rows="1" :disabled="true" v-model="student.name" type="textarea" class="article-textarea" autosize placeholder="姓名"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="学号:">
                        <el-input :rows="1" :disabled="true" v-model="student.stuNum" type="textarea" class="article-textarea" autosize placeholder="学号"/>
                    </el-form-item>
                </div>
                <div v-else>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="姓名:">
                        <el-input :rows="1" :disabled="true" v-model="teacher.name" type="textarea" class="article-textarea" autosize placeholder="姓名"/>
                    </el-form-item>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="学号:">
                        <el-input :rows="1" :disabled="true" v-model="teacher.username" type="textarea" class="article-textarea" autosize placeholder="学号"/>
                    </el-form-item>
                </div>
                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="密码" prop="pass">
                    <el-input type="password" v-model="ruleForm.pass" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item style="margin-bottom: 30px;" label-width="90px" label="确认密码" prop="checkPass">
                    <el-input type="password" v-model="ruleForm.checkPass" autocomplete="off"></el-input>
                </el-form-item>

                <template #footer style="margin-bottom: 10px;">
                    <span class="dialog-footer">
                    <el-button @click="dialogPasswordVisible = false">返回</el-button>
                    <el-button @click="resetForm('ruleForm')">重置</el-button>
                    <el-button type="primary" @click="submitForm(ruleForm)">提交</el-button>
                    </span>
                </template>
                </el-form>
            </el-dialog>
        </div>

        <!-- 修改头像 -->
        <div>
            <el-dialog 
            :title="'修改头像'" 
            :visible.sync="imagecropperShow"  
            :before-close="handleClose">
                <el-upload
                    class="upload-demo"
                    :on-success="handleUploadSuccess"
                    :on-remove="handleRemove"
                    :before-remove="beforeVodRemove"
                    :on-exceed="handleUploadExceed"
                    :file-list="fileList"
                    :action="BASE_API+'/introOss/fileOss/uploadFile'"
                    :limit="1"
                    list-type="picture">
                    <el-button size="small" type="primary">点击上传</el-button>
                    <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
                </el-upload>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogVideoFormVisible = false">取 消</el-button>
                    <el-button :disabled="saveBtnDisabled" type="primary" @click="saveAvatar">确 定</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>


import cookie from 'js-cookie'
import * as dateUtils from "@/utils/date"
import studentApi from '@/api/intro/student'
import teacherApi from '@/api/intro/teacher'
import ossApi from '@/api/oss/fileOss'
import { isvalidPassword } from '@/utils/validate'
import ImageCropper from '@/components/ImageCropper'

export default {
    components: { ImageCropper },
    data(){
        var validatePass = (rule, value, callback) => {
            if (value === '') {
            callback(new Error('请输入密码'));
            } else {
            if (this.ruleForm.checkPass !== '') {
                this.$refs.ruleForm.validateField('checkPass');
            }
            callback();
            }
        };

        var validatePass2 = (rule, value, callback) => {
            if (value === '') {
            callback(new Error('请再次输入密码'));
            } else if (value !== this.ruleForm.pass) {
            callback(new Error('两次输入密码不一致!'));
            } else {
            callback();
            }
        };

        return{
            student: {}, //登陆的学生
            teacher: {}, //也有可能是教师
            newStuInfo: {}, //待更新的学生信息
            dialogFormVisible: false, //弹框是否可视
            dialogPasswordVisible: false, //改密码弹框是否可视
            imagecropperShow: false, //修改头像窗口
            saveBtnDisabled: false, //保存按钮
            BASE_API: process.env.BASE_API, // 接口API地址
            fileList: [],
            admin: false, //是否是管理员 
            ruleForm: {
                pass: '',
                checkPass: '',
            },
            rules: {
                pass: [
                    { validator: validatePass, trigger: 'blur' }
                ],
                checkPass: [
                    { validator: validatePass2, trigger: 'blur' }
                ],
            }

        }
    },
    created(){
        this.admin = (cookie.get('intro_admin') === "true")
        console.log(this.admin);
        if(this.admin){
            this.teacher = JSON.parse(cookie.get('intro_roles')) //初始化登陆的教师信息
        }else{
            this.student = JSON.parse(cookie.get('intro_roles')) //初始化登陆的学生信息
        }
    },
    methods: {
        //格式化日期
        formatDate(dateStr){
            if(dateStr==null){
            return "--"
            }else{
            let date = new Date(dateStr);
            return dateUtils.formatDate(date,'yyyy-MM-dd hh:mm:ss');
            }
        },
        //打开编辑的窗口
        toEdit(){
            this.dialogFormVisible = true
        },
        toEditPassword(){
            this.dialogPasswordVisible = true
        },
        //窗口中点击确认 确认修改
        toUpdateInfo(){
            if(!this.admin){
                studentApi.updateInfo(this.student).then( () =>{
                    this.$message({
                        type: 'success',
                        message: '修改成功!'
                    });
                    //后端更新数据库之后更新当前cookie里面的数据
                    cookie.set('intro_roles', this.student, { domain: 'localhost' })
                    this.handleClose()
                    console.log(JSON.parse(cookie.get('intro_roles')));
                    location.reload()  //刷新页面 可忽略
                })
            }else{
                teacherApi.updateInfo(this.teacher).then( () =>{
                    this.$message({
                        type: 'success',
                        message: '修改成功!'
                    });
                    //后端更新数据库之后更新当前cookie里面的数据
                    cookie.set('intro_roles', this.teacher, { domain: 'localhost' })
                    this.handleClose()
                    console.log(JSON.parse(cookie.get('intro_roles')));
                    location.reload()  //刷新页面 可忽略
                })
            }
        },
        submitForm(formName) {
            if(formName.pass === formName.checkPass){
                if(isvalidPassword(formName.checkPass)){
                    if(!this.admin){
                        this.student.password = formName.checkPass
                    }else{
                        this.teacher.password = formName.checkPass
                    }
                    this.toUpdateInfo()
                    alert('提交成功！');
                }else{
                    alert('密码不符合格式要求！请确认密码长度在8-16之间');
                }
                this.resetForm()
            }else{
                alert('两次输入密码不一致！请确认后重新提交');
            }
         
        },
        resetForm() {
            this.ruleForm.pass = ""
            this.ruleForm.checkPass = ""
        },

        handleClose(){
            this.dialogFormVisible = false
            this.dialogPasswordVisible = false
            this.imagecropperShow = false
        },


        //更换头像
        handleUploadSuccess(response, file, fileList){  //提交成功之后
            console.log("===============这里是上传头像操作=================");
            console.log(response.data.url);
            //上传头像url赋值
            if(!this.admin){
                this.student.avatar = response.data.url
            }else{
                this.teacher.avatar = response.data.url
            }
            
            console.log(this.student);
        },
        handleRemove(file, fileList) {
            ossApi.removeOssFile(file).then(response =>{
                //清空文件列表
                this.fileList = []
                this.$message({
                    type: 'success',
                    message: response.message
                })
            })
            console.log(file, fileList);
        },
        handleUploadExceed(files, fileList){
            this.$message.warning('想要重新上传视频，请先删除已上传的视频')
        },
        //确认删除提示
        beforeVodRemove(file, fileList){
            return this.$confirm(`确定移除 ${file.name}？`)
        },
        saveAvatar(){
            this.$message.success('保存成功')
            this.toUpdateInfo()  
        }
        
    }
}
</script>

<style scoped>
  .avatar{
    width: 200px;
    height: 200px;
    border-radius: 50%;
  }
</style>