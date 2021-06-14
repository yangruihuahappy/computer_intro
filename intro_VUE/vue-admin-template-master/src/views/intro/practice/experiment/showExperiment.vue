<template>
    <div>
        <!-- 上部分查询表栏-->
        <div class="show" style="margin-top: 10px;" >
            <el-form :inline="true" class="demo-form-inline" label-width="800px">
                    <el-form-item label-width="200px" label="该实验练习完成情况：" style="margin-left: 10px; float:left">
                            {{this.completion}}
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
                    width="70"
                    align="center">
                    <template slot-scope="scope">
                        {{ (page - 1) * limit + scope.$index + 1 }}
                    </template>
                </el-table-column>

                <el-table-column label="学生姓名" align="center"> 
                    <template slot-scope="scope">
                        {{stuNameList[scope.$index]}}
                    </template> 
                </el-table-column>

                <el-table-column label="练习标题" align="center"> 
                    <template slot-scope="scope">
                        {{toShowName[scope.$index-scope.$index]}}
                    </template> 
                </el-table-column>
            
                <el-table-column label="完成情况" width="100" align="center">
                    <template slot-scope="scope">
                        {{ scope.row.finish===true?'已完成':'未完成' }}
                    </template>
                </el-table-column>

                <el-table-column prop="score" label="练习成绩" width="80" align="center"/>
                

                <el-table-column label="截止时间" width="200" align="center">
                    <template slot-scope="scope">
                        {{formatDate(scope.row.gmtDeadline)}}
                    </template> 
                </el-table-column>

                <el-table-column label="完成时间" width="200" align="center">
                    <template slot-scope="scope">
                        {{formatDate(scope.row.gmtFinish)}}
                    </template> 
                </el-table-column>

                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <el-button-group>
                        <el-button type="success" size="mini" :disabled='!scope.row.finish' @click="openStuExperiment(scope.row.id)">查看</el-button>
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
        <!-- 打开显示详情信息弹窗 -->
            <el-dialog 
            :title="'批改实验练习'" 
            :visible.sync="dialogFormVisible"  
            :before-close="handleClose">
                <el-form ref="postForm" :model="stuExperimentInfo" class="form-container">
                    <el-button size="small" type="primary" style="margin-bottom: 10px;" @click="downloadReport(stuExperimentInfo.ossUrl)">下载实验报告</el-button>
                    <el-tooltip placement="right-end">
                        <div slot="content">
                            默认下载后的地址为D://testload/ 文件夹中，请自行查看！
                        </div>
                        <i class="el-icon-warning"/>
                    </el-tooltip>
                    <el-form-item style="margin-bottom: 5px;" label-width="90px" label="评分:">
                        <el-input :rows="1"  v-model="stuExperimentInfo.score" type="textarea" class="article-textarea" autosize placeholder="请输入分数"/>
                    </el-form-item>

                    <template #footer style="margin-bottom: 10px;">
                        <span class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">返回</el-button>
                        <el-button type="primary" @click="toUpdateInfo()">提交</el-button>
                        </span>
                    </template>
                </el-form>

            </el-dialog>
        <div>

        </div>
    </div>
</template>

<script>

import experiment from '@/api/intro/practice/experiment'
import * as dateUtils from "@/utils/date"
import oss from '@/api/oss/fileOss'

export default {
    data(){
        return {
            page: 1,
            limit: 10,
            total: 0,
            completion: "", //完成情况
            experimentId: "", //显示的experimentId
            listLoading: true, // 是否显示loading信息
            list:null,//查询之后接口返回集合
            toShowName: {}, //所有显示的名称都是一样的
            stuNameList: {}, //学生姓名
            stuExperimentInfo: {}, //窗口显示的实验信息
            dialogFormVisible: false, //弹框是否可视  
            BASE_API: process.env.BASE_API, // 接口API地址 
        }
    },
    created(){
        //获取列表
        if(this.$route.params && this.$route.params.id){
            console.log("当前url有课程id");
            this.experimentId = this.$route.params.id
        }
        this.getList()
    },
    methods: {
        getList(page=1){
            console.log(this.experimentId);
            this.page = page
            this.listLoading = true 
            experiment.findStuExperiment(this.page,this.limit,this.experimentId)
            .then( response =>{
                this.list = response.data.stuExperimentPageById
                this.toShowName[0] = response.data.toShowName
                this.total = response.data.total
                this.stuNameList = response.data.stuNameList
                this.completion = response.data.completion
                console.log(this.toShowName);
            })
            .catch(error=>{
                console.log(error);
            })
            this.listLoading = false
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

        //查看详情并打分
        openStuExperiment(id){
            this.dialogFormVisible = true
            console.log(id);
            experiment.findExperimentById(id).then( response=>{
                this.stuExperimentInfo = response.data.stuExperimentInfo
            })
        },

        //下载学生实验报告
        downloadReport(url){
            console.log(url);
            oss.loadOssFile(url).then( ()=>{
                this.$message({
                    type: 'success',
                    message: '下载成功!请前往本地文件中查看'
                });
            })
        },

        toUpdateInfo(){
            console.log(this.stuExperimentInfo)
            experiment.updateStuExperimentByTeacher(this.stuExperimentInfo).then( ()=>{
                this.$message({
                    type: 'success',
                    message: '提交成功!'
                });
                this.handleClose();
            })
        },
        handleClose(){
            this.dialogFormVisible = false
            this.stuExperimentInfo = {}
        },

    }
}
</script>

<style>

</style>