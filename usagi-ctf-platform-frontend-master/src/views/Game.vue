<script setup>
import { ref } from 'vue'
import { post } from '../utils/request.js'
import router from '@/router'

const responseData = ref({
  "data": []
})
const url = '/games'
const isDark = ref(false)  // 控制按钮是否为深色(不要删掉，报warning)
const isSignUpDisabled = ref(false)  // 控制“报名参赛”按钮是否禁用
const isEnterDisabled = ref(false)  // 控制“进入比赛”按钮是否禁用
const showDialog = ref(-1)  // 控制报名小弹窗是否划出,默认-1,v-for的index从0开始
const isShowButtons = ref(false)  // 控制报名小弹窗内容是否显示
const user = ref(JSON.parse(localStorage.getItem('User')))  // 从浏览器缓存获取用户信息,JSON.parse()将字符串转为对象

// 存储上一道

// user = {
//         "teamName":"小小呆",
//         "phoneNumber":"18611168611",
//         "userIntro":null,
//         "teamId":-1,
//         "teamGames":[1,4],
//         "name":"郭凯辉",
//         "isCaptain":0,
//         "id":3,
//         "email":"670939375@qq.com",
//         "username":"BaiFu"
//       }

// 模拟从后端获取到的比赛列表数据
// responseData.value = {
//   "code": 200,
//   "message": "success",
//   "data": [
//     {
//       "id": 10,
//       "gameName": "NEWctf1",
//       "gameIntro": "A new ctf1.",
//       "timeStart": "1970-01-01T00:00:00.000+00:00",
//       "timeEnd": "1970-01-01T00:00:00.000+00:00",
//       "is_open": 0
//     },
//     {
//       "id": 30,
//       "gameName": "航江创建3",
//       "gameIntro": "CTF是一种流行的信息安全竞赛形式，其英文名可直译为夺得Flag，也可意译为夺旗赛。其大致流程是，参赛团队之间通过进行攻防对抗、程序分析等形式，率先从主办方给出的比赛环境中得到一串具有一定格式的字符串或其他内容，并将其提交给主办方，从而夺得分数。",
//       "timeStart": "2023/11/1 20:03:00",
//       "timeEnd": "2023/11/1 23:17:30",
//       "is_open": 0
//     }
//   ]
// }

// 从后端获取requestData
// 编辑请求信息
const requestData = ref({
  "username": user.value.username
})
// 发送请求
post(url, requestData.value,
  res => {
    responseData.value = res.data  // 将响应数据存储在响应式变量中
    console.log('获取比赛列表数据成功')
    console.log(res.data)
  },
  err => {
    console.error(err)
    console.log('获取比赛列表数据失败')
  }
)

// 报名比赛按钮点击事件
const gameSignUp = (index) => {
  if (showDialog.value == index) {
    // 点击按钮后，再次点击按钮，隐藏弹窗
    showDialog.value = showDialog.value == -1 ? index : -1
    // console.log(user.value)
  } else {
    // 不重复点击，原来的弹回去，当前的弹出来
    showDialog.value = index
  }
  // 在点击按钮后，先隐藏弹窗内容
  isShowButtons.value = false
}

// 弹窗动画完毕后，再显示弹窗内容
const showContent = () => {
  isShowButtons.value = true  // 显示弹窗内容
}

// 点击报名按钮，真正发送报名请求
const gameTrueSignUp = (item) => {
  if (user.value.isCaptain === 0) {
    ElMessage({
      showClose: true,
      message: '只有队长可以报名比赛',
      type: 'error'
    })
  } else {
    // 发送加入比赛请求
    post('/team/01', {
      "user": {
        "username": user.value.username
      },
      "team": {
        "id": user.value.teamId
      },
      "game": {
        "id": item.id
      }
    },
      res => {
        ElMessage({
          showClose: true,
          message: '报名成功',
          type: 'success'
        })
        // 报名成功后，将该比赛id加入到user.teamGames中
        user.value.teamGames.push(item.id)
        // 报名成功后，将修改后的user的值加入到localStorage中，覆盖掉原来的User
        localStorage.setItem('User', JSON.stringify(user.value))
        // 报名成功后，将弹窗收回去
        showDialog.value = -1
      },
      err => {
        ElMessage({
          showClose: true,
          message: '报名失败',
          type: 'error'
        })
        console.error(err)
      }
    )
  }
}

