<script setup>
import { ref, watch } from 'vue'
import { Flag, Eleme, Download, MagicStick } from '@element-plus/icons-vue'
import { post, del } from '../utils/request.js'

const dialogVisible = ref(false) // 控制题目详情弹窗是否显示
const flagInput = ref('') // 题目详情弹窗中的flag输入框
const isFlagSubmit = ref(false) // 题目详情弹窗中的flag提交按钮
const user = ref(JSON.parse(localStorage.getItem('User')))  // 从浏览器缓存获取用户信息,JSON.parse()将字符串转为对象
const selectedIndexArray = ['Misc', 'Crypto', 'Pwn', 'Web', 'Reverse'] // 用于存储选中的题目类型的索引
// user = {
//         "teamName":"小小呆",
//         "phoneNumber":"18611168611",
//         "userIntro":null,
//         "teamId":-1,
//         "name":"郭凯辉",
//         "isCaptain":0,
//         "id":3,
//         "email":"670939375@qq.com",
//         "username":"BaiFu"
//       }

// 获取父组件传递的参数，并且随父组件进行更新
const props = defineProps(["selectedIndex", "problemList"])
const selectedIndex = ref(props.selectedIndex)
const problemList = ref(props.problemList)
const problemListNow = ref(problemList.value.data) // 临时储存选中的题目类型的题目列表

watch(props, async (newName, oldName) => {
  problemList.value = newName.problemList
  selectedIndex.value = newName.selectedIndex
  if (selectedIndex.value === -1) {
    problemListNow.value = problemList.value.data
  } else {
    problemListNow.value = problemList.value.data.filter(item => item.problemType === selectedIndexArray[selectedIndex.value])
  }
})

// 点击进入题目详情
const leftTime = ref(0)
const enterProblemInfo = ref({})
const enterProblem = (item) => {
  if (item.isOpen===0) {
    ElMessage({
      message: '该题目暂未开放',
      type: 'warning',
    })
  } else {
    enterProblemInfo.value = item
    dialogVisible.value = true

    // 获取当前题目的靶机剩余时间(如果开启的话)
    post('/container',
      {
        "user": {
          "username": JSON.parse(localStorage.getItem('User')).username
        },
        "problem": {
          "gameId": enterProblemInfo.value.gameId,
          "problemId": enterProblemInfo.value.problemId
        }
      },
      res => {
        leftTime.value = res.data.data.leftTime
        clearInterval(myTimeDisplay)//销毁之前定时器
        var myTimeDisplay = setInterval(() => {
          leftTime.value--
          if (leftTime.value <= 0) {
            clearInterval(myTimeDisplay)
          }
        }, 1000)
      },
      err => {
        console.log('当前靶机未开启，获取不了剩余时间')
      }
    )
  }
}

// 提交flag按钮点击事件
const flagSubmit = () => {
  // console.log(enterProblemInfo.value)
  if (enterProblemInfo.value.beSolved) {
    ElMessage({
      message: '队伍已解出该题目',
      type: 'warning',
    })
  } else {
    isFlagSubmit.value = true
    console.log('1秒后flag将提交')
    // 给1s延迟，防止连续点击提交按钮
    setTimeout(() => {
      // flag提交，请求发送
      post('/flag',
        {
          "user": {
            "username": JSON.parse(localStorage.getItem('User')).username
          },
          "problem": {
            "gameId": Number(localStorage.getItem('gameId')),
            "problemId": enterProblemInfo.value.problemId,
            "flag": flagInput.value
          }
        },
        res => {
          if(res.data.message=="Flag 正确"){
            ElMessage({
              message: '恭喜，flag正确！',
              type: 'success',
            })
            isFlagSubmit.value = false
            // 提交成功后更新题目列表
            post('/problems',
              {
                "user": {
                  "username": JSON.parse(localStorage.getItem('User')).username
                },
                "game": {
                  "id": Number(localStorage.getItem('gameId'))
                }
              },
              res => {
                console.log('题目列表更新成功')
                problemList.value = res.data
                if (selectedIndex.value === -1) {
                  problemListNow.value = problemList.value.data
                } else {
                  problemListNow.value = problemList.value.data.filter(item => item.problemType === selectedIndexArray[selectedIndex.value])
                }
              },
              err => {
                console.error(err)
                console.log('题目列表更新失败')
              }
            )
            // 关闭弹窗
            dialogVisible.value = false
            
          }else{
            ElMessage({
              message: 'flag错误！',
              type: 'error',
            })
            isFlagSubmit.value = false
          }
        },
        err => {
          ElMessage({
            message: '提交出现错误',
            type: 'error',
          })
          isFlagSubmit.value = false
        }
      )
    }, 1000)
  }
}

