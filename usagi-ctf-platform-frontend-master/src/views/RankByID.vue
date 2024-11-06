<script setup>
import * as echarts from 'echarts/core';
import {
  TooltipComponent,
  GridComponent,
} from 'echarts/components';
import { BarChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { post } from '../utils/request';

echarts.use([
  GridComponent,
  TooltipComponent,
  BarChart,
  CanvasRenderer,
  // UniversalTransition
]);

var option;

// const rankList = ref([{
//   0: "name",
//   1: 123,
//   2: 234,
// }, {
//   0: "test",
//   1: 2623,
//   2: 23,
// }, {
//   0: "ser",
//   1: 34,
//   2: 626,
//   }])

// const rankList = ref([{
//   teamName: 'name',
//   total: 123,
//   data: [0, 23]
// }, {
//   teamName: 'name2',
//   total: 213,
//   data: [12, 0]
// }])
const rankList = ref([])



const props = defineProps(["gameID"]);
props.gameID;

const problemList = ref([])

var problemIDList = []


const chartColor = ['#D3A7F4', '#C1A7F4', '#B4A7F4', '#A7A9F4', '#A7B5F4', '#A7C4F4', '#A7D1F4', '#A7E2F4', '#A7F4F4', '#A7F4F4']
var myChart

const min = (a, b) => {
  return a < b ? a : b
}

const generateChartYData = () => {
  var chartData = []
  for (var i = 0; i < min(10, rankList.value.length); i++) {
    chartData.push({
      value: rankList.value[i].total,
      // 设置单个柱子的样式
      itemStyle: {
        color: chartColor[i]
      }
    })
  }
  return chartData
}

const generateChartXData = () => {
  var chartData = []
  for (var i = 0; i < min(10, rankList.value.length); i++) {
    chartData.push(rankList.value[i].teamName)
  }
  return chartData
}

const updateChart = () => {
  option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: generateChartXData()
    },
    yAxis: {
      type: 'value'
    },
    backgroundColor: 'transparent',
    series: [
      {
        data: generateChartYData(),
        type: 'bar'
      }
    ]
  };
  option && myChart.setOption(option);

}


const sortRankList = () => {
  let len = rankList.value.length
  for (var i = 1; i < len; i++)
  {
    for (var j = 0; j < len - i; j++)
    {
      if (rankList.value[j].total < rankList.value[j + 1].total)
      {
        var tmp = rankList.value[j]
        rankList.value[j] = rankList.value[j + 1]
        rankList.value[j+1] = tmp
      }
    }
  }

}
const analyzeRankHashMap = async (str) => {
  // "{4:{0:\"测试战队\",9:384},5:{0:\"汪汪队\",9:384,10:888,11:200,12:333,13:444,14:555}}"


  str = str.slice(1, -1)
  var strList = []
  var depth = 0
  for (let i = 0; i < str.length; i++) {
    if (str[i] === '{')
      depth++
    else if (str[i] === '}')
      depth--;
    if (depth === 0 && str[i] === ',') {
      strList.push(str.slice(0, i))
      str = str.slice(i + 1, str.length)
      i = -1;
    }
  }
  if (str !== '')
    strList.push(str)
  strList.forEach((value, index) => {
    value = value.split('{')[1]
    value = value.slice(0, -1)
    value = value.split(',')
    var tmp = {
      teamName: '',
      total: 0,
      data: []
    }
    tmp.data = Array.apply(null, { length: problemList.value.length })
    value.forEach((v, i) => {
      if (i === 0) {
        tmp.teamName = v.split(":")[1].slice(1, -1)
      }
      else {
        tmp.data[problemIDList.indexOf(Number(v.split(":")[0]))] = '已解出'
        tmp.total += Number(v.split(":")[1])
      }

    })
    rankList.value.push(tmp)
  })
  
  sortRankList()

  updateChart()
}


nextTick(() => {
  // 初始化 Chart
  var chartDom = document.getElementById('chart');
  myChart = echarts.init(chartDom, 'dark');


  post('/problems', { user: { username: localStorage.getItem('Username') }, game: { id: props.gameID } },
    (res) => {
      problemList.value = res.data.data.map((item) => {
        problemIDList.push(item.problemId)
        return item.problemName
      });
      
      post('/ranks', { username: localStorage.getItem('Username'), gameId: props.gameID },
        (res) => {
          analyzeRankHashMap(res.data.data)
        },
        (res) => {
          console.log(res)

        })
    },
    (err) => {
    }
  )

  window.addEventListener('resize', function () { // 监听窗口大小变化
    myChart.resize();
  });
})


// option = {
//   tooltip: {
//     trigger: 'axis'
//   },
//   legend: {},
//   grid: {
//     left: '3%',
//     right: '4%',
//     bottom: '3%',
//     containLabel: true
//   },
//   backgroundColor: 'transparent',
//   xAxis: {
//     type: 'category',
//     boundaryGap: false,
//     data: ['13:00', '13:10', '13:20', '13:30', '13:40', '13:50', '14:00']
//   },
//   yAxis: {
//     type: 'value'
//   },
//   series: data
// };


</script>

<template>
  <div class="container">
    <div id="chart"></div>
    <div class="list">
      <el-table class="table" :data="rankList">
        <el-table-column fixed="left" prop="teamName" label="战队名" width="180" />
        <el-table-column prop="total" label="总分" width="120" />
        <el-table-column v-for="(item, index) in problemList" :prop="`data[${index}]`" :label="item" width="120" />
      </el-table>

      <!-- <table>
        <tr>
          <td class="table-title">战队名</td>
          <td class="table-title">总分</td>
          <td class="table-title" v-for="(item, index) in problemList">{{ item }}</td>
        </tr>
        <tr v-for="(item, index) in rankList">
          <td style="color: #f0f0f0; font-weight: bold;">{{ item.teamName }}</td>
          <td style="color: #f0f0f0; font-weight: bold;">{{ item.total }}</td>
          <td v-for="(num, i) in item.data">{{ num !== 0 ? '已解出' : '' }}</td>
        </tr>
      </table> -->

    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

#chart {
  width: 60%;
  min-width: 800px;
  height: 300px;
}

.list .table {
  padding: 10px;
  border-radius: 10px;
  white-space: nowrap;
}

/* .list table {
  width: 60%;
  min-width: 800px;
  color: #f0f0f0;

  border-collapse: collapse;

  .table-title {
    font-weight: bold;
    color: #f0f0f0;
  }

  & td {
    padding: 8px 16px;
    text-align: left;
    color: #f0f0f0cc;
    border-bottom: 1px solid #ddd;
  }
} */
</style>
