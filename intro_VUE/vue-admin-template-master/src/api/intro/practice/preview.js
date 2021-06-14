import request from '@/utils/request'

export default{
    /**==========================公共接口方法=========================== */
    //1 根据学生id进行查询所有课前预习状态
    findAllByStudent(current,limit,stuId,previewQuery){
        return request({
            url: `/introService/preview/findAllByStudent/${current}/${limit}/${stuId}`,
            method: 'post',
            data: previewQuery
        })
    },

    //2 根据id查询课前预习已完成所有内容，包含题目作答题目
    findStuPreviewById(stuPreviewId){
        return request({
            url: `/introService/preview/findStuPreviewById/${stuPreviewId}`,
            method: 'get',
        })
    },

    /**==========================教师端接口方法=========================== */
    //3 查询教师发布的所有课前预习
    findAllByTeacher(current,limit){
        return request({
            url: `/introService/preview/findAllByTeacher/${current}/${limit}`,
            method: 'get',
        })
    },

    //4 根据截止时间进行倒叙分页查询
    findDeadlinePreview(current,limit){
        return request({
            url: `/introService/preview/findDeadline/${current}/${limit}`,
            method: 'get',
        })
    },

    //5 根据某次课前预习查询该次练习下所有学生完成情况
    findPreviewById(current,limit,previewId){
        return request({
            url: `/introService/preview/findPreviewById/${current}/${limit}/${previewId}`,
            method: 'get',
        })
    },

    //6 根据id查询练习本身
    getPreviewById(previewId){
        return request({
            url: `/introService/preview/getPreview/${previewId}`,
            method: 'get',
        })
    },

    //7 修改课前预习
    updatePreview(preview){
        return request({
            url: `/introService/preview/updatePreview`,
            method: 'post',
            data: preview
        })
    },

    //8 删除课前预习
    deletePreviewById(previewId){
        return request({
            url: `/introService/preview/deletePreviewById/${previewId}`,
            method: 'delete',
        })
    },
    
    //9 教师端添加课前预习
    addPreview(preview){
        return request({
            url: `/introService/preview/addPreview`,
            method: 'post',
            data: preview
        })
    },

    /**==========================学生端接口方法=========================== */
    //10 提交课前预习
    addStuPreview(stuPreview){
        return request({
            url: `/introService/preview/addStuPreview`,
            method: 'post',
            data: stuPreview
        })
    },    

    /**=========================其他方法=========================== */
    findPreviewName(id){
        return request({
            url: `/introService/preview/getPreview/${id}`,
            method: 'get',
        })
    }    

}