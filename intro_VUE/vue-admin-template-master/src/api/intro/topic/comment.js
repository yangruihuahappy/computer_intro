import request from '@/utils/request'

export default{
    //1 新增评论
    addComment(comment){
        console.log(comment);
        return request({
            url: `/introService/comment/addOrUpdateComment`,
            method: 'post',
            data: comment
        })
    },

    //2 查找所有评论
    findCommentList(topicId){
        return request({
            url: `/introService/comment/findCommentsByTopicId/${topicId}`,
            method: 'get',
        })
    },

    //3 点赞评论
    praiseComment(comment){
        return request({
            url: `/introService/comment/addOrUpdateComment`,
            method: 'post',
            data: comment
        })
    },
}