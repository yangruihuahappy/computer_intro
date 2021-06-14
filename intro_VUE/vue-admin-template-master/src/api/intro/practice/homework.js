import request from '@/utils/request'

export default{
    /**==========================公共接口方法=========================== */
    //1 根据学生id进行查询所有课前预习状态
    findAllByStudent(current,limit,stuId,homeworkQuery){
        return request({
            url: `/introService/homework/findAllByStudent/${current}/${limit}/${stuId}`,
            method: 'post',
            data: homeworkQuery
        })
    },

    //2 根据id查询课前预习已完成所有内容，包含题目作答题目
    findStuHomeworkById(stuHomeworkId){
        return request({
            url: `/introService/homework/findStuHomeworkById/${stuHomeworkId}`,
            method: 'get',
        })
    },

    /**==========================教师端接口方法=========================== */
    //3 查询教师发布的所有课前预习
    findAllByTeacher(current,limit){
        return request({
            url: `/introService/homework/findHomeworkPage/${current}/${limit}`,
            method: 'get',
        })
    },

    //4 根据截止时间进行倒叙分页查询
    findDeadlineHomework(current,limit){
        return request({
            url: `/introService/homework/findDeadline/${current}/${limit}`,
            method: 'get',
        })
    },

    //5 根据某次课前预习查询该次练习下所有学生完成情况
    findHomeworkById(current,limit,homeworkId){
        return request({
            url: `/introService/homework/findStuHomework/${current}/${limit}/${homeworkId}`,
            method: 'get',
        })
    },

    //6 根据id查询练习本身
    getHomeworkById(homeworkId){
        return request({
            url: `/introService/homework/getHomework/${homeworkId}`,
            method: 'get',
        })
    },

    //7 修改课前预习
    updateHomework(homework){
        return request({
            url: `/introService/homework/updateHomework`,
            method: 'post',
            data: homework
        })
    },

    //8 删除课前预习
    deleteHomeworkById(homeworkId){
        return request({
            url: `/introService/homework/deleteHomeworkById/${homeworkId}`,
            method: 'delete',
        })
    },
    
    //9 教师端添加课后作业
    addHomework(homework){
        return request({
            url: `/introService/homework/addHomework`,
            method: 'post',
            data: homework
        })
    },

    /**==========================学生端接口方法=========================== */
    //10 提交课前预习
    updateStuHomework(stuHomework){
        return request({
            url: `/introService/homework/updateStuHomework`,
            method: 'post',
            data: stuHomework
        })
    }    
}