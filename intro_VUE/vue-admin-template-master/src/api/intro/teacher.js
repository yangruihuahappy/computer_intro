import request from '@/utils/request'

export default{

    // 修改个人信息
    updateInfo(teacher){
        return request({
            url: `/introService/teacher/updateTeacher`,
            method: 'post',
            data: teacher,
            })
    },


}