// 下载附件按钮点击事件
const downloadProblemAtta = () => {
  window.open(enterProblemInfo.value.problemAtta, '_blank')
}

// 启动靶机按钮点击事件
const containerLink = ref('') // 用来记录靶机链接
const startContainer = () => {
  // 销毁之前开启的容器
  if(JSON.parse(localStorage.getItem('whichProblemOpen'))!==null){
    del('/container',
    {
      "user": {
        "username": JSON.parse(localStorage.getItem('User')).username
      },
      "problem": {
        "gameId": JSON.parse(localStorage.getItem('whichProblemOpen')).gameId,
        "problemId": JSON.parse(localStorage.getItem('whichProblemOpen')).problemId
      }
    },
    res => {
      ElMessage({
        message: '已销毁其他靶机',
        type: 'success',
      })
      // 销毁成功后，清空localStorage中whichProblemOpen储存的比赛以及题目id
      localStorage.setItem('whichProblemOpen', JSON.stringify(
          {
            "gameId": null,
            "problemId": null
          }
        )
      )
    },
    err => {
      ElMessage('销毁其他靶机失败')
      console.error(err)
      console.log('其他靶机销毁失败')
    })
  }

  // 开启当前题目的容器
  post('/container',
    {
      "user": {
        "username": JSON.parse(localStorage.getItem('User')).username
      },
      "problem": {
        "gameId": enterProblemInfo.value.gameId,
        "problemId": enterProblemInfo.value.problemId
      }
    },
    res => {
      ElMessage({
        message: '当前靶机启动成功',
        type: 'success',
      })
      containerLink.value = 'http://e.linjhs.top:[' + res.data.data.port + ']'
      // 记录当前开启的靶机的比赛与题目id
      localStorage.setItem('whichProblemOpen', JSON.stringify(
          {
            "gameId": Number(localStorage.getItem('gameId')),
            "problemId": enterProblemInfo.value.problemId
          }
        )
      )
    },
    err => {
      ElMessage({
        message: '当前靶机启动失败',
        type: 'warning',
      })
      console.error(err)
      console.log('当前靶机启动失败')
    }
  )
}

// 靶机链接跳转点击
const gotoContainer = () => {
  window.open(containerLink.value)
}

// 重启靶机链接点击事件
const reopenContainer=()=>{
    // 销毁之前开启的容器
    if(JSON.parse(localStorage.getItem('whichProblemOpen'))!==null){
    del('/container',
    {
      "user": {
        "username": JSON.parse(localStorage.getItem('User')).username
      },
      "problem": {
        "gameId": JSON.parse(localStorage.getItem('whichProblemOpen')).gameId,
        "problemId": JSON.parse(localStorage.getItem('whichProblemOpen')).problemId
      }
    },
    res => {
      ElMessage({
        message: '已销毁其他靶机',
        type: 'success',
      })
      // 销毁成功后，清空localStorage中whichProblemOpen储存的比赛以及题目id
      localStorage.setItem('whichProblemOpen', JSON.stringify(
          {
            "gameId": null,
            "problemId": null
          }
        )
      )
    },
    err => {
      ElMessage('销毁其他靶机失败')
      console.error(err)
      console.log('其他靶机销毁失败')
    })
  }

  // 开启当前题目的容器
  post('/container',
    {
      "user": {
        "username": JSON.parse(localStorage.getItem('User')).username
      },
      "problem": {
        "gameId": enterProblemInfo.value.gameId,
        "problemId": enterProblemInfo.value.problemId
      }
    },
    res => {
      ElMessage({
        message: '当前靶机启动成功',
        type: 'success',
      })
      containerLink.value = 'http://e.linjhs.top:[' + res.data.data.port + ']'
      // 记录当前开启的靶机的比赛与题目id
      localStorage.setItem('whichProblemOpen', JSON.stringify(
          {
            "gameId": Number(localStorage.getItem('gameId')),
            "problemId": enterProblemInfo.value.problemId
          }
        )
      )
    },
    err => {
      ElMessage({
        message: '当前靶机启动失败',
        type: 'warning',
      })
      console.error(err)
      console.log('当前靶机启动失败')
    }
  )
}

