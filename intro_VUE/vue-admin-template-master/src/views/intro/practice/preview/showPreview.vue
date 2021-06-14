<template>
    <div>
        <!-- 上部分查询表栏-->
        <div class="show" style="margin-top: 10px;" >
            <el-form :inline="true" class="demo-form-inline" label-width="800px">
                    <el-form-item label-width="200px" label="该课前预习完成情况：" style="margin-left: 10px; float:left">
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
    </div>
</template>

<script>

import preview from '@/api/intro/practice/preview'
import * as dateUtils from "@/utils/date"

export default {
    data(){
        return {
            page: 1,
            limit: 10,
            total: 0,
            completion: "", //完成情况
            previewId: "", //显示的previewId
            listLoading: true, // 是否显示loading信息
            list:null,//查询之后接口返回集合
            toShowName: {}, //所有显示的名称都是一样的
            stuNameList: {}, //学生姓名
        }
    },
    created(){
        //获取列表
        if(this.$route.params && this.$route.params.id){
            console.log("当前url有课程id");
            this.previewId = this.$route.params.id
        }
        this.getList()
    },
    methods: {
        getList(page=1){
            
            this.page = page
            this.listLoading = true
            preview.findPreviewById(this.page,this.limit,this.previewId)
            .then(response =>{
                this.list = response.data.stuPreviewPageById
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

    }
}
</script>

<style>

</style>