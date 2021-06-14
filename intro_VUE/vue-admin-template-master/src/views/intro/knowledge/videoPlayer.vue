<template>

    <div>
    <link rel="stylesheet" href="https://g.alicdn.com/de/prismplayer/2.8.1/skins/default/aliplayer-min.css" >
    <!-- 定义播放器dom -->
        <div id="J_prismPlayer" class="prism-player" />
    </div>
</template>

    <!-- 阿里云视频播放器脚本 -->
    <script charset="utf-8" type="text/javascript" src="https://g.alicdn.com/de/prismplayer/2.8.1/aliplayer-min.js" />
<script>
import video from '@/api/video/video'
export default {
     data() {
        return {
            videoId: '',
            playAuth: '',
        }
    },
    created(){
        if(this.$route.params && this.$route.params.id){
            this.videoId = this.$route.params.id
            video.getPlayAuth(this.videoId).then(response => {
                this.playAuth = response.data.playAuth
            })
        }
        
    
    },
    mounted() { //页面渲染之后  created
    // eslint-disable-next-line no-unused-vars 
        new Aliplayer({
            id: 'J_prismPlayer',
            vid: this.vid, // 视频id
            playauth: this.playAuth, // 播放凭证
            encryptType: '1', // 如果播放加密视频，则需设置encryptType=1，非加密视频无需设置此项
            width: '100%',
            height: '500px',
            // 以下可选设置
            cover: 'http://guli.shop/photo/banner/1525939573202.jpg', // 封面
            qualitySort: 'asc', // 清晰度排序

            mediaType: 'video', // 返回音频还是视频
            autoplay: false, // 自动播放
            isLive: false, // 直播
            rePlay: false, // 循环播放
            preload: true,
            controlBarVisibility: 'hover', // 控制条的显示方式：鼠标悬停
            useH5Prism: true, // 播放器类型：html5
        }, function(player) {
            console.log('播放器创建成功')
        })
    }

}
</script>

<style>

</style>