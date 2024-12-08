<script setup>
import ProblemLeft from '../components/ProblemLeft.vue'
import ProblemMiddle from '../components/ProblemMiddle.vue'
import ProblemRight from '../components/ProblemRight.vue'
import { ref } from 'vue'
import { post } from '../utils/request.js'

const user = ref(JSON.parse(localStorage.getItem('User')))  // 从浏览器缓存获取用户信息,JSON.parse()将字符串转为对象
const problemList = ref({
  "data": [] // 给一个默认初始值，有可能界面加载要快于请求发送，导致此时数据为空，界面渲染报错
}) // 记录获取题目列表信息

// 向后端获取题目列表的请求信息
const requestDataProblemList = ref({
  user: {
    username: user.value.username
  },
  game: {
    id: Number(localStorage.getItem('gameId'))
  }
})
// {"user":{"username":"adminb"},"game":{"id":5}}

// 从后端获取problemList
post('/problems', requestDataProblemList.value,
  res => {
    problemList.value = res.data;
    problemList.value.data.map((value)=>{
      value.gameId = Number(localStorage.getItem('gameId'))
      return value
    })
    console.log('获取题目列表信息成功');
    console.log(problemList.value);
  },
  err => {
    console.error(err);
    console.log('获取题目列表信息失败');
    problemList.value={
      "data": [] // 失败了给一个空空的数据，要不然题目列表组件都用了这里面的数据，会报错
    }
  }
)

// 将problemList存储在浏览器缓存中，方便ProblemMiddle组件获取，进行相应题目展示
// localStorage.setItem('problemList', JSON.stringify(problemList.value));

const selectedIndex = ref(-1)
// 定义变化监听函数，当selectedIndex变化时，更新父组件的selectedIndex
const updateSelectedIndex = (newIndex) => {
  selectedIndex.value = newIndex;
}


// 定义变化监听函数，当problemList变化时，更新父组件的problemList
const updateProblemList = (newProblemList) => {
  problemList.value = newProblemList;
}

// 模拟的一个假数据
// problemList.value ={
// 	"code": 500,
// 	"message": "success",
// 	"data": [
// 		{
// 			"gameId": 2,
// 			"tableName": null,
// 			"problemId": 396272777916227,
// 			"problemName": "测试题目1",
// 			"problemType": "Crypto",
// 			"problemIntro": "绝大多数情况下，本平台 flag 形式is_your_flag}，请提交包含flag{}的完整flag，来进行得分；如果flag是其他形式，题目中会阐明；",
// 			"problemEnvi": "hidden",
// 			"problemAtta": "https://www.bilibili.com/",
// 			"oriPts": 1000,
// 			"solved": "12,45,89,45",
// 			"flag": "调十表容类已",
// 			"beSolved": false,
// 			"open": true
// 		},
//     {
// 			"gameId": 2,
// 			"tableName": null,
// 			"problemId": 396272777916227,
// 			"problemName": "测试题目2",
// 			"problemType": "Web",
// 			"problemIntro": "斗将市料",
// 			"problemEnvi": "力天化无程界",
// 			"problemAtta": "习例候合区",
// 			"oriPts": 1000,
// 			"solved": "54,21",
// 			"flag": "调十表容类已",
// 			"beSolved": true,
// 			"open": false
// 		},
// 	]
// }
</script>

<template>
  <div class="container">
    <ProblemLeft :selectedIndex="selectedIndex" :problemList="problemList" @update:selectedIndex="updateSelectedIndex"/>
    <ProblemMiddle class="middle" :selectedIndex="selectedIndex" :problemList="problemList" @update:problemList="updateProblemList"/>
    <!-- <div style="color: aliceblue;">{{ selectedIndex }}</div> -->
    <ProblemRight :problemList="problemList"/>
  </div>
</template>

<style scoped>
.container {
  margin:0 auto;
  display: flex;
  max-width: 2200px;
}

</style>