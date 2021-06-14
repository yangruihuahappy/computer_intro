import request from '@/utils/request'

export default{
    /**==========================公共接口方法=========================== */
    //1 根据学生id进行查询所有课前预习状态
    findAllByStudent(current,limit,stuId,experimentQuery){
        return request({
            url: `/introService/experiment/findExperimentByStudent/${current}/${limit}/${stuId}`,
            method: 'post',
            data: experimentQuery
        })
    },

    //2 根据id查询课前预习已完成所有内容，包含题目作答题目
    findExperimentById(stuExperimentId){
        return request({
            url: `/introService/experiment/findStuExperimentById/${stuExperimentId}`,
            method: 'get',
        })
    },

    /**==========================教师端接口方法=========================== */
    //3 发布新的实验练习
    addExperiment(experiment){
        return request({
            url: `/introService/experiment/addExperiment`,
            method: 'post',
            data: experiment
        })
    },

    //4 根据截止时间进行倒叙分页查询
    findDeadlineExperiment(current,limit){
        return request({
            url: `/introService/experiment/findDeadline/${current}/${limit}`,
            method: 'get',
        })
    },

    //5 修改实验——查询到实验进行回显
    getExperimentById(experimentId){
        return request({
            url: `/introService/experiment/getExperimentById/${experimentId}`,
            method: 'get',
        })
    },

    //7 修改实验——更新实验报告数据
    updateExperiment(experiment){
        return request({
            url: `/introService/experiment/updateExperiment`,
            method: 'post',
            data: experiment
        })
    },

    //8 删除课前预习
    deleteExperimentById(experimentId){
        return request({
            url: `/introService/experiment/deleteExperimentById/${experimentId}`,
            method: 'delete',
        })
    },
    
    //9 教师端查询所有实验练习
    findExperimentPage(current,limit){
        console.log("查看运行进程");
        return request({
            url: `/introService/experiment/findExperimentPage/${current}/${limit}`,
            method: 'get'
        })
    },

    // 查询某次实验下的所有学生实验情况
    findStuExperiment(current,limit,experimentId){
        return request({    
            url: `/introService/experiment/findStuExperiment/${current}/${limit}/${experimentId}`,
            method: 'get'
        })
    },

    //教师端更新学生练习内容 教师端打分操作
    updateStuExperimentByTeacher(stuExperiment){
        return request({
            url: `/introService/experiment/updateStuExperimentByTeacher`,
            method: 'post',
            data: stuExperiment
        })
    },

    //教师端查询未批改的练习
    findNoScoreExperiment(current,limit){
        return request({
            url: `/introService/experiment/findNoScoreExperiment/${current}/${limit}`,
            method: 'get'
        })
    },

    /**==========================学生端接口方法=========================== */
    //10 提交课前预习
    updateStuExperiment(stuExperiment){
        return request({
            url: `/introService/experiment/updateStuExperiment`,
            method: 'post',
            data: stuExperiment
        })
    }, 

    //通过实验id获取报告下载url
    getUrlById(experimentId){
        return request({
            url: `/introService/experiment/getBaseFileUrl/${experimentId}`,
            method: 'get',
        })
    } 
}