import request from '@/utils/request'

export default{
    
    //1 章节目录列表
    getChapterList(){
        return request({
            url: `/introService/chapter/getAllChapter`,
            method: 'get',
          })
    },
}