// 进入比赛
const gameEnter = (index) => {
  const id = responseData.value.data[index].id
  router.push(`/problem/${id}`)
  localStorage.setItem('gameId', id)
}

// 传入比赛开始与结束时间，判断现在是否在比赛时间内
const isGameTime = (start, end) => {
  let now = new Date();
  let timeStart = new Date(start);
  let timeEnd = new Date(end);
  if (now >= timeStart && now <= timeEnd) {
    return true
  } else {
    return false
  }
}

// 后端时间格式转为前端时间格式
// TODO: 写个博客研究一下
const timeFormat = (str) => {
  var date = new Date(str)

  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  month = month < 10 ? ('0' + month) : month;
  var day = date.getDate();
  day = day < 10 ? ('0' + day) : day;
  var hour = date.getHours();
  hour = hour < 10 ? ('0' + hour) : hour;
  var minute = date.getMinutes();
  minute = minute < 10 ? ('0' + minute) : minute;
  var second = date.getSeconds();
  second = second < 10 ? ('0' + second) : second;
  return year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
}

</script>

<template>
  <!-- 如果没有加入队伍，不显示比赛列表 -->
  <div v-if="user.teamId === -1">
    <h2 class="gotoUserCenter">请先前往个人中心 加入/创建 队伍</h2>
    <img class="gotoUserCenterImg" src="../assets/usagiRabbit/趴着.png" alt="">
  </div>
  <div class="container" v-else>
    <h2>Hi，<span style="font-size:large;">来自队伍 </span>{{ user.teamName }}<span style="font-size:large;"> 的 </span>{{
      user.username }}</h2>
    <div v-if="responseData.data.length !== 0" class="cardContainer" v-for="(item, index) in responseData.data"
      :key="index">
      <div class="card">
        <p class="note">{{ item.gameName }}</p>
        <p>{{ item.gameIntro }}</p>
        <p style="opacity: .8;">比赛时间：{{ timeFormat(item.timeStart) }} 至 {{ timeFormat(item.timeEnd) }}</p >
        <div class="button">
          <!-- 判断按钮是否允许点击，不允许使用el-tooltip在下方谈一个提示框 -->
          <el-tooltip v-if="user.teamGames.includes(item.id)" content="你已报名该比赛">
            <el-button color="#404afc" :dark="isDark" :disabled="user.teamGames.includes(item.id)">报名参赛</el-button>
          </el-tooltip>
          <el-button v-else color="#404afc" :dark="isDark" :disabled="isSignUpDisabled"
            @click="gameSignUp(index)">报名参赛</el-button>
          <el-tooltip v-if="!user.teamGames.includes(item.id)" content="请先报名该比赛">
            <el-button color="#404afc" :dark="isDark" :disabled="!user.teamGames.includes(item.id)">进入比赛</el-button>
          </el-tooltip>
          <el-tooltip v-else-if="!isGameTime(item.timeStart,item.timeEnd)" content="不在比赛时间区间内">
            <el-button color="#404afc" :dark="isDark" :disabled="!isGameTime(item.timeStart,item.timeEnd)">进入比赛</el-button>
          </el-tooltip>
          <el-button v-else color="#404afc" :dark="isDark" :disabled="isEnterDisabled"
            @click="gameEnter(index)">进入比赛</el-button>
        </div>
      </div>

      <!-- 报名比赛按钮点击事件给showDialog赋值为当前index，从而控制选中项显示 -->
      <div class="dialog" :class="{ 'move-down': showDialog == index }" @transitionend="showContent">
        <!-- transition作用是让v-if满足以后不要突然出现，给个渐变动画 -->
        <transition name="fade">
          <!-- 在showDialog == index前提下，dialog动画完毕后，将isShowButtons赋值为true -->
          <div v-if="isShowButtons && showDialog == index" class="dialogContent">
            <p>只有队长可以报名比赛，确定要带领队伍参赛吗？</p>
            <img src="../assets/usagiRabbit/仰望天空.png">
            <img src="../assets/usagiRabbit/仰望天空.png">
            <el-button color="#404afc" :dark="isDark" @click="gameTrueSignUp(item)">报 名</el-button>
            <el-button color="#404afc" :dark="isDark" @click="gameSignUp(index)">再想想</el-button>
          </div>
        </transition>
      </div>
    </div>
    <div v-if="responseData.data.length === 0">
      <h2 style="text-align: center;">暂无比赛</h2>
      <img class="haveNoGameImg" src="../assets/usagiRabbit/趴着.png" alt="">
    </div>
  </div>
