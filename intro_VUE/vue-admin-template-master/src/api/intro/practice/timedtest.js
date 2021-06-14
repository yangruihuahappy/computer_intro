import request from '@/utils/request'

export default{
    /**==========================公共接口方法=========================== */
    //1 根据学生id进行查询所有课前预习状态
    findAllByStudent(current,limit,stuId,timedQuery){
        return request({
            url: `/introService/timedtest/findTimedtestByStudent/${current}/${limit}/${stuId}`,
            method: 'post',
            data: timedQuery
        })
    },

    //2 根据id查询课前预习已完成所有内容，包含题目作答题目
    findStuTimedtestById(stuTimedtestId){
        return request({
            url: `/introService/timedtest/findStuTimedtestById/${stuTimedtestId}`,
            method: 'get',
        })
    },

    /**==========================教师端接口方法=========================== */
    //3 查询教师发布的所有课前预习
    findAllByTeacher(current,limit){
        return request({
            url: `/introService/timedtest/findTimedtestPage/${current}/${limit}`,
            method: 'get'
        })
    },

    //4 根据截止时间进行倒叙分页查询
    findDeadlineHomework(current,limit){
        return request({
            url: `/introService/timedtest/findDeadline/${current}/${limit}`,
            method: 'get',
        })
    },

    //5 根据某次课前预习查询该次练习下所有学生完成情况
    findTimedtestById(current,limit,timedtestId){
        return request({
            url: `/introService/timedtest/findStuTimedtest/${current}/${limit}/${timedtestId}`,
            method: 'get',
        })
    },

    //6 根据id查询练习本身
    getTimedtestById(timedtestId){
        return request({
            url: `/introService/timedtest/getTimedtest/${timedtestId}`,
            method: 'get',
        })
    },

    //7 修改课前预习
    updateTimedtest(timedtest){
        return request({
            url: `/introService/timedtest/updateTimedtest`,
            method: 'post',
            data: timedtest
        })
    },

    //8 删除课前预习
    deleteTimedtestById(timedtestId){
        return request({
            url: `/introService/timedtest/deleteTimedtestById/${timedtestId}`,
            method: 'delete',
        })
    },
    
    //9 教师端添加课前预习
    addTimedtest(timedtest){
        return request({
            url: `/introService/timedtest/addTimedtest`,
            method: 'post',
            data: timedtest
        })
    },

    /**==========================学生端接口方法=========================== */
    //10 提交课前预习
    updateStuTimedtest(stuTimedtest){
        return request({
            url: `/introService/timedtest/updateStuTimedtest`,
            method: 'post',
            data: stuTimedtest
        })
    }    
}