// 删除靶机按钮点击事件
const deleteContainer=()=>{
  del('/container',
    {
      "user": {
        "username": JSON.parse(localStorage.getItem('User')).username
      },
      "problem": {
        "gameId": enterProblemInfo.value.gameId,
        "problemId": enterProblemInfo.value.problemId
      }
    },
    res => {
      ElMessage({
        message: '靶机删除成功',
        type: 'success',
      })
      // 删除成功后，清空localStorage中whichProblemOpen储存的比赛以及题目id
      localStorage.setItem('whichProblemOpen', JSON.stringify(
          {
            "gameId": null,
            "problemId": null
          }
        )
      )
    },
    err => {
      ElMessage({
        message: '靶机删除失败',
        type: 'warning',
      })
      console.error(err)
      console.log('靶机删除失败')
    })
}

// 判断当前题目的开启靶机按钮是否可以点击
const canOpenContainer=()=>{
  if(enterProblemInfo.value.problemEnvi == 'hidden'){// 如果是容器题，有靶机
    if(JSON.parse(localStorage.getItem('whichProblemOpen'))===null){// 如果当前没有靶机开启
      return true
    }else if(JSON.parse(localStorage.getItem('whichProblemOpen')).problemId!== enterProblemInfo.value.problemId){// 如果当前有靶机开启，但不是当前题目的靶机
      return true
    }else{// 如果当前有靶机开启，且是当前题目的靶机
      return false
    }
  }else{// 如果不是容器题，没有靶机
    return false
  }
}

// 判断当前题目的靶机链接是否显示
const canLinkShow=()=>{
  if(enterProblemInfo.value.problemEnvi === 'hidden'){// 如果是容器题，有靶机
    if(JSON.parse(localStorage.getItem('whichProblemOpen'))===null){// 如果当前没有靶机开启
      return false
    }else if(JSON.parse(localStorage.getItem('whichProblemOpen')).problemId!== enterProblemInfo.value.problemId){// 如果当前有靶机开启，但不是当前题目的靶机
      return false
    }else{// 如果当前有靶机开启，且是当前题目的靶机
      return true
    }
  }else{// 如果不是容器题，没有靶机
    return false
  }
}
</script>

