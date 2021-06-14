<template>
<div :class="className" :style="{height:height,width:width}">
</div>
  

</template>

<script>

//引入题目topic.js
import topic from '@/api/intro/topic/topic'
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils'

export default {
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      chapterInfo: [], //题目集
      chapterData: [], //数量集
      toShowDate: [],
    }
  },
  mounted() {
    this.initData()
    this.__resizeHandler = debounce(() => {
      if (this.chart) {
        this.chart.resize()
      }
    }, 100)
    window.addEventListener('resize', this.__resizeHandler)
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    window.removeEventListener('resize', this.__resizeHandler)
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initData(){ //初始化数据
      topic.findCountPerChapter().then( response=>{
        this.chapterInfo = response.data.chapterInfo
        this.chapterData = response.data.chapterData
        
        for(let i=0;i<this.chapterInfo.length;i++){
          var temp = {value: 0,name:""}
          temp.value = this.chapterData[i]
          temp.name = this.chapterInfo[i]
          this.toShowDate.push(temp)
        }
    
        console.log(this.toShowDate);
        this.initChart()
      })
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          // data: ['Industries', 'Technology', 'Forex', 'Gold', 'Forecasts']
          data: this.chapterInfo
        },
        calculable: true,
        series: [
          {
            name: 'WEEKLY WRITE ARTICLES',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            // data: [
            //   { value: 320, name: 'Industries' },
            //   { value: 240, name: 'Technology' },
            //   { value: 149, name: 'Forex' },
            //   { value: 100, name: 'Gold' },
            //   { value: 59, name: 'Forecasts' }
            // ],
            data: this.toShowDate,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
