<template>
  <div>
   123
  </div>
</template>

<script>

import video from '@/api/video/video'

export default {
  layout: 'video',//应用video布局
  asyncData({ params, error }) {
    return video.getPlayAuth(params.vid).then(response => {
      console.log(response.data);
      console.log(response.data.data)
      return {
        playAuth: response.data.data.playAuth,
        vid: params.vid,
      }
    })
  },
  mounted() {
    new Aliplayer({
        id: 'J_prismPlayer',
        vid: this.vid, // 视频id
        playauth: this.playAuth, // 播放凭证
        encryptType: '1', // 如果播放加密视频，则需设置encryptType=1，非加密视频无需设置此项
        width: '100%',
        height: '500px',
        autoplay: false, // 自动播放
    }, function(player) {
        console.log('播放器创建成功')
    })
  }
}
</script>