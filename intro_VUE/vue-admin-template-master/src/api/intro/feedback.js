import request from '@/utils/request'

export default{
    
    //1 添加反馈
    publishFeedback(feedback){
        return request({
            url: `/introService/feedback/publish`,
            method: 'post',
            data: feedback,
          })
    },

    //查询所有反馈
    getFeedbackList(current,limit){
        return request({
            url: `/introService/feedback/getList/${current}/${limit}`,
            method: 'get'
          })
    },

}