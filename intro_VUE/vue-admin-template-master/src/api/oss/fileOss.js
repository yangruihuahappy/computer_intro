import request from '@/utils/request'

export default{
    
    //1 通过url下载
    loadOssFile(fileUrl){
        return request({
            url: `/introOss/fileOss/downloadBaseFileByUrl`,
            method: 'post',
            data: fileUrl
        })    
    },

    //2 通过url删除
    removeOssFile(fileUrl){
        return request({
            url: `/introOss/fileOss/removeByUrl`,
            method: 'post',
            data: fileUrl,
        })    
    },

    //3 上传文件
    uploadFile(file){
        return request({
            url: `/introOss/fileOss/uploadFile`,
            method: 'post',
            data: file,
        }) 
    },

}