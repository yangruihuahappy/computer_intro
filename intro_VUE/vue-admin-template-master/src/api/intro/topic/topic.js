import request from '@/utils/request'

export default{

    //查询所有题目
    getTopicPageList(current,limit){
        return request({
            url:`/introService/topic/pageTopic/${current}/${limit}`,
            method:'get',
        })
    },

    //查询所有已采纳题目
    changeAdoptedTopicPageList(current,limit){
        return request({
            url:`/introService/topic/adoptedTopicPageList/${current}/${limit}`,
            method:'get',
        })
    },

    //查询当前所有题目的数量和章节信息
    findCountPerChapter(){
        return request({
            url:`/introService/topic/findCountPerChapter`,
            method:'get',
        })
    },
    
    //根据学生id进行查询
    getTopicListByProId(stuId){
        return request({
            url:`/introService/topic/getTopicListByProId/${stuId}`,
            method:'get',
        })
    },

    //新建题目
    addTopic(topic){
        return request({
            url:`/introService/topic/addTopic`,
            method:'post',
            data: topic
        })
    },

    //根据topicId进行查询
    getTopicByTopicId(topicId){
        return request({
            url:`/introService/topic/getTopicById/${topicId}`,
            method:'get',
        })
    },

    //删除题目
    removeTopicById(topicId){
        return request({
            url:`/introService/topic/deleteTopicById/${topicId}`,
            method:'delete',
        })
    },

    //查询热点题目
    getHotTopicList(current,limit){
        return request({
            url:`/introService/topic/pageHotListTopic/${current}/${limit}`,
            method:'get',
        })
    }


    

}