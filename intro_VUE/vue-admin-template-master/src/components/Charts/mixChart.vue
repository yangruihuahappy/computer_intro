<template>
  <div :class="className" :id="id" :style="{height:height,width:width}"/>
</template>

<script>
import * as echarts from "echarts"
import resize from './mixins/resize'
import chapter from '@/api/intro/chapter'
import student from '@/api/intro/student'
import preview from '@/api/intro/practice/preview'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    id: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '200px'
    },
    height: {
      type: String,
      default: '200px'
    }
  },
  data() {
    return {
      chart: null,
      chapterList: [], //章节列表
      sectionList: [], //小节列表
      abscissaList: [], //横坐标List
      stuId: "1376870881904656385", //默认使用学生
      stuPreviewList: [],
      previewScoreList: [], //学生的课前预习分数列表
      homeworkScoreList: [], //学生的课后作业分数列表
      timedtestScoreList: [], //学生的课前预习分数列表
      experimentScoreList: [], //学生的课后作业分数列表
      current: 1,
      limit: 10,
      Query: {}, //查询体 防报错

    }
  },
  mounted() {
    this.initAbscissaList() 
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    //初始化章节
    //初始化一级章节目录  后续修改为进行到第几章第几节，不显示未发布的章节作业
    initAbscissaList(){
      student.getAllPracticeScore(this.stuId)
      .then(response =>{
        this.abscissaList = response.data.indexList
        this.previewScoreList = response.data.previewScoreList
        this.homeworkScoreList = response.data.homeworkScoreList
        this.timedtestScoreList = response.data.timedtestScoreList
        this.experimentScoreList = response.data.experimentScoreList
        console.log(this.abscissaList);
        console.log(this.timedtestScoreList);
        console.log(this.experimentScoreList);
        this.initChart(this.abscissaList,this.previewScoreList,this.homeworkScoreList,this.timedtestScoreList,this.experimentScoreList)
      }) 
    },

    //初始化图表
    initChart(abscissaList,previewScoreList,homeworkScoreList,timedtestScoreList,experimentScoreList) {
      console.log(abscissaList);
      console.log(previewScoreList);
      this.chart = echarts.init(document.getElementById(this.id))
      const xData = (function() { //放横坐标
        const data = []
        for (let i = 0; i < abscissaList.length; i++) {
          data.push(abscissaList[i]) //设置为第几章第几节
        }
        return data
      }())
      const previewData = previewScoreList
      this.chart.setOption({
        backgroundColor: '#344b58',
        title: {
          text: '我的成就', //放标题
          x: '20',
          top: '20',
          textStyle: {
            color: '#fff',
            fontSize: '22'
          },
          subtextStyle: {
            color: '#90979c',
            fontSize: '16'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            textStyle: {
              color: '#fff'
            }
          }
        },
        grid: {
          left: '5%',
          right: '5%',
          borderWidth: 0,
          top: 150,
          bottom: 95,
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          x: '5%',
          top: '10%',
          textStyle: {
            color: '#90979c'
          },
          data: ['Preview', 'Homework', 'Timedtest', 'Experiment']
        },
        calculable: true,
        xAxis: [{
          type: 'category',
          axisLine: {
            lineStyle: {
              color: '#90979c'
            }
          },
          splitLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          splitArea: {
            show: false
          },
          axisLabel: {
            interval: 0

          },
          data: xData
        }],
        yAxis: [{
          type: 'value',
          splitLine: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: '#90979c'
            }
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            interval: 0
          },
          splitArea: {
            show: false
          }
        }],
        dataZoom: [{ //下方滑动条
          show: true,
          height: 30,
          xAxisIndex: [
            0
          ],
          bottom: 30,
          start: 10,
          end: 80,
          handleIcon: 'path://M306.1,413c0,2.2-1.8,4-4,4h-59.8c-2.2,0-4-1.8-4-4V200.8c0-2.2,1.8-4,4-4h59.8c2.2,0,4,1.8,4,4V413z',
          handleSize: '110%',
          handleStyle: {
            color: '#d3dee5'

          },
          textStyle: {
            color: '#fff' },
          borderColor: '#90979c'

        }, {
          type: 'inside',
          show: true,
          height: 15,
          start: 1,
          end: 35
        }],
        series: [{
          name: 'Preview',
          type: 'line',
          stack: 'total',
          symbolSize: 10,
          symbol: 'circle',
          itemStyle: {
            normal: {
              color: 'rgba(255,144,128,1)',
              label: {
                show: true,
                textStyle: {
                  color: '#fff'
                },
                position: 'insideTop',
                formatter(p) {
                  return p.value > 0 ? p.value : ''
                }
              }
            }
          },
          data: previewScoreList
        },

        {
          name: 'Homework',
          type: 'line',
          stack: 'total',
          symbolSize: 10,
          symbol: 'circle',
          itemStyle: {
            normal: {
              color: 'rgba(0,191,183,1)',
              barBorderRadius: 0,
              label: {
                show: true,
                position: 'top',
                formatter(p) {
                  return p.value > 0 ? p.value : ''
                }
              }
            }
          },
          data: homeworkScoreList
        }, {
          name: 'Timedtest',
          type: 'line',
          stack: 'total',
          symbolSize: 10,
          symbol: 'circle',
          itemStyle: {
            normal: {
              color: 'rgba(252,230,48,1)',
              barBorderRadius: 0,
              label: {
                show: true,
                position: 'top',
                formatter(p) {
                  return p.value > 0 ? p.value : ''
                }
              }
            }
          },
          data: timedtestScoreList
        }, {
          name: 'Experiment',
          type: 'line',
          stack: 'total',
          symbolSize: 10,
          symbol: 'circle',
          itemStyle: {
            normal: {
              color: 'rgba(102,163,88,100)',
              barBorderRadius: 0,
              label: {
                show: true,
                position: 'top',
                formatter(p) {
                  return p.value > 0 ? p.value : ''
                }
              }
            }
          },
          data: experimentScoreList
        }
        ]
      })
    },
  }
}
</script>
