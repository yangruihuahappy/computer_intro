import request from '@/utils/request'

export default{

    //1 获取成就的下标和分数
    getAllPracticeScore(stuId){
        console.log(stuId);
        return request({
            url: `/introService/student/getAllPracticeScore/${stuId}`,
            method: 'get',
          })
    },

    //2 修改个人信息
    updateInfo(student){
        return request({
            url: `/introService/student/updateStu`,
            method: 'post',
            data: student,
            })
    },

    //根据id进行查询学生信息
    findInfoByStuId(id){
        return request({
            url: `/introService/student/getStuInfo/${id}`,
            method: 'get'
        })
    },

    //教师端查询所有学生信息
    getStudentList(current,limit,studentQuery){
        return request({
            url: `/introService/student/pageStudent/${current}/${limit}`,
            method: 'post',
            data: studentQuery
        })
    },

    //教师端添加学生信息
    saveStudent(student){
        return request({
            url: `/introService/student/addStu`,
            method: 'post',
            data: student
        })
    },

    //教师端批量添加学生信息
    saveStudentBatch(file){
        return request({
            url: `/introService/student/addStuBatch`,
            method: 'post',
            data: file,
        }) 
    },

    //教师端删除某个学生信息
    removeStudent(id){
        return request({
            url: `/introService/student/deleteStuById/${id}`,
            method: 'delete',
        })
    }

}