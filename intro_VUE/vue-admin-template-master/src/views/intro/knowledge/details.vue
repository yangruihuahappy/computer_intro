<template>
    <div>
        <el-card class="box-card">
            <p>{{this.knowDetils.title}}</p>
            
            <p>{{this.knowDetils.content}}</p>
            <br>
            <br>
            <div>
                <a v-if="knowDetils.fileUrl!=null" @click="downloadFile(knowDetils.fileUrl)">下载课堂ppt</a>
                
                <!-- <a v-if="knowDetils.videoSourceId!=null" 
                :href="'/player/'+knowDetils.videoSourceId"
                :title="knowDetils.title"
                target="_blank"
                style="float:right">播放视频</a> -->
                <a v-if="knowDetils.videoSourceId!=null"  @click="toShowVideo(knowDetils.videoSourceId)" style="float:right">播放视频</a>
            </div>
        </el-card>      
    </div>
</template>

<script>

import know from '@/api/intro/knowledge'
import oss from '@/api/oss/fileOss'


export default {
    props: {
        'knowledgeId': '',
    },
    data() {
        return{
            knowDetils:{
                id:'', //详情id
                title:'计算机导论', //标题
                content:'通过学习该课程， 你将对计算机系统有一个初步、系统的了解；你将学习到与计算机科学的相关的内容，对计算机专业的学习内容有一个大概的了解；', //内容
                chapterId:'', //所属章节
                sectionId:'', //所属小节
                fileUrl: null, 
            },
            DetailsId: '',
            playAuth: '', //播放凭证
            videoId: '',
        }
    },
    created() {

    },
    watch: { //监听

        'knowledgeId': function(val){
            this.DetailsId = val;
            console.log("发生了变化啊");
            console.log("变化后的id："+this.DetailsId);
            this.findKnowById(this.DetailsId)
        },
        
    },
    methods: {
        findKnowById(id){
            console.log("id发生变化");
            know.getKnowledgeInfoById(id)
            .then(response => {
                this.knowDetils = response.data.knowledgeInfo
                console.log(this.knowDetils);
            })        
            console.log(this.knowDetils);    
        },
        downloadFile(url){
            console.log(url);
            oss.loadOssFile(url).then(()=>{
            //下载成功
                this.$message({
                    type: 'success',
                    message: '下载成功!'
                });
            })
            
        },
        toShowVideo(id){
            console.log(id);
            this.$router.push({ path:'/knowledge/videoPlayer/'+ id})
        }

    }

}
</script>

<style>
.text {
    font-size: 14px;
}

.item {
    padding: 18px 0;
}

.box-card {
    width: 520px;
    min-height: 100%;
    height: 100%;
}
</style>