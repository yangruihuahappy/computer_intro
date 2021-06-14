<template>
    <div>
        <!-- 上部分查询表栏-->
        <div class="show" style="margin-top: 10px;" >
            <el-form :inline="true" class="demo-form-inline" label-width="800px">
                    <el-form-item label-width="200px" label="该周总结及完成情况：" style="margin-left: 10px; float:left">
                            {{this.description}}——{{this.completion}}
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

                <el-table-column label="学生姓名" align="center" width="180"> 
                    <template slot-scope="scope">
                        {{stuNameList[scope.$index]}}
                    </template> 
                </el-table-column>

                <el-table-column label="练习标题" align="center" width="200"> 
                    <template slot-scope="scope">
                        {{toShowName[scope.$index-scope.$index]}}
                    </template> 
                </el-table-column>

                <el-table-column prop="content" label="总结内容"  align="center" :show-overflow-tooltip = "true"/>
            
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

                 <el-table-column label="操作" width="90" align="center">
                    <template slot-scope="scope">
                        <el-button-group>
                        <el-button type="success" size="mini" :disabled='!scope.row.finish || scope.row.score!=-1' @click="openStuSummary(scope.row.id)">查看</el-button>
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

        <div>
            <!-- 弹出的新增和编辑框 -->
            <el-dialog :title="'查看总结'" :visible.sync="dialogFormVisible"  :close-on-click-modal='false'>
            <el-form ref="dataForm"  :value="stuSummaryInfo" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
                <el-form-item >
                <div class="summaryInput" style="width: 100%">
                    本周总结
                    <el-input id="input1" type="textarea" 
                            :disabled="true"
                            :rows="10" maxlength="500" 
                            style="width: 100%" show-word-limit 
                            placeholder="请输入内容" v-model="stuSummaryInfo.content"/>
                </div>
                </el-form-item>
                <el-form-item style="margin-bottom: 5px;" label-width="90px" label="评分:">
                    <el-input :rows="1"  v-model="stuSummaryInfo.score" type="textarea" class="article-textarea" autosize placeholder="请输入分数"/>
                </el-form-item>
                
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">返回</el-button>
                <el-button type="primary" @click="toUpdateInfo()">确认</el-button>
            </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>

import summary from '@/api/intro/summary'
import * as dateUtils from "@/utils/date"

export default {
    data(){
        return {
            page: 1,
            limit: 10,
            total: 0,
            completion: "", //完成情况
            summaryId: "", //显示的previewId
            description: "", //显示的总结描述
            dialogFormVisible: false, //弹窗
            listLoading: true, // 是否显示loading信息
            list:null,//查询之后接口返回集合
            toShowName: {}, //所有显示的名称都是一样的
            stuNameList: {}, //学生姓名
            stuSummaryInfo: {}, //弹窗内容
        }
    },
    created(){
        //获取列表
        if(this.$route.params && this.$route.params.id){
            console.log("当前url有课程id");
            this.summaryId = this.$route.params.id
        }
        this.getList()
    },
    methods: {
        getList(page=1){
            
            this.page = page
            this.listLoading = true
            summary.findSummaryById(this.page,this.limit,this.summaryId)
            .then(response =>{
                this.list = response.data.stuSummaryPageById
                this.toShowName[0] = response.data.toShowName
                this.total = response.data.total
                this.stuNameList = response.data.stuNameList
                this.completion = response.data.completion
                this.description = response.data.description
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
        //查看学生总结详情
        openStuSummary(id){
            this.dialogFormVisible = true, //弹窗打开
            summary.findStuSummaryById(id).then( response=>{
                this.stuSummaryInfo = response.data.stuSummaryInfo
            })
            console.log(id);
        },
        toUpdateInfo(){  //提交
            console.log(this.stuSummaryInfo)
            summary.updateStuSummary(this.stuSummaryInfo).then( ()=>{
                this.$message({
                    type: 'success',
                    message: '提交成功!'
                });
                this.handleClose();
                this.getList();
            })
        },
        handleClose(){
            this.dialogFormVisible = false
            this.stuSummaryInfo = {}
        },

    }
}
</script>

<style>

</style>