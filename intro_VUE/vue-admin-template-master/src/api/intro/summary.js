import request from '@/utils/request'

export default{
    
    /**
     * ==========================公共接口方法===========================
     */
    //1 根据学生id查询某个学生的所有每周总结
    findAllSummaryByStu(current,limit,stuId,summaryQuery){
        return request({
            url: `/introService/summary/findAllSummaryByStu/${current}/${limit}/${stuId}`,
            method: 'post',
            data: summaryQuery
        })
    },

    //2 根据stu_Summary_id查看某位同学具体的每周总结
    findStuSummaryById(stuSummaryId){
        return request({
            url: `/introService/summary/findStuSummaryById/${stuSummaryId}`,
            method: 'get',
        })
    },

    //获取总结展示成就的下标和成绩
    getAllSummaryScore(stuId){
        return request({
            url: `/introService/student/getAllSummaryScore/${stuId}`,
            method: 'get',
        })
    },

    /**
     * ==========================教师端接口方法===========================
     */

    //3获取所有每周总结
    findSummaryPage(current,limit,summaryQuery){
        return request({
            url: `/introService/summary/findSummaryPage/${current}/${limit}`,
            method: 'post',
            data: summaryQuery
        })
    },
    //4 删除每周总结
    deleteSummaryById(id){
        return request({
            url: `/introService/summary/deleteSummaryById/${id}`,
            method: 'delete'
        })
    },

    //findSummaryById
    findSummaryById(current,limit,id){
        return request({
            url: `/introService/summary/findStuSummaryBySummaryId/${current}/${limit}/${id}`,
            method: 'get'
        })
    },

    //判断当前周是否发布过总结
    judgePublish(){
        return request({
            url: `/introService/summary/judgePublish`,
            method: 'get'
        })
    },

    //添加每周总结
    addSummary(summary){
        return request({
            url: `/introService/summary/addSummary`,
            method: 'post',
            data: summary
        })
    },

        /**
     * ==========================学生端接口方法===========================
     */

    // 学生提交更新总结
    updateStuSummary(stuSummary){
        return request({
            url: `/introService/summary/updateStuSummary`,
            method: 'post',
            data: stuSummary,
        })
    }
}