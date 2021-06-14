<template>
  <div class="dashboard-container">

  <el-row :gutter="16">
    <el-carousel :interval="4000" type="card" height="200px">
      <el-carousel-item v-for="item in imagebox" :key="item.id">
        <img :src="item.idView" class="image">
      </el-carousel-item>
    </el-carousel>
  </el-row>
  <el-row :gutter="16">
    <!-- 讲师介绍模块 -->
    <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
      <teacher-text/>
    </el-col>

    <!-- 主页课程介绍模块 -->
    <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">
      <introduction-text/>
    </el-col>
  </el-row>
  </div>
</template>

<script>
import IntroductionText from './components/IntroductionText'
import TeacherText from './components/TeacherText'
export default {
  name: 'IntroToComputer',
  components: {
    IntroductionText,
    TeacherText
  },
  data(){
    return{
      imagebox:[
        {id:0,idView:require('../../assets/box_images/box1.jpg')},
        {id:1,idView:require('../../assets/box_images/box2.jpg')},
        {id:2,idView:require('../../assets/box_images/box3.jpg')},
        {id:3,idView:require('../../assets/box_images/box4.jpg')}
      ],
      // 浏览器宽度
      screenWidth :0,
    }
  },
  methods:{
    setSize:function () {
      // 通过浏览器宽度(图片宽度)计算高度
      this.bannerHeight = 400 / 1920 * this.screenWidth;
    },
  },
  mounted() {
      // 首次加载时,需要调用一次
      this.screenWidth =  window.innerWidth;
      this.setSize();
      // 窗口大小发生改变时,调用一次
      window.onresize = () =>{
      this.screenWidth =  window.innerWidth;
      this.setSize();
    }
  }
  
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .dashboard {
    &-container {
      margin: 30px;
    }
    &-text {
      font-size: 30px;
      line-height: 46px;
    }
  }

  .el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
  }
  
  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }
  
  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }

  img{
    /*设置图片宽度和浏览器宽度一致*/
    width:100%;
    height:inherit;
  }
</style>
