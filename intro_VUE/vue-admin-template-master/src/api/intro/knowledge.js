import request from '@/utils/request'

export default{
    
    //1 根据id获取知识点
    getKnowledgeInfoById(id){
        return request({
            url: `/introService/knowledge/getKnowledge/${id}`,
            method: 'get',
        })
    },

    //2 添加新的知识点
    addKnowledge(knowInfo){
        return request({
            url: `/introService/knowledge/addKnowledge`,
            method: 'post',
            data: knowInfo
        })
    }
}