<template>
  <div class="containerM">
    <!-- <div style="color: aliceblue;">{{ selectedIndex }}</div> -->
    <el-scrollbar wrap-style="overflow-x:hidden;">
      <el-row :gutter="0">
        <el-col class="elcol" v-for="(item, index) in problemListNow" :key="index" :xs="24" :sm="24" :md="12" :lg="8"
          :xl="6">
          <!-- 根据selectedIndex确认middle中间展示的题目类型 -->
          <div class="BoxContainer">
            <div class="box" :class="item.beSolved ? 'box-solved' : ''">
              <div class="imgBx">
                <div style="font-weight: bold;margin-left: 30px;">{{ item.problemName }}</div>
                <img v-if="item.problemType === 'Misc'" src="../assets/usagiRabbit/题目背景Misc.png">
                <img v-else-if="item.problemType === 'Crypto'" src="../assets/usagiRabbit/题目背景Crypto.png">
                <img v-else-if="item.problemType === 'Pwn'" src="../assets/usagiRabbit/题目背景Pwn.png">
                <img v-else-if="item.problemType === 'Web'" src="../assets/usagiRabbit/题目背景Web.png">
                <img v-else-if="item.problemType === 'Reverse'" src="../assets/usagiRabbit/题目背景Reverse.png">
              </div>
              <div class="glass">
                <!--加br，可以实现换行效果 <h3>{{ item.problemName }}<br/><span>题目现在的分值</span></h3> -->
                <div class="glassProblemName">{{ item.problemName }}</div>
                <div class="glassProblemIntro"><span class="title">题目简介：</span>{{ item.problemIntro }}</div>
                <div class="glassProblemGrade">
                  <span>做出队伍数：<span style="font-size: large;font-weight: bold;">{{ item.solved===''?0:item.solved.split(',').length
                  }}</span></span>
                  <span>当前分值：<span style="font-size: large;font-weight: bold;">{{ Math.floor(item.oriPts / 20 +
                    (item.oriPts - item.oriPts / 20) / (Math.log2(item.solved.split(',').length + 3) - 1))
                  }}</span></span>
                </div>
                <!-- 写入选项 -->
                <div class="enterProblem" @click="enterProblem(item)">题目详情</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-scrollbar>

    <!-- 点击题目详情后，弹出可拖动弹窗 -->
    <el-dialog v-model="dialogVisible" class="dialog" width="50%" draggable center :close-on-click-modal="false"
      :close-on-press-escape="false">
      <!-- 弹窗头部 -->
      <template #header>
        <div class="header">
          <div style="font-size: larger;font-weight:bolder;margin-top: -13px;">{{ enterProblemInfo.problemName }}</div>
        </div>
      </template>

      <!-- 弹窗正文 -->
      <div><span style="font-weight: bold;">题目ID：</span>{{ enterProblemInfo.problemId }}</div>
      <div><span style="font-weight: bold;">题目类型：</span>{{ enterProblemInfo.problemType }}</div>
      <div style="display: flex;justify-content: flex-start;">
        <div style="font-weight: bold;white-space: nowrap;">题目简介：</div>
        <div>{{ enterProblemInfo.problemIntro }}</div>
      </div>
      <div style="display: flex; justify-content: flex-start">
        <div style="margin-right: 15px;"><span style="font-weight: bold;">初始分值：</span>{{ enterProblemInfo.oriPts }}</div>
        <div style="margin-right: 15px;"><span style="font-weight: bold;">当前分值：</span>{{
          Math.floor(enterProblemInfo.oriPts / 20 + (enterProblemInfo.oriPts - enterProblemInfo.oriPts / 20) /
            (Math.log2(enterProblemInfo.solved.split(',').length + 3) - 1)) }}</div>
        <div><span style="font-weight: bold;">做出队伍数：</span>{{ enterProblemInfo.solved===''?0:enterProblemInfo.solved.split(',').length }}</div>
      </div>
      <!-- 靶机部分 -->
      <div class="startContainer">
        <!-- 如果该题目有容器并且容器没开启，启动靶机按钮可以点 -->
        <el-button plain
          :disabled="!canOpenContainer()"
          @click="startContainer">
          <el-icon>
            <MagicStick />
          </el-icon>
          启动靶机
        </el-button>
        <el-text style="margin-left: 10px;" v-if="canLinkShow()">靶机链接：</el-text>
        <el-link @click="gotoContainer" v-if="canLinkShow()">{{ containerLink }}</el-link>
        <el-tooltip content="时间变为0可按下按钮重启靶机" v-if="canLinkShow()">
          <el-button style="margin-right: 5px;" 
            :disabled="leftTime !== 0" @click="reopenContainer">靶机剩余时间: {{ leftTime }}s</el-button>
        </el-tooltip>
        <el-button type="danger" plain v-if="canLinkShow()" @click="deleteContainer">删除靶机</el-button>
      </div>
      <!-- 附件下载部分 -->
      <div class="downloadProblemAtta">
        <el-button plain :disabled="(enterProblemInfo.problemAtta === null)" @click="downloadProblemAtta">
          <el-icon>
            <Download />
          </el-icon>
          下载附件
        </el-button>
      </div>
      <!-- 弹窗底部 -->
      <template #footer>
        <el-input v-model="flagInput" placeholder="提交包含 flag{} 的完整flag..." clearable>
          <template #append>
            <el-button v-if="!isFlagSubmit" @click="flagSubmit">提交</el-button>
            <el-button v-else type="primary" :loading-icon="Eleme" loading
              style="display: flex;align-items: center;">提交中</el-button>
          </template>
        </el-input>
      </template>

    </el-dialog>
  </div>
</template>

<style scoped>
.startContainer {
  margin-top: 10px;
  margin-bottom: 10px;
}

/* 题目卡片毛玻璃标题 */
.glassProblemName {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  margin-top: 10px;
  text-align: center;
}

/* 题目卡片毛玻璃简介 */
.glassProblemIntro {
  /* 以下样式用来控制显示简介文字的数量 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  /* 限制行数 */
  -webkit-box-orient: vertical;
  /* 文字样式 */
  color: #ffffff95;
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 3px;

  .title {
    color: #fff;
  }
}

/* 题目卡片毛玻璃分值 */
.glassProblemGrade {
  text-align: center;
  position: absolute;
  bottom: 50px;
  left: 15px;
  color: #fff;
}

.glassProblemGrade span:first-child {
  margin-right: 10px;
}

/* 题目卡片鼠标悬浮后展示的比赛详情按钮 */
.enterProblem {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 40px;
  color: white;
  text-align: center;
  font-weight: 400;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  /* 鼠标悬浮后的样式 */
  &:hover {
    background-color: rgba(255, 255, 255, 0.2);
  }
}


