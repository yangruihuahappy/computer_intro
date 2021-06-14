<template>
    <div class="dashboard-container">
        <el-row :gutter="20">
            <el-col :span="12">
                <div>
                    <h2>讲师介绍</h2>
                    <h3>宋华珠</h3>
                    <p>
                        武汉理工大学计算机学院
                    </p>
                    <p>
                        博士，副教授
                    </p>
                </div>
            </el-col>
            <el-col :span="12">
                <el-card class="box-card-component" style="margin-left:10px;" :body-style="{ padding: '0px' }">
                    <div  class="box-card-header">
                    <img src="../../../assets/static_images/teacher.jpg" 
                        style="CURSOR: hand" 
                        onclick='window.open("https://detail.tmall.com/item.htm?spm=a230r.1.14.33.72b015e5DGxp4a&id=44400084985&ns=1&abbucket=19")' >>
                    </div>
                </el-card>                    
            </el-col>
        </el-row>
        <el-row :gutter="10">
            <el-col :span="24">
                <div>
                    主要研究方向
                    <ul>
                        <li>智能方法</li> 
                　　    <li>机器学习、深度学习、数据挖掘与大数据分析</li> 
                　　    <li>语义及本体、自然语言处理与知识图谱、数字图像处理</li> 
                    </ul>
                </div>
                <div>
                <p>
                    曾发表专著2本、获得国家发明专利4项、软件著作权20多项，发表论文40多篇；
                    <br>
                    曾获得湖北省科学技术二等奖(排名5)一次;
                    <br>
                    湖北省自然科学优秀学术论文二等奖一次;
                    <br>
                    全国高等学校计算机课件评比二等奖多次。 
                </p>
                </div>
            </el-col>
        </el-row>

    </div>
</template>

<script>
export default {

}
</script>


<style rel="stylesheet/scss" lang="scss" scoped>
.box-card-component {
  .box-card-header {
    position: relative;
    height: 220px;
    img {
      width: 100%;
      height: 100%;
      transition: all 0.2s linear;
      &:hover {
        transform: scale(1.1, 1.1);
        filter: contrast(130%);
      }
    }
  }
}
</style>