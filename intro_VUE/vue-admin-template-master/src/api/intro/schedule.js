import request from '@/utils/request'

export default{
    
    //1 获取待办事项
    getToDoList(student){
        console.log(student);
        return request({
            url: `/introService/student/stuToDoList`,
            method: 'post',
            data: student,
          })
    },

    //2 获取紧急事项
    getUrgentList(query){
        console.log(query);
        return request({
            url: `/introService/student/getUrgentList`,
            method: 'post',
            data: query,
        })
    },

    //3 获取错过的任务
    getMissedList(student){
        return request({
            url: `/introService/student/getMissedList`,
            method: 'post',
            data: student,
        })
    }
}