</template>

<style scoped>
/* 提醒前往个人中心文字样式 */
.gotoUserCenter {
  text-align: center;
  color: #fff;
}

/* 提醒前往个人中心图片样式 */
.gotoUserCenterImg {
  display: block;
  margin-left: auto;
  margin-right: auto;
  width: 50%;
  height: 50%;
  max-width: 500px;
}

/* 没有比赛时的图片样式 */
.haveNoGameImg {
  display: block;
  margin-left: auto;
  margin-right: auto;
  width: 50%;
  height: 50%;
  max-width: 500px;
}

.dialogContent {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
}

.dialogContent p {
  margin: 0;
  color: white;
  display: block;
  padding-top: 15px;
  padding-bottom: 15px;
  font-size: large;
}

.dialogContent img:first-of-type {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
}

.dialogContent img:nth-of-type(2) {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  transform: scaleX(-1);
}

.dialogContent .el-button:first-of-type {
  margin-right: 50px;
}

/* 弹窗内容淡入淡出动画 */
.fade-enter-active {
  transition: opacity 0.2s;
}

.fade-enter-from {
  opacity: 0;
}

.fade-enter-to {
  opacity: 1;
}

.fade-leave-active {
  transition: opacity 0.35s;
}

.fade-leave {
  opacity: 1;
}

.fade-leave-to {
  opacity: 0;
}


/* 弹窗每点击之前高度为0 */
.dialog {
  width: 750px;
  height: 50px;
  margin-left: auto;
  margin-right: auto;
  height: 0;
  transition: height 0.35s;
}

/* 弹窗点击之后高度变为100，内容开始显示 */
.move-down {
  height: 100px;
}

/* 自定义按钮disabled时的样式 */
.el-button[disabled] {
  color: #cfd3dc !important;
  background-color: transparent !important;
  border-color: #cfd3dc !important;
}

/* 中心弹窗样式 */
.middleDialog {
  /* 位置样式 */
  position: fixed;
  /* 只是左上角居中了，还需要transform: translate(-50%, -50%) */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  /* 其他样式 */
  background-color: #fff;
  width: 300px;
  height: 300px;
  color: black;
}

.container {
  margin: 0 auto;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  /* align-items: center; */
  color: var(--text-color);
  /* background-color: #f5f5f5; */
}

.cardContainer {
  margin-bottom: 20px;
}

.card {
  width: 800px;
  padding: 20px;
  box-sizing: border-box;
  border-radius: 30px 0 30px 0;
  border: var(--card-border);
  transition: border-color .25s;
}

.card:hover {
  border: var(--card-border-hover);
}

.card .note {
  width: 150px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: -22px;
  margin-left: -22px;
  border-radius: 30px 0 30px 0;
  font-weight: bold;
  font-size: var(--font-size-medium);
  /* background-color: rgba(7, 111, 240, 0.5); */
  background-image: linear-gradient(0deg,
      var(--color-primary-light), var(--color-primary));
}
</style>