/* 隐藏横向滚动条 */
:deep(.el-scrollbar__bar.is-horizontal) {
  height: 0 !important;
}

.containerM {
  /* background-color: red; */
  padding-right: 30px;
  min-width: 230px;
  /* 响应式增长 */
  height: calc(100vh - 210px);
  min-height: 700px;
  flex: 1;
}

.elcol {
  padding-left: 10px;
  padding-right: 20px;
  padding-top: 10px;
  padding-bottom: 10px;
}

/* 以下为小卡默认样式 */
.BoxContainer {
  user-select: none;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
}

.BoxContainer .box {
  position: relative;
  width: 300px;
  height: 180px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: .5s;

  &.box-solved::after {
    content: '已解出';
    color: #f0f0f0;
    text-align: center;
    position: absolute;
    top: 20px;
    right: -25px;
    width: 120px;
    line-height: 30px;
    height: 30px;
    /* background-color: #267d26; */
    background-color: #6427ea;
    rotate: 45deg;
    clip-path: polygon(0 100%, 50% -100%, 100% 100%);

  }
}

.BoxContainer .box .imgBx {
  position: absolute;
  inset: 0;
  border-radius: 12px;
  border: 4px solid rgba(0, 0, 0, .2);
}

.BoxContainer .box .imgBx img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: .5s;
}

.BoxContainer .box:hover .imgBx img {
  opacity: .5;
}

.BoxContainer .box .glass {
  z-index: 100;
  position: absolute;
  inset: 0;
  background: linear-gradient(#fff2, transparent);
  border: 1px solid rgba(255, 255, 255, .1);
  box-shadow: 0 15px 15px rgba(0, 0, 0, .25);
  backdrop-filter: blur(15px);
  border-radius: 10px;
  /* display: flex; */
  /* justify-content: center;
  align-items: center; */
  overflow: hidden;
  scale: 0;
  opacity: 0;
  transition: .5s;
}

.BoxContainer .box .glass::before {
  content: '';
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 40px;
  background-color: rgba(255, 255, 255, .05);
}

.BoxContainer .box:hover {
  transform: rotate(-15deg);
  transition: all 0.2s;
}

.BoxContainer .box:hover .glass {
  transform: rotate(20deg);
  transition: all 0.2s;
  scale: 1;
  opacity: 1;
}

.BoxContainer .box .glass h3 {
  font-size: 1.25em;
  color: #fff;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: .1em;
  text-align: center;
  line-height: 0.8em;
}

.BoxContainer .box .glass h3 span {
  font-weight: 400;
  font-size: .5em;
}

.BoxContainer .box ul {
  position: absolute;
  bottom: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 以下为小卡自定义样式 */
.imgBx>div {
  color: white;
  font-weight: 400;
  font-size: 20px;
  margin-left: 30px;
  margin-top: 10px;
}
</style>

<style>
/* 由于vue3的bug，无法通过深度选择器修改dialog的样式，
  故新建一个非scoped的style，用来修改其样式*/

.dialog {
  border-radius: 11px;
  min-width: 500px;
}

/*标题背景色*/
.dialog .el-dialog__header {
  background: linear-gradient(to right,
      rgba(4, 9, 137, 1),
      rgba(67, 3, 131, 1),
      rgba(3, 6, 104, 1));
  /* 去除默认右边距 */
  margin-right: 0;
  color: white;
  border-top-left-radius: 9px;
  border-top-right-radius: 9px;
  height: 10px;
  filter: brightness(1);
  transition: filter 0.25s;
}

/*标题背景色鼠标悬浮后*/
.dialog .el-dialog__header:hover {
  /* background会变成图片，用transition跟animation不能改变 */
  filter: brightness(1.4);
  /* background: linear-gradient(to right,
    rgba(4, 9, 137, 0.8),
    rgba(67, 3, 131, 0.8),
    rgba(3, 6, 104, 0.8) 
    );*/
}

/*body背景色*/
.dialog .el-dialog__body {
  background-color: white;
}

/* 修改 Dialog 右上角关闭按钮 */
.el-dialog__headerbtn .el-dialog__close {
  font-size: 25px;
}

.el-dialog__headerbtn {
  margin-top: -12px;
}

/* 修改 Dialog 右上角关闭按钮悬浮效果 */
.el-dialog__headerbtn:hover .el-dialog__close {
  color: white;
}
</style>