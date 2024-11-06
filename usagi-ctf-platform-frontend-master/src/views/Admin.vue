<script setup>
import { ref } from 'vue'
import { Timer, Delete, MagicStick, Warning, CaretLeft,SuccessFilled,CircleCloseFilled } from '@element-plus/icons-vue'
import { post, put, patch, del } from '../utils/request.js'

const userName = ref(JSON.parse(localStorage.getItem('User')).username) // 从缓存中获取用户名
const adminResponseData = ref({ "data": [] }) // 刚进入admin页面，发送后端请求，拉取到的比赛列表
const responseMessage = ref({ "data": [] }) // 发送后端请求，拉取到的消息列表
const responseMessageType1 = ref([]) // 过滤到的消息列表中的 通知
const responseMessageType2 = ref([]) // 过滤到的消息列表中的 动态
const responseMessageType3 = ref([]) // 过滤到的消息列表中的 题目
const isDark = ref(false)  // 控制按钮是否为深色(不要删掉，报warning)
const panelSelection = ref(2) // 控制面板显示的内容选项
const is_open = ref(null) // 记录当前点击的比赛是否开启
const messageInput = ref('') // 消息管理 新增消息输入框的内容
const newTime = ref('') // 消息管理 新增消息输入框的时间
const problemInfoDialogVisible = ref(false) // 控制题目详情弹窗是否显示
const problemEditDialogVisible = ref(false) // 控制题目编辑弹窗是否显示
const problemAddDialogVisible = ref(false) // 控制题目新增弹窗是否显示
const responseProblem = ref({ "data": [] }) // 发送后端请求，拉取到的题目列表
const responseProblem1 = ref([]) // 过滤到的题目列表中的 Web
const responseProblem2 = ref([]) // 过滤到的题目列表中的 Reverse
const responseProblem3 = ref([]) // 过滤到的题目列表中的 Crypto
const responseProblem4 = ref([]) // 过滤到的题目列表中的 Pwn
const responseProblem5 = ref([]) // 过滤到的题目列表中的 Misc
const options = [
  {
    value: 'Web',
    label: 'Web',
  },
  {
    value: 'Reverse',
    label: 'Reverse',
  },
  {
    value: 'Crypto',
    label: 'Crypto',
  },
  {
    value: 'Pwn',
    label: 'Pwn',
  },
  {
    value: 'Misc',
    label: 'Misc',
  },
] // 题目类型下拉框选项


// 储存比赛基础信息的表单
const basicForm = ref({
  id: -1, // -1时显示主界面，当点击某个比赛时，显示编辑界面，值变为对应比赛id，-2时显示添加比赛界面
  gameName: '',
  timeStart1: '',
  timeStart2: '',
  timeEnd1: '',
  timeEnd2: '',
  gameIntro: '',
})

// 存储题目信息的表单，用于新增题目
const problemForm = ref({
  "problemName": "",
  "problemType": "",
  "problemIntro": "",
  "problemAtta": "",
  "problemEnvi": "",
  "flag": "",
  "oriPts": null
})

// 存储题目详情的表单，用于题目详情弹窗以及题目编辑弹窗
const problemInfoForm = ref({})

// 模拟点击某个比赛后，拉取到的消息列表
// responseMessage.value = {
//   "code": 200,
//   "message": "success",
//   "data": [
//     {
//       "id": 1,
//       "gameId": null,
//       "content": "Hello notice!",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 2,
//       "gameId": null,
//       "content": "Hello notice!saddadhkajhdjwkalhdwlahdjwakhdjahwldkhwjadhjaklshdjashdjkawhdjadlhawhdjaklhd",
//       "type": "type2",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 3,
//       "gameId": null,
//       "content": "Hello notice!saddadhkajhdjwkalhdwlahdjwakhdjahwldHello notice!Hello notice!Hello notice!",
//       "type": "type3",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 4,
//       "gameId": null,
//       "content": "Hello notice!4",
//       "type": "type2",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 5,
//       "gameId": null,
//       "content": "Hello notice!5",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 6,
//       "gameId": null,
//       "content": "Hello notice!666666666666666666666666666666666666666666666666666666666666666666666666",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 7,
//       "gameId": null,
//       "content": "Hello凤凰军事大国粉红色的警告房的撒还记得凯撒还得看，达瓦会尽快等哈就肯定会开始就。\n阿桑的歌时间价快速的官方 notice!7",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "888888888888888888888888888888888888888888888888888Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hel德瓦达干哈我国达娃大嘎看我的噶几位大哥lo notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//     {
//       "id": 8,
//       "gameId": null,
//       "content": "Hello notice!8",
//       "type": "type1",
//       "time": "2023/11/1 20:03:00"
//     },
//   ]
// }


// 进入编辑按钮点击事件
const gameEdit = (id) => {
  console.log("进入比赛编辑,id:" + id)
  basicForm.value.id = id
  // filter过滤出的结果是一个数组，由于我们的id是唯一的，所以数组只有一个元素，取第一个元素即可
  is_open.value = adminResponseData.value.data.filter(item => item.id === id)[0].is_open
  basicForm.value.gameName = adminResponseData.value.data.filter(item => item.id === id)[0].gameName
  basicForm.value.timeStart1 = adminResponseData.value.data.filter(item => item.id === id)[0].timeStart
  basicForm.value.timeStart2 = adminResponseData.value.data.filter(item => item.id === id)[0].timeStart
  basicForm.value.timeEnd1 = adminResponseData.value.data.filter(item => item.id === id)[0].timeEnd
  basicForm.value.timeEnd2 = adminResponseData.value.data.filter(item => item.id === id)[0].timeEnd
  basicForm.value.gameIntro = adminResponseData.value.data.filter(item => item.id === id)[0].gameIntro
  // 获取当前比赛是否开启
  is_open.value = adminResponseData.value.data.filter(item => item.id === id)[0].isOpen

  // 从后端获取消息列表信息
  put('/notice', {
    "notice": {
      "gameId": basicForm.value.id
    },
    "user": {
      "username": userName.value
    }
  },
    res => {
      responseMessage.value = res.data  // 将响应数据存储在响应式变量中
      console.log('获取消息列表成功')
      // console.log(res.data)

      // 拉取到的消息列表后过滤出通知，赋值给responseMessageType1
      responseMessageType1.value = responseMessage.value.data.filter(item => item.type === 'type1')

      // 拉取到的消息列表后过滤出动态，赋值给responseMessageType2
      responseMessageType2.value = responseMessage.value.data.filter(item => item.type === 'type2')

      // 拉取到的消息列表后过滤出题目，赋值给responseMessageType3
      responseMessageType3.value = responseMessage.value.data.filter(item => item.type === 'type3')
    },
    err => {
      console.error(err)
      console.log('获取消息列表失败')
    }
  )

  // 从后端获取题目列表信息
  post('/problems', {
    "user": {
      "username": userName.value
    },
    "game": {
      "id": basicForm.value.id
    }
  },
    res => {
      responseProblem.value = res.data  // 将响应数据存储在响应式变量中
      console.log('获取题目列表成功')
      console.log(res.data)

      // 拉取到的题目列表后过滤出Web，赋值给responseProblem1
      responseProblem1.value = responseProblem.value.data.filter(item => item.problemType === 'Web')

      // 拉取到的题目列表后过滤出Reverse，赋值给responseProblem2
      responseProblem2.value = responseProblem.value.data.filter(item => item.problemType === 'Reverse')

      // 拉取到的题目列表后过滤出Crypto，赋值给responseProblem3
      responseProblem3.value = responseProblem.value.data.filter(item => item.problemType === 'Crypto')

      // 拉取到的题目列表后过滤出Pwn，赋值给responseProblem4
      responseProblem4.value = responseProblem.value.data.filter(item => item.problemType === 'Pwn')

      // 拉取到的题目列表后过滤出Misc，赋值给responseProblem5
      responseProblem5.value = responseProblem.value.data.filter(item => item.problemType === 'Misc')
    },
    err => {
      console.error(err.data)
      console.log('获取题目列表失败')
    }
  )

}

// 进入添加比赛按钮点击事件
const EnterAddGame = () => {
  console.log("进入添加比赛界面")
  basicForm.value.id = -2
}

// 面板返回按钮点击事件
const back = () => {
  // 清空表格
  basicForm.value.gameName = ''
  basicForm.value.timeStart1 = ''
  basicForm.value.timeStart2 = ''
  basicForm.value.timeEnd1 = ''
  basicForm.value.timeEnd2 = ''
  basicForm.value.gameIntro = ''
  // 刷新数据
  post('/games', {
    "username": userName.value
  },
    res => {
      adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
      console.log('更新比赛列表数据成功')
      console.log(res.data)
    },
    err => {
      console.error(err)
      console.log('更新比赛列表数据失败')
    }
  )
  // 置id为-1，显示主界面
  basicForm.value.id = -1
  // 置panelSelection为2，保证下次点开显示基本信息
  panelSelection.value = 2
}

// 面板基本信息按钮点击事件
const basic = () => {
  console.log("基本信息")
  panelSelection.value = 2
}

// 面板基本信息修改按钮点击事件
const trueEditBasic = () => {
  put('/game', {
    "user": {
      "username": userName.value
    },
    "game": {
      "id": basicForm.value.id,
      "gameName": basicForm.value.gameName,
      "timeStart": timeTransform(basicForm.value.timeStart1, basicForm.value.timeStart2),
      "timeEnd": timeTransform(basicForm.value.timeEnd1, basicForm.value.timeEnd2),
      "gameIntro": basicForm.value.gameIntro
    }
  },
    res => {
      ElMessage({
        type: 'success',
        message: '修改成功',
      })
      // 更新比赛列表的数据
      post('/games', {
        "username": userName.value
      },
        res => {
          adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新比赛列表数据成功')
          console.log(res.data)
        },
        err => {
          console.error(err)
          console.log('更新比赛列表数据失败')
        }
      )
    },
    err => {
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '修改失败',
      })
    }
  )
}

// 面板基本信息修改取消按钮点击事件
const backToMain = () => {
  // 取消表格修改，重新把一开始的数据赋值给表格
  basicForm.value.gameName = adminResponseData.value.data.filter(item => item.id === basicForm.value.id)[0].gameName
  basicForm.value.timeStart1 = adminResponseData.value.data.filter(item => item.id === basicForm.value.id)[0].timeStart
  basicForm.value.timeStart2 = adminResponseData.value.data.filter(item => item.id === basicForm.value.id)[0].timeStart
  basicForm.value.timeEnd1 = adminResponseData.value.data.filter(item => item.id === basicForm.value.id)[0].timeEnd
  basicForm.value.timeEnd2 = adminResponseData.value.data.filter(item => item.id === basicForm.value.id)[0].timeEnd
  basicForm.value.gameIntro = adminResponseData.value.data.filter(item => item.id === basicForm.value.id)[0].gameIntro
}

// 面板赛题管理Web按钮点击事件
const problem1 = () => {
  console.log("赛题管理Web")
  panelSelection.value = 3.1
}

// 面板赛题管理Reverse按钮点击事件
const problem2 = () => {
  console.log("赛题管理Reverse")
  panelSelection.value = 3.2
}

// 面板赛题管理Crypto按钮点击事件
const problem3 = () => {
  console.log("赛题管理Crypto")
  panelSelection.value = 3.3
}

// 面板赛题管理Pwn按钮点击事件
const problem4 = () => {
  console.log("赛题管理Pwn")
  panelSelection.value = 3.4
}

// 面板赛题管理Misc按钮点击事件
const problem5 = () => {
  console.log("赛题管理Misc")
  panelSelection.value = 3.5
}

// 面板消息管理按钮点击事件
const messages1 = () => {
  console.log("消息管理1")
  panelSelection.value = 4.1

  // 进入到消息管理界面时，获取到当前时间，并且每秒更新一次
  // 主要是用于新增消息时的提交时间展示
  getNowTime()//进入页面调用该方法获取当前时间
  clearInterval(myTimeDisplay)//销毁之前定时器
  var myTimeDisplay = setInterval(() => {
    getNowTime() //每秒更新一次时间
  }, 1000)
}

const messages2 = () => {
  console.log("消息管理2")
  panelSelection.value = 4.2

  // 进入到消息管理界面时，获取到当前时间，并且每秒更新一次
  // 主要是用于新增消息时的提交时间展示
  getNowTime()//进入页面调用该方法获取当前时间
  clearInterval(myTimeDisplay)//销毁之前定时器
  var myTimeDisplay = setInterval(() => {
    getNowTime() //每秒更新一次时间
  }, 1000)
}

const messages3 = () => {
  console.log("消息管理3")
  panelSelection.value = 4.3

  // 进入到消息管理界面时，获取到当前时间，并且每秒更新一次
  // 主要是用于新增消息时的提交时间展示
  getNowTime()//进入页面调用该方法获取当前时间
  clearInterval(myTimeDisplay)//销毁之前定时器
  var myTimeDisplay = setInterval(() => {
    getNowTime() //每秒更新一次时间
  }, 1000)
}

// 面板消息管理删除消息按钮点击事件
const delMessage = (messageId) => {
  del('/notice', {
    "notice": {
      "id": messageId,
      "gameId": basicForm.value.id
    },
    "user": {
      "username": userName.value
    }
  },
    res => {
      ElMessage({
        type: 'success',
        message: '删除成功',
      })
      // 更新消息列表的数据
      put('/notice', {
        "notice": {
          "gameId": basicForm.value.id
        },
        "user": {
          "username": userName.value
        }
      },
        res => {
          responseMessage.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新消息列表成功')
          console.log(res.data)

          // 拉取到的消息列表后过滤出通知，赋值给responseMessageType1
          responseMessageType1.value = responseMessage.value.data.filter(item => item.type === 'type1')

          // 拉取到的消息列表后过滤出动态，赋值给responseMessageType2
          responseMessageType2.value = responseMessage.value.data.filter(item => item.type === 'type2')

          // 拉取到的消息列表后过滤出题目，赋值给responseMessageType3
          responseMessageType3.value = responseMessage.value.data.filter(item => item.type === 'type3')
        },
        err => {
          console.error(err)
          console.log('更新消息列表失败')
        }
      )
    },
    err => {
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '删除失败',
      })
    }
  )
}

// 面板消息管理新增消息按钮点击事件
const addMessage = () => {
  // 判断点击的是哪个消息，获取type
  let type = ''
  if (panelSelection.value === 4.1) {
    type = 'type1'
  } else if (panelSelection.value === 4.2) {
    type = 'type2'
  } else if (panelSelection.value === 4.3) {
    type = 'type3'
  }
  // 获取当前时间时间戳
  let now = new Date()
  let timestamp = now.getTime()
  console.log(timestamp)
  // 发送请求
  post('/notice', {
    "notice": {
      "gameId": basicForm.value.id,
      "content": messageInput.value,
      "type": type,
      "time": timestamp
    },
    "user": {
      "username": userName.value
    }
  },
    res => {
      ElMessage({
        type: 'success',
        message: '新增消息成功',
      })
      // 清空输入框
      messageInput.value = ''
      // 更新消息列表的数据
      put('/notice', {
        "notice": {
          "gameId": basicForm.value.id
        },
        "user": {
          "username": userName.value
        }
      },
        res => {
          responseMessage.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新消息列表成功')
          console.log(res.data)

          // 拉取到的消息列表后过滤出通知，赋值给responseMessageType1
          responseMessageType1.value = responseMessage.value.data.filter(item => item.type === 'type1')

          // 拉取到的消息列表后过滤出动态，赋值给responseMessageType2
          responseMessageType2.value = responseMessage.value.data.filter(item => item.type === 'type2')

          // 拉取到的消息列表后过滤出题目，赋值给responseMessageType3
          responseMessageType3.value = responseMessage.value.data.filter(item => item.type === 'type3')
        },
        err => {
          console.error(err)
          console.log('更新消息列表失败')
        }
      )
    },
    err => {
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '新增消息失败',
      })
    }
  )
}

// 面板赛题管理新增题目按钮点击事件
const addProblem = () => {
  problemAddDialogVisible.value = true
}

// 面板赛题管理删除题目按钮点击事件
const delProblem = (problemNow) => {
  console.log(problemNow)
  del('/prob', {
    "user": {
      "username": userName.value
    },
    "problem": {
      "gameId": basicForm.value.id,
      "problemId": problemNow.problemId
    }
  },
    res => {
      ElMessage({
        type: 'success',
        message: '删除成功',
      })
      // 更新题目列表的数据
      post('/problems', {
        "user": {
          "username": userName.value
        },
        "game": {
          "id": basicForm.value.id
        }
      },
        res => {
          responseProblem.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新题目列表成功')
          console.log(res.data)

          // 拉取到的题目列表后过滤出Web，赋值给responseProblem1
          responseProblem1.value = responseProblem.value.data.filter(item => item.problemType === 'Web')

          // 拉取到的题目列表后过滤出Reverse，赋值给responseProblem2
          responseProblem2.value = responseProblem.value.data.filter(item => item.problemType === 'Reverse')

          // 拉取到的题目列表后过滤出Crypto，赋值给responseProblem3
          responseProblem3.value = responseProblem.value.data.filter(item => item.problemType === 'Crypto')

          // 拉取到的题目列表后过滤出Pwn，赋值给responseProblem4
          responseProblem4.value = responseProblem.value.data.filter(item => item.problemType === 'Pwn')

          // 拉取到的题目列表后过滤出Misc，赋值给responseProblem5
          responseProblem5.value = responseProblem.value.data.filter(item => item.problemType === 'Misc')
        },
        err => {
          console.error(err.data)
          console.log('更新题目列表失败')
        }
      )
    },
    err => {
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '删除失败',
      })
    }
  )
}

// 新增题目弹窗 确认新增按钮点击事件
const trueAddProblem = () => {
  post('/prob', {
    "user": {
      "username": userName.value
    },
    "problem": {
      "gameId": basicForm.value.id,
      "problemName": problemForm.value.problemName,
      "problemType": problemForm.value.problemType,
      "problemIntro": problemForm.value.problemIntro,
      "problemAtta": problemForm.value.problemAtta,
      "problemEnvi": problemForm.value.problemEnvi,
      "flag": problemForm.value.flag,
      "oriPts": problemForm.value.oriPts
    }
  },
    res => {
      ElMessage({
        type: 'success',
        message: '新增题目成功',
      })
      // 清空表单
      problemForm.value.problemName = ''
      problemForm.value.problemType = ''
      problemForm.value.problemIntro = ''
      problemForm.value.problemAtta = ''
      problemForm.value.problemEnvi = ''
      problemForm.value.flag = ''
      problemForm.value.oriPts = null
      // 更新题目列表的数据
      post('/problems', {
        "user": {
          "username": userName.value
        },
        "game": {
          "id": basicForm.value.id
        }
      },
        res => {
          responseProblem.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新题目列表成功')
          console.log(res.data)

          // 拉取到的题目列表后过滤出Web，赋值给responseProblem1
          responseProblem1.value = responseProblem.value.data.filter(item => item.problemType === 'Web')

          // 拉取到的题目列表后过滤出Reverse，赋值给responseProblem2
          responseProblem2.value = responseProblem.value.data.filter(item => item.problemType === 'Reverse')

          // 拉取到的题目列表后过滤出Crypto，赋值给responseProblem3
          responseProblem3.value = responseProblem.value.data.filter(item => item.problemType === 'Crypto')

          // 拉取到的题目列表后过滤出Pwn，赋值给responseProblem4
          responseProblem4.value = responseProblem.value.data.filter(item => item.problemType === 'Pwn')

          // 拉取到的题目列表后过滤出Misc，赋值给responseProblem5
          responseProblem5.value = responseProblem.value.data.filter(item => item.problemType === 'Misc')
        },
        err => {
          console.error(err.data)
          console.log('更新题目列表失败')
        }
      )
      // 关闭弹窗
      problemAddDialogVisible.value = false
    },
    err => {
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '新增题目失败',
      })
    }
  )
}

// 面板容器管理按钮点击事件
const container = () => {
  console.log("容器管理")
  panelSelection.value = 5
}

// 面板状态管理按钮点击事件
const state = () => {
  console.log("状态管理")
  panelSelection.value = 6
}

// 面板删除比赛按钮点击事件
const removing = () => {
  ElMessageBox.confirm(
    '确定要删除这场比赛吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      del('/game', {
        "user": {
          "username": userName.value
        },
        "game": {
          "id": basicForm.value.id
        }
      },
        res => {
          // 更新比赛列表的数据
          post('/games', {
            "username": userName.value
          },
            res => {
              adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
              console.log('更新比赛列表数据成功')
              console.log(res.data)
            },
            err => {
              console.error(err)
              console.log('更新比赛列表数据失败')
            }
          )
          // 显示删除成功
          ElMessage({
            type: 'success',
            message: '删除成功',
          })
          // 清空表格
          basicForm.value.gameName = ''
          basicForm.value.timeStart1 = ''
          basicForm.value.timeStart2 = ''
          basicForm.value.timeEnd1 = ''
          basicForm.value.timeEnd2 = ''
          basicForm.value.gameIntro = ''
          // 置id为-1，显示主界面
          basicForm.value.id = -1
        },
        err => {
          console.error(err.data)
          ElMessage({
            type: 'error',
            message: '删除失败',
          })
        }
      )
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '删除中止',
      })
    })
}

// 面板开启比赛按钮点击事件
const opening = () => {
  ElMessageBox.confirm(
    '确定要开启比赛吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      patch('/game/1', {
        "user": {
          "username": userName.value
        },
        "game": {
          "id": basicForm.value.id
        }
      },
        res => {
          is_open.value = 1
          ElMessage({
            type: 'success',
            message: '开启成功',
          })
          // 更新比赛列表的数据
          post('/games', {
            "username": userName.value
          },
            res => {
              adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
              console.log('更新比赛列表数据成功')
              console.log(res.data)
            },
            err => {
              console.error(err)
              console.log('更新比赛列表数据失败')
            }
          )
        },
        err => {
          console.error(err.data)
          ElMessage({
            type: 'error',
            message: '开启失败',
          })
        }
      )
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '开启中止',
      })
    })
}

// 面板关闭比赛按钮点击事件
const closing = () => {
  ElMessageBox.confirm(
    '确定要关闭比赛吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      patch('/game/0', {
        "user": {
          "username": userName.value
        },
        "game": {
          "id": basicForm.value.id
        }
      },
        res => {
          is_open.value = 0
          ElMessage({
            type: 'success',
            message: '关闭成功',
          })
          // 更新比赛列表的数据
          post('/games', {
            "username": userName.value
          },
            res => {
              adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
              console.log('更新比赛列表数据成功')
              console.log(res.data)
            },
            err => {
              console.error(err)
              console.log('更新比赛列表数据失败')
            }
          )
        },
        err => {
          console.error(err.data)
          ElMessage({
            type: 'error',
            message: '关闭失败',
          })
        }
      )
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '关闭中止',
      })
    })
}

// 模拟刚点击admin界面时，获取到的全部比赛列表信息
// adminResponseData.value = {
//   "code": 200,
//   "message": "success",
//   "data": [
//     {
//       "id": 10,
//       "gameName": "NEWctf1",
//       "gameIntro": "A new ctf1.",
//       "timeStart": "2023/11/1 20:03:00",
//       "timeEnd": "2023/11/1 20:03:00",
//       "is_open": 0
//     },
//     {
//       "id": 30,
//       "gameName": "航江创建3",
//       "gameIntro": "CTF是一种流行的信息安全竞赛形式，其英文名可直译为夺得Flag，也可意译为夺旗赛。其大致流程是，参赛团队之间通过进行攻防对抗、程序分析等形式，率先从主办方给出的比赛环境中得到一串具有一定格式的字符串或其他内容，并将其提交给主办方，从而夺得分数。",
//       "timeStart": "2023/11/1 20:03:00",
//       "timeEnd": "2023/11/1 23:17:30",
//       "is_open": 1
//     },
//     {
//       "id": 50,
//       "gameName": "563354",
//       "gameIntro": "CTF是得F通过进行攻防对抗、程序分析等形式，率先从主办方给出的比赛环境中得到一串具有一定格式的字符串或其他内容，并将其提交给主办方，从而夺得分数。",
//       "timeStart": "2023/11/1 20:03:00",
//       "timeEnd": "2023/11/1 23:17:30",
//       "is_open": 1
//     }
//   ]
// }

// 从后端获取获取到的全部比赛列表信息
// 发送请求
post('/games', {
  "username": userName.value
},
  res => {
    adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
    console.log('获取比赛列表数据成功')
    console.log(res.data)
  },
  err => {
    console.error(err)
    console.log('获取比赛列表数据失败')
  }
)

// 获取到当前时间，并转换为时间格式
const getNowTime = () => {
  var date = new Date()
  var time = date.getFullYear() + '/' + addZero(date.getMonth() + 1) + '/' + addZero(date.getDate()) + ' ' + addZero(date.getHours()) + ':' + addZero(date.getMinutes()) + ':' + addZero(date.getSeconds())
  newTime.value = time
}
//时间不大于10的时候在前面补0，如果需要直接this.addZero(date.getMinutes()),其它与之相同
//小于10的拼接上0字符串
const addZero = (s) => {
  return s < 10 ? ('0' + s) : s
}

// 赛题管理 题目详情查看按钮点击事件
const problemInfoDialog = (problemNow) => {
  console.log(responseProblem1.value)
  problemInfoForm.value = problemNow
  problemInfoDialogVisible.value = true
}

// 赛题管理 题目编辑按钮点击事件
const problemEditDialog = (problemNow) => {
  problemInfoForm.value = problemNow
  problemEditDialogVisible.value = true
}

// 赛题管理 题目编辑弹窗 确认修改按钮点击事件
const trueEditProblem = ()=>{
  let isOpen
  if(problemInfoForm.value.isOpen===true){
    isOpen = 1
  }else{
    isOpen = 0
  }
  put('/prob',{
      "user": {
          "username": userName.value
      },
      "problem": {
          "gameId": basicForm.value.id,
          "problemId": problemInfoForm.value.problemId,
          "problemName": problemInfoForm.value.problemName,
          "problemType": problemInfoForm.value.problemType,
          "problemIntro": problemInfoForm.value.problemIntro,
          "problemAtta": problemInfoForm.value.problemAtta,
          "problemEnvi": problemInfoForm.value.problemEnvi,
          "flag": problemInfoForm.value.flag,
          "oriPts": problemInfoForm.value.oriPts,
          'isOpen': isOpen
      }
    },
    res=>{
      ElMessage({
        type: 'success',
        message: '修改成功',
      })
      // 更新题目列表的数据
      post('/problems', {
        "user": {
          "username": userName.value
        },
        "game": {
          "id": basicForm.value.id
        }
      },
        res => {
          responseProblem.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新题目列表成功')
          console.log(res.data)

          // 拉取到的题目列表后过滤出Web，赋值给responseProblem1
          responseProblem1.value = responseProblem.value.data.filter(item => item.problemType === 'Web')

          // 拉取到的题目列表后过滤出Reverse，赋值给responseProblem2
          responseProblem2.value = responseProblem.value.data.filter(item => item.problemType === 'Reverse')

          // 拉取到的题目列表后过滤出Crypto，赋值给responseProblem3
          responseProblem3.value = responseProblem.value.data.filter(item => item.problemType === 'Crypto')

          // 拉取到的题目列表后过滤出Pwn，赋值给responseProblem4
          responseProblem4.value = responseProblem.value.data.filter(item => item.problemType === 'Pwn')

          // 拉取到的题目列表后过滤出Misc，赋值给responseProblem5
          responseProblem5.value = responseProblem.value.data.filter(item => item.problemType === 'Misc')
        },
        err => {
          console.error(err.data)
          console.log('更新题目列表失败')
        }
      )
      // 关闭弹窗
      problemEditDialogVisible.value = false
    },
    err=>{
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '修改失败',
      })
    }
  )
}

// 创建比赛 清空表格按钮点击事件
const clearForm = () => {
  basicForm.value.gameName = ''
  basicForm.value.timeStart1 = ''
  basicForm.value.timeStart2 = ''
  basicForm.value.timeEnd1 = ''
  basicForm.value.timeEnd2 = ''
  basicForm.value.gameIntro = ''
}

// 创建比赛 提交按钮点击事件
const addGame = () => {
  console.log("提交表单按钮点击")
  // 时间转换测试
  // console.log(basicForm.value.timeStart1)
  // console.log(basicForm.value.timeStart2)
  // console.log(basicForm.value.timeEnd1)
  // console.log(basicForm.value.timeEnd2)
  // console.log(timeTransform(basicForm.value.timeStart1, basicForm.value.timeStart2))
  // console.log(timeTransform(basicForm.value.timeEnd1, basicForm.value.timeEnd2))

  // 以下是提交表单的代码
  // 编辑请求
  let editData = {
    "user": {
      "username": userName.value
    },
    "game": {
      "gameName": basicForm.value.gameName,
      "gameIntro": basicForm.value.gameIntro,
      "timeStart": timeTransform(basicForm.value.timeStart1, basicForm.value.timeStart2),
      "timeEnd": timeTransform(basicForm.value.timeEnd1, basicForm.value.timeEnd2)
    }
  }
  // 向后端发送请求
  post('/game', editData,
    res => {
      ElMessage({
        type: 'success',
        message: '创建成功',
      })
      // 清空表格
      basicForm.value.gameName = ''
      basicForm.value.timeStart1 = ''
      basicForm.value.timeStart2 = ''
      basicForm.value.timeEnd1 = ''
      basicForm.value.timeEnd2 = ''
      basicForm.value.gameIntro = ''
      // 置id为-1，显示主界面
      basicForm.value.id = -1
      // 重新从从后端获取adminResponseData
      post('/games', {
        "username": userName.value
      },
        res => {
          adminResponseData.value = res.data  // 将响应数据存储在响应式变量中
          console.log('更新比赛列表数据成功')
          console.log(res.data)
        },
        err => {
          console.error(err)
          console.log('更新比赛列表数据失败')
        }
      )
    },
    err => {
      console.error(err.data)
      ElMessage({
        type: 'error',
        message: '创建失败',
      })
    }
  )
}

// 传入两个13位时间戳哦，第一个截取年月日，第二个截取时分秒，拼接成一个新的13位时间戳
// 最后，返回一个13位时间戳
const timeTransform = (time1, time2) => {
  let date1 = new Date(time1)
  let date2 = new Date(time2)

  // TODO: 博客：时间戳转字符串发生的错误
  // let year = date1.getFullYear();
  // let month = date1.getMonth() + 1; // getMonth 返回的月份从0开始，所以需要加1
  // let day = date1.getDate();

  // let yearMonthDay = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`; // 提取年月日
  // let timeRest = date2.toISOString().split('T')[1]; // 提取剩下的部分


  // let combinedDateTime = `${yearMonthDay}T${timeRest}`;

  // let result = new Date(combinedDateTime).getTime()
  var finalDate = new Date()
  finalDate.setFullYear(date1.getFullYear())
  finalDate.setMonth(date1.getMonth())
  finalDate.setDate(date1.getDate())
  finalDate.setHours(date2.getHours())
  finalDate.setMinutes(date2.getMinutes())
  finalDate.setSeconds(date2.getSeconds())

  return finalDate.getTime()
}

// 传入后端返回的时间格式，返回两个13位时间戳(待修改)
// 两个时间戳分别截取的是年月日、时分秒
const timeTransform1 = (timeStr) => {
  let datePart = timeStr.split('T')[0] // 提取年月日
  let timePart = timeStr.split('T')[1].split('+')[0] // 提取时分秒

  // 将年月日加00:00:00.000转为13位时间戳
  let timestamp1 = new Date(`${datePart}T00:00:00.000`).getTime()

  // 获取当前年月日
  let now = new Date()
  let currentYear = now.getFullYear()
  let currentMonth = now.getMonth() + 1 // getMonth 返回的月份从0开始，所以需要加1
  let currentDay = now.getDate()
  let currentYearMonthDay = `${currentYear}-${currentMonth.toString().padStart(2, '0')}-${currentDay.toString().padStart(2, '0')}`

  // 将这个字符串的时分秒加当前年月日转为13位时间戳
  let timestamp2 = new Date(`${currentYearMonthDay}T${timePart}`).getTime()

  return [timestamp1, timestamp2]
}
</script>


<template>
  <!-- 从用户名判断不是管理员 -->
  <div v-if="userName !== 'admin'">
    <el-empty description="take a hike" />
    <h2 style="color: aliceblue;text-align: center;">是管理员吗访问这个界面？</h2>
  </div>

  <!-- 用户名为admin，进入界面 -->
  <!-- 管理员界面很简陋，自己人别嫌弃自己人，mua~ -->
  <div v-else-if="userName === 'admin'" class="mainContainer">
    <!-- 警告：在html中不可以直接获取浏览器缓存中的数据 -->
    <!-- fixed一个黑框，潦草挡住顶部tab栏 -->
    <div class="head">
      <p class="pHead">管理员面板</p>
    </div>

    <!-- ——————basicForm.id == -1，显示一点开admin的主界面—————— -->
    <div class="gameDivContainer" v-if="basicForm.id === -1">
      <div class="gameDiv" v-for="(item, index) in adminResponseData.data" :key="index">
        <div>比赛ID:{{ item.id }}</div>
        <div>比赛名称:{{ item.gameName }}</div>
        <el-button color="#404afc" :dark="isDark" @click="gameEdit(item.id)"> 进 入 编 辑 </el-button>
      </div>
      <!-- 添加比赛按钮 -->
      <div class="gameDiv add" @click="EnterAddGame">
        <svg xmlns="http://www.w3.org/2000/svg" width="150" height="150" viewBox="0 0 256 256">
          <path fill="#ffffff"
            d="M220 128a4 4 0 0 1-4 4h-84v84a4 4 0 0 1-8 0v-84H40a4 4 0 0 1 0-8h84V40a4 4 0 0 1 8 0v84h84a4 4 0 0 1 4 4Z" />
        </svg>
      </div>
    </div>

    <!-- ——————basicForm.id == 点击比赛的id，显示这个比赛的编辑页面—————— -->
    <div v-else-if="basicForm.id !== -2" class="gameEditContainer">
      <div class="editBody">
        <!-- el左右布局组件 -->
        <el-container>
          <!-- 侧边栏 -->
          <el-aside width="180px">
            <el-menu default-active="2" class="left">
              <el-menu-item index="1" @click="back">
                <span>返回上一级</span>
              </el-menu-item>
              <el-menu-item index="2" @click="basic">
                <span>基本信息</span>
              </el-menu-item>
              <el-sub-menu index="3">
                <template #title>
                  <span>赛题管理</span>
                </template>
                <el-menu-item index="3-1" @click="problem1">Web</el-menu-item>
                <el-menu-item index="3-2" @click="problem2">Reverse</el-menu-item>
                <el-menu-item index="3-3" @click="problem3">Crypto</el-menu-item>
                <el-menu-item index="3-4" @click="problem4">Pwn</el-menu-item>
                <el-menu-item index="3-5" @click="problem5">Misc</el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="4">
                <template #title>
                  <span>消息管理</span>
                </template>
                <el-menu-item index="4-1" @click="messages1">通知</el-menu-item>
                <el-menu-item index="4-2" @click="messages2">动态</el-menu-item>
                <el-menu-item index="4-3" @click="messages3">题目</el-menu-item>
              </el-sub-menu>
              <el-menu-item index="5" @click="container">
                <span>容器管理</span>
              </el-menu-item>
              <el-menu-item index="6" @click="state">
                <span>状态管理</span>
              </el-menu-item>
            </el-menu>
          </el-aside>

          <!-- 右边主体 -->
          <el-main>
            <!-- 显示基本信息 -->
            <el-form v-if="panelSelection == 2" :model="basicForm" label-width="120px">
              <el-form-item label="比赛ID">
                <el-input v-model="basicForm.id" disabled />
              </el-form-item>
              <el-form-item label="比赛名称">
                <el-input v-model="basicForm.gameName" />
              </el-form-item>
              <el-form-item label="开始时间">
                <el-col :span="11">
                  <el-date-picker v-model="basicForm.timeStart1" type="date" placeholder="Pick a date"
                    style="width: 100%" />
                </el-col>
                <el-col :span="2" class="text-center">
                  <span>-</span>
                </el-col>
                <el-col :span="11">
                  <el-time-picker v-model="basicForm.timeStart2" placeholder="Pick a time" style="width: 100%" />
                </el-col>
              </el-form-item>
              <el-form-item label="结束时间">
                <el-col :span="11">
                  <el-date-picker v-model="basicForm.timeEnd1" type="date" placeholder="Pick a date"
                    style="width: 100%" />
                </el-col>
                <el-col :span="2" class="text-center">
                  <span>-</span>
                </el-col>
                <el-col :span="11">
                  <el-time-picker v-model="basicForm.timeEnd2" placeholder="Pick a time" style="width: 100%" />
                </el-col>
              </el-form-item>
              <el-form-item label="比赛介绍">
                <el-input v-model="basicForm.gameIntro" :autosize="{ minRows: 3, maxRows: 6 }" type="textarea" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="trueEditBasic">修改</el-button>
                <el-button @click="backToMain">取消</el-button>
              </el-form-item>
            </el-form>

            <!-- 显示赛题管理Web -->
            <div v-else-if="panelSelection == 3.1">
              <el-table :data="responseProblem1" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemId }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题类型" width="100">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemType }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="130">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span>{{ scope.row.problemName }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否为容器题" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemEnvi === ''||scope.row.problemEnvi == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否有附件" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemAtta === ''||scope.row.problemAtta == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="开启状态" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 10px">
                      <span class="messageDataContent" v-if="scope.row.isOpen === 0"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="初始分值" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.oriPts }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="赛题管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput" style="margin-left:50px">
                <el-button style="width:110px" type="primary" plain @click="addProblem">新增题目</el-button>
              </div>
            </div>

            <!-- 显示赛题管理Reverse -->
            <div v-else-if="panelSelection == 3.2">
              <el-table :data="responseProblem2" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemId }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题类型" width="100">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 5px">{{ scope.row.problemType }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="130">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span>{{ scope.row.problemName }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否为容器题" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemEnvi === ''||scope.row.problemEnvi == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否有附件" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemAtta === ''||scope.row.problemAtta == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="开启状态" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 10px">
                      <span class="messageDataContent" v-if="scope.row.isOpen === 0"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="初始分值" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.oriPts }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="赛题管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput" style="margin-left:50px">
                <el-button style="width:110px" type="primary" plain @click="addProblem">新增题目</el-button>
              </div>
            </div>

            <!-- 显示赛题管理Crypto -->
            <div v-else-if="panelSelection == 3.3">
              <el-table :data="responseProblem3" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemId }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题类型" width="100">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemType }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="130">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span>{{ scope.row.problemName }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否为容器题" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemEnvi === ''||scope.row.problemEnvi == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否有附件" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemAtta === ''||scope.row.problemAtta == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="开启状态" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 10px">
                      <span class="messageDataContent" v-if="scope.row.isOpen === 0"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="初始分值" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.oriPts }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="赛题管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput" style="margin-left:50px">
                <el-button style="width:110px" type="primary" plain @click="addProblem">新增题目</el-button>
              </div>
            </div>

            <!-- 显示赛题管理Pwn -->
            <div v-else-if="panelSelection == 3.4">
              <el-table :data="responseProblem4" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemId }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题类型" width="100">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemType }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="130">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span>{{ scope.row.problemName }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否为容器题" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemEnvi === ''||scope.row.problemEnvi == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否有附件" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemAtta === ''||scope.row.problemAtta == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="开启状态" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 10px">
                      <span class="messageDataContent" v-if="scope.row.isOpen === 0"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="初始分值" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.oriPts }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="赛题管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput" style="margin-left:50px">
                <el-button style="width:110px" type="primary" plain @click="addProblem">新增题目</el-button>
              </div>
            </div>

            <!-- 显示赛题管理Misc -->
            <div v-else-if="panelSelection == 3.5">
              <el-table :data="responseProblem5" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemId }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题类型" width="100">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemType }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="130">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span>{{ scope.row.problemName }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否为容器题" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemEnvi === ''||scope.row.problemEnvi == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否有附件" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemAtta === ''||scope.row.problemAtta == null"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="开启状态" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 10px">
                      <span class="messageDataContent" v-if="scope.row.isOpen === 0"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="初始分值" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.oriPts }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="赛题管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput" style="margin-left:50px">
                <el-button style="width:110px" type="primary" plain @click="addProblem">新增题目</el-button>
              </div>
            </div>

            <!-- 显示消息管理：通知管理 -->
            <div v-else-if="panelSelection == 4.1">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="消息ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time.replace('T', ' ').replace(/-/g, '/').split('.')[0]
                      }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time.replace('T', ' ').replace(/-/g, '/').split('.')[0] }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger" @click="delMessage(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput">
                <el-input v-model="messageInput" :autosize="{ minRows: 2 }" type="textarea"
                  placeholder="输入新增提交 消息-通知 的内容..." />
                <el-button type="primary" plain @click="addMessage">提交</el-button>
                <el-tooltip content="不可更改">
                  <el-button style="margin-right: 5px;" disabled>提交时间: {{ newTime }}</el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 显示消息管理：动态管理 -->
            <div v-else-if="panelSelection == 4.2">
              <el-table :data="responseMessageType2" style="width: 100%">
                <el-table-column label="消息ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">动态</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time.replace('T', ' ').replace(/-/g, '/').split('.')[0]
                      }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time.replace('T', ' ').replace(/-/g, '/').split('.')[0] }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger" @click="delMessage(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput">
                <el-input v-model="messageInput" :autosize="{ minRows: 2 }" type="textarea"
                  placeholder="输入新增提交 消息-动态 的内容..." />
                <el-button type="primary" plain @click="addMessage">提交</el-button>
                <el-tooltip content="不可更改">
                  <el-button style="margin-right: 5px;" disabled>提交时间: {{ newTime }}</el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 显示消息管理：题目管理 -->
            <div v-else-if="panelSelection == 4.3">
              <el-table :data="responseMessageType3" style="width: 100%">
                <el-table-column label="消息ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">题目</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time.replace('T', ' ').replace(/-/g, '/').split('.')[0]
                      }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time.replace('T', ' ').replace(/-/g, '/').split('.')[0] }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger" @click="delMessage(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput">
                <el-input v-model="messageInput" :autosize="{ minRows: 2 }" type="textarea"
                  placeholder="输入新增提交 消息-题目 的内容..." />
                <el-button type="primary" plain @click="addMessage">提交</el-button>
                <el-tooltip content="不可更改">
                  <el-button style="margin-right: 5px;" disabled>提交时间: {{ newTime }}</el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 显示容器管理 -->
            <div v-else-if="panelSelection == 5">
              <el-table :data="responseProblem1" style="width: 100%">
                <el-table-column label="容器ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemId }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="题目类型" width="100">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.problemType }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="130">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span>{{ scope.row.problemName }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="是否为容器题" width="110">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 25px">
                      <span class="messageDataContent" v-if="scope.row.problemEnvi === ''||scope.row.problemEnvi == null"><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="开启状态" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;margin-left: 10px">
                      <span class="messageDataContent" v-if="scope.row.isOpen === 0"><el-icon style="color:#FF0000" :size="18"><CircleCloseFilled /></el-icon></span>
                      <span class="messageDataContent" v-else><el-icon style="color:#74E600" :size="18"><SuccessFilled /></el-icon></span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="初始分值" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.oriPts }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="容器详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="容器管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput" style="margin-left:50px">
                <el-button style="width:110px" type="primary" plain @click="addProblem">新增容器</el-button>
              </div>
            </div>

            <!-- 状态管理 -->
            <div v-else-if="panelSelection == 6">
              <div class="openGame">
                <div style="margin-bottom: 10px;">
                  <el-text class="mx-1">当前比赛状态: </el-text>
                  <el-text class="mx-1" type="primary" size="large" v-if="is_open == 1">开启中</el-text>
                  <el-text class="mx-1" type="primary" size="large" v-else>关闭中</el-text>
                </div>
                <el-button type="primary" plain @click="opening" :disabled="is_open == 1">开启比赛</el-button>
                <el-button plain @click="closing" :disabled="!is_open == 1">关闭比赛</el-button>
                <el-text type="info" style="display: block;">
                  <el-icon>
                    <MagicStick />
                  </el-icon>
                  比赛能够进入的条件为 "开启比赛 && 处于比赛时间"
                </el-text>
              </div>
              <el-button :icon="Delete" size="large" type="danger" @click="removing">删除比赛</el-button>
              <el-text type="info" style="display: block;">
                <el-icon>
                  <Warning />
                </el-icon>
                删除后无法恢复，请谨慎操作
              </el-text>
            </div>
          </el-main>

        </el-container>
      </div>

      <!-- 点击题目详情后，弹出可拖动弹窗 -->
      <el-dialog v-model="problemInfoDialogVisible" class="dialog" width="50%" draggable center
        :close-on-click-modal="false" :close-on-press-escape="false">
        <!-- 弹窗头部 -->
        <template #header>
          <div class="header">
            <div style="font-size: larger;font-weight:bolder;margin-top: -13px;">{{ problemInfoForm.problemName }}</div>
          </div>
        </template>

        <!-- 弹窗正文 -->
        <div><span style="font-weight: bold;">题目ID：</span>{{ problemInfoForm.problemId }}</div>
        <div><span style="font-weight: bold;">是否开启：</span>{{ problemInfoForm.isOpen === 1 }}</div>
        <div><span style="font-weight: bold;">题目类型：</span>{{ problemInfoForm.problemType }}</div>
        <div style="display: flex;justify-content: flex-start;">
          <div style="font-weight: bold;white-space: nowrap;">题目简介：</div>
          <div>{{ problemInfoForm.problemIntro }}</div>
        </div>
        <div><span style="font-weight: bold;">容器：</span>{{ problemInfoForm.problemEnvi }}</div>
        <div><span style="font-weight: bold;">附件：</span>{{ problemInfoForm.problemAtta }}</div>
        <div style="display: flex; justify-content: flex-start">
          <div style="margin-right: 15px;"><span style="font-weight: bold;">初始分值：</span>{{ problemInfoForm.oriPts }}</div>
          <div style="margin-right: 15px;"><span style="font-weight: bold;">当前分值：</span>{{
            Math.floor(problemInfoForm.oriPts / 20 + (problemInfoForm.oriPts - problemInfoForm.oriPts / 20) /
              (Math.log2(problemInfoForm.solved.split(',').length + 3) - 1)) }}</div>
          <div><span style="font-weight: bold;">做出队伍数：</span>{{ problemInfoForm.solved===''?0:problemInfoForm.solved.split(',').length }}</div>
        </div>

        <!-- 弹窗底部 -->
        <template #footer>
          <el-input v-model="problemInfoForm.flag" disabled style="margin-top:-20px">
            <template #append>
              <el-button><el-icon>
                  <CaretLeft />
                </el-icon>本题Flag</el-button>
            </template>
          </el-input>
        </template>

      </el-dialog>

      <!-- 点击题目编辑后，弹出可拖动弹窗 -->
      <el-dialog v-model="problemEditDialogVisible" class="dialog" width="50%" draggable center
        :close-on-click-modal="false" :close-on-press-escape="false">
        <!-- 弹窗头部 -->
        <template #header>
          <div class="header">
            <div style="font-size: larger;font-weight:bolder;margin-top: -13px;">题目编辑</div>
          </div>
        </template>

        <!-- 弹窗正文 -->
        <el-form label-position="left" label-width="120px" :model="problemForm">
          <el-form-item label="题目名称(必填)：">
            <el-input v-model="problemInfoForm.problemName" placeholder="建议六字以内" />
          </el-form-item>
          <el-form-item label="题目类型(必填)：">
            <el-select v-model="problemInfoForm.problemType" class="m-2" placeholder="Select">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题目介绍(必填)：">
            <el-input :rows="2" type="textarea" v-model="problemInfoForm.problemIntro" placeholder="字数不限" />
          </el-form-item>
          <el-form-item label="初始分值(必填)：">
            <el-input v-model="problemInfoForm.oriPts" placeholder="纯数字" />
          </el-form-item>
          <el-form-item label="附件地址：">
            <el-input v-model="problemInfoForm.problemAtta" placeholder="例：https://www.bilibili.com/" />
          </el-form-item>
          <el-form-item label="dockerfile位置：">
            <el-input v-model="problemInfoForm.problemEnvi" placeholder="例：/user/problems/tests/test1" />
          </el-form-item>
          <el-form-item label="Flag(必填)：">
            <el-input v-model="problemInfoForm.flag" placeholder="如果要使用动态flag用{}，例：ctf{hello_flag_{}}" />
          </el-form-item>
          <el-form-item label="开启状态：">
            <el-switch v-model="problemInfoForm.isOpen" active-text="Open" inactive-text="Close" />
          </el-form-item>
        </el-form>
        <!-- 弹窗底部 -->
        <template #footer>
          <el-button style="width:40%;height:35px;margin-top:-20px" @click="trueEditProblem">确定修改</el-button>
        </template>

      </el-dialog>

      <!-- 点击新增题目后，弹出可拖动弹窗 -->
      <el-dialog v-model="problemAddDialogVisible" class="dialog" width="50%" draggable center
        :close-on-click-modal="false" :close-on-press-escape="false">
        <!-- 弹窗头部 -->
        <template #header>
          <div class="header">
            <div style="font-size: larger;font-weight:bolder;margin-top: -13px;">新增题目</div>
          </div>
        </template>

        <!-- 弹窗正文 -->
        <el-form label-position="left" label-width="120px" :model="problemForm">
          <el-form-item label="题目名称(必填)：">
            <el-input v-model="problemForm.problemName" placeholder="建议六字以内" />
          </el-form-item>
          <el-form-item label="题目类型(必填)：">
            <el-select v-model="problemForm.problemType" class="m-2" placeholder="Select">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题目介绍(必填)：">
            <el-input :rows="2" type="textarea" v-model="problemForm.problemIntro" placeholder="字数不限" />
          </el-form-item>
          <el-form-item label="初始分值(必填)：">
            <el-input v-model="problemForm.oriPts" placeholder="纯数字" />
          </el-form-item>
          <el-form-item label="附件地址：">
            <el-input v-model="problemForm.problemAtta" placeholder="例：https://www.bilibili.com/" />
          </el-form-item>
          <el-form-item label="dockerfile位置：">
            <el-input v-model="problemForm.problemEnvi" placeholder="例：/user/problems/tests/test1" />
          </el-form-item>
          <el-form-item label="Flag(必填)：">
            <el-input v-model="problemForm.flag" placeholder="如果要使用动态flag用{}，例：ctf{hello_flag_{}}" />
          </el-form-item>
        </el-form>
        <!-- 弹窗底部 -->
        <template #footer>
          <el-button style="width:40%;height:35px;margin-top:-20px" @click="trueAddProblem">确定提交</el-button>
        </template>
      </el-dialog>
    </div>

    <!-- ——————basicForm.id == -2，显示这个创建比赛的页面—————— -->
    <div v-else class="gameEditContainer">
      <div class="editBody">
        <!-- el左右布局组件 -->
        <el-container>
          <!-- 侧边栏 -->
          <el-aside width="180px">
            <el-menu default-active="2" class="left">
              <el-menu-item index="1" @click="back">
                <span>返回上一级</span>
              </el-menu-item>
              <el-menu-item index="2" @click="basic">
                <span>基本信息</span>
              </el-menu-item>
              <el-tooltip content="请在创建比赛后，使用比赛编辑功能进行修改">
                <el-sub-menu index="3" disabled>
                  <template #title>
                    <span>赛题管理</span>
                  </template>
                  <el-menu-item index="3-1" @click="problem1">Web</el-menu-item>
                  <el-menu-item index="3-2" @click="problem2">Reverse</el-menu-item>
                  <el-menu-item index="3-3" @click="problem3">Crypto</el-menu-item>
                  <el-menu-item index="3-4" @click="problem4">Pwn</el-menu-item>
                  <el-menu-item index="3-5" @click="problem5">Misc</el-menu-item>
                </el-sub-menu>
              </el-tooltip>
              <el-tooltip content="请在创建比赛后，使用比赛编辑功能进行修改">
                <el-sub-menu index="4" disabled>
                  <template #title>
                    <span>消息管理</span>
                  </template>
                  <el-menu-item index="4-1" @click="messages1">通知</el-menu-item>
                  <el-menu-item index="4-2" @click="messages2">动态</el-menu-item>
                  <el-menu-item index="4-3" @click="messages3">题目</el-menu-item>
                </el-sub-menu>
              </el-tooltip>
              <el-tooltip content="请在创建比赛后，使用比赛编辑功能进行修改">
                <el-menu-item index="5" @click="container" disabled>
                  <span>容器管理</span>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="请在创建比赛后，使用比赛编辑功能进行修改">
                <el-menu-item index="6" @click="state" disabled>
                  <span>状态管理</span>
                </el-menu-item>
              </el-tooltip>
            </el-menu>
          </el-aside>

          <!-- 右边主体 -->
          <el-main>
            <!-- 显示基本信息 -->
            <el-form v-if="panelSelection == 2" :model="basicForm" label-width="120px">
              <el-form-item label="比赛ID">
                <el-input disabled placeholder="点击创建自动分配" />
              </el-form-item>
              <el-form-item label="比赛名称">
                <el-input v-model="basicForm.gameName" placeholder="建议6字以内" />
              </el-form-item>
              <el-form-item label="开始时间">
                <el-col :span="11">
                  <el-date-picker value-format="x" v-model="basicForm.timeStart1" type="date" placeholder="Pick a date"
                    style="width: 100%" />
                </el-col>
                <el-col :span="2" class="text-center">
                  <span>-</span>
                </el-col>
                <el-col :span="11">
                  <el-time-picker value-format="x" v-model="basicForm.timeStart2" placeholder="Pick a time"
                    style="width: 100%" />
                </el-col>
              </el-form-item>
              <el-form-item label="结束时间">
                <el-col :span="11">
                  <el-date-picker value-format="x" v-model="basicForm.timeEnd1" type="date" placeholder="Pick a date"
                    style="width: 100%" />
                </el-col>
                <el-col :span="2" class="text-center">
                  <span>-</span>
                </el-col>
                <el-col :span="11">
                  <el-time-picker value-format="x" v-model="basicForm.timeEnd2" placeholder="Pick a time"
                    style="width: 100%" />
                </el-col>
              </el-form-item>
              <el-form-item label="比赛介绍">
                <el-input v-model="basicForm.gameIntro" type="textarea" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="addGame">创建比赛</el-button>
                <el-button @click="clearForm">清空表格</el-button>
              </el-form-item>
            </el-form>

            <!-- 显示赛题管理Web -->
            <div v-else-if="panelSelection == 3.1">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题名称" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="赛题详情" width="100">
                  <template #default="scope">
                    <el-button type="primary" plain size="small" @click="problemInfoDialog">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="赛题管理" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="problemEditDialog(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="delProblem(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 显示赛题管理Reverse -->
            <div v-else-if="panelSelection == 3.2">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 显示赛题管理Crypto -->

            <div v-else-if="panelSelection == 3.3">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 显示赛题管理Pwn -->
            <div v-else-if="panelSelection == 3.4">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 显示赛题管理Misc -->
            <div v-else-if="panelSelection == 3.5">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="赛题ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 显示消息管理：通知管理 -->
            <div v-else-if="panelSelection == 4.1">
              <el-table :data="responseMessageType1" style="width: 100%">
                <el-table-column label="消息ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">通知</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput">
                <el-input v-model="messageInput" :autosize="{ minRows: 2 }" type="textarea"
                  placeholder="输入新增提交 消息-通知 的内容..." />
                <el-button type="primary" plain>提交</el-button>
                <el-tooltip content="不可更改">
                  <el-button style="margin-right: 5px;" disabled>提交时间: {{ newTime }}</el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 显示消息管理：动态管理 -->
            <div v-else-if="panelSelection == 4.2">
              <el-table :data="responseMessageType2" style="width: 100%">
                <el-table-column label="消息ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">动态</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput">
                <el-input v-model="messageInput" :autosize="{ minRows: 2 }" type="textarea"
                  placeholder="输入新增提交 消息-动态 的内容..." />
                <el-button type="primary" plain>提交</el-button>
                <el-tooltip content="不可更改">
                  <el-button style="margin-right: 5px;" disabled>提交时间: {{ newTime }}</el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 显示消息管理：题目管理 -->
            <div v-else-if="panelSelection == 4.3">
              <el-table :data="responseMessageType3" style="width: 100%">
                <el-table-column label="消息ID" width="70">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">{{ scope.row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="消息类型" width="80">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span style="margin-left: 10px">题目</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="发布时间" width="180">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <el-icon>
                        <timer />
                      </el-icon>
                      <span style="margin-left: 10px">{{ scope.row.time }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容缩略" width="300">
                  <template #default="scope">
                    <div style="display: flex; align-items: center">
                      <span class="messageDataContent">{{ scope.row.content }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="内容详情" width="100">
                  <template #default="scope">
                    <el-popover effect="light" trigger="hover" placement="top" width="auto">
                      <template #default>
                        <div>时间: {{ scope.row.time }}</div>
                        <div>内容: {{ scope.row.content }}</div>
                      </template>
                      <template #reference>
                        <el-tag style="cursor: pointer;margin-left: 7px;">查看</el-tag>
                      </template>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="消息管理" width="150">
                  <template #default="scope">
                    <el-tooltip content="该功能暂未启用">
                      <el-button size="small" disabled>编辑</el-button>
                    </el-tooltip>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="messageInput">
                <el-input v-model="messageInput" :autosize="{ minRows: 2 }" type="textarea"
                  placeholder="输入新增提交 消息-题目 的内容..." />
                <el-button type="primary" plain>提交</el-button>
                <el-tooltip content="不可更改">
                  <el-button style="margin-right: 5px;" disabled>提交时间: {{ newTime }}</el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 显示容器管理 -->
            <div v-else-if="panelSelection == 5">
              容器管理
            </div>

            <!-- 状态管理 -->
            <div v-else-if="panelSelection == 6">
              <div class="openGame">
                <div style="margin-bottom: 10px;">
                  <el-text class="mx-1">当前比赛状态: </el-text>
                  <el-text class="mx-1" type="primary" size="large" v-if="is_open">开启中</el-text>
                  <el-text class="mx-1" type="primary" size="large" v-else>关闭中</el-text>
                </div>
                <el-button type="primary" plain @click="opening" :disabled="is_open">开启比赛</el-button>
                <el-button plain @click="closing" :disabled="!is_open">关闭比赛</el-button>
                <el-text type="info" style="display: block;">
                  <el-icon>
                    <MagicStick />
                  </el-icon>
                  比赛能够进入的条件为 "开启比赛 && 处于比赛时间"
                </el-text>
              </div>
              <el-button :icon="Delete" size="large" type="danger" @click="removing">删除比赛</el-button>
              <el-text type="info" style="display: block;">
                <el-icon>
                  <Warning />
                </el-icon>
                删除后无法恢复，请谨慎操作
              </el-text>
            </div>
          </el-main>

        </el-container>
      </div>

      <!-- 点击题目详情后，弹出可拖动弹窗 -->
      <el-dialog v-model="dialogVisible" class="dialog" width="50%" draggable center :close-on-click-modal="false"
        :close-on-press-escape="false">
        <!-- 弹窗头部 -->
        <template #header>
          <div class="header">
            <div style="font-size: larger;font-weight:bolder;margin-top: -13px;">{{ problemInfoForm.problemName }}</div>
          </div>
        </template>

        <!-- 弹窗正文 -->
        <div><span style="font-weight: bold;">题目ID：</span>{{ problemInfoForm.problemId }}</div>
        <div><span style="font-weight: bold;">题目类型：</span>{{ problemInfoForm.problemType }}</div>
        <div style="display: flex;justify-content: flex-start;">
          <div style="font-weight: bold;white-space: nowrap;">题目简介：</div>
          <div>{{ problemInfoForm.problemIntro }}</div>
        </div>
        <div style="display: flex; justify-content: flex-start">
          <div style="margin-right: 15px;"><span style="font-weight: bold;">初始分值：</span>{{ problemInfoForm.oriPts }}</div>
          <div style="margin-right: 15px;"><span style="font-weight: bold;">当前分值：</span>{{
            Math.floor(problemInfoForm.oriPts / 20 + (problemInfoForm.oriPts - problemInfoForm.oriPts / 20) /
              (Math.log2(problemInfoForm.solved.split(',').length + 3) - 1)) }}</div>
          <div><span style="font-weight: bold;">做出队伍数：</span>{{ problemInfoForm.solved===''?0:problemInfoForm.solved.split(',').length }}</div>
        </div>

        <!-- 弹窗底部 -->
        <template #footer>
          <el-input v-model="problemInfoForm.flag" disabled>
            <template #append>
              <el-button>Flag</el-button>
            </template>
          </el-input>
        </template>

      </el-dialog>

      <!-- 点击题目编辑后，弹出可拖动弹窗 -->
      <el-dialog v-model="problemEditDialogVisible" class="dialog" width="50%" draggable center
        :close-on-click-modal="false" :close-on-press-escape="false">
        <!-- 弹窗头部 -->
        <template #header>
          <div class="header">
            <div style="font-size: larger;font-weight:bolder;margin-top: -13px;">题目编辑</div>
          </div>
        </template>

        <!-- 弹窗正文 -->
        <div>题目简介：绝大多数情况下，本平台 flag 形式为
          flag{This_is_your_flag}，请提交包含flag{}的完整flag，来进行得分；如果flag是其他形式，题目中会阐明；禁止攻击本平台，或向平台发送大量流量，没有任何题目需要使用扫描器</div>
        <div>题目描述：这里是题目描述</div>
        <div>题目提示：这里是题目提示</div>
        <div>题目附件：这里是题目附件</div>
        <div>题目标签：这里是题目标签</div>
        <div>题目分值：这里是题目分值</div>
        <div>题目类型：这里是题目类型</div>
        <div>题目难度：这里是题目难度</div>
        <div>题目作者：这里是题目作者</div>
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
  </div>

  <!-- 从用户名判断不是管理员 -->
  <div v-else="userName !== 'admin'">
    <el-empty description="take a hike" />
    <h2 style="color: aliceblue;text-align: center;">是管理员吗访问这个界面？</h2>
  </div>
</template>

<style scoped>
/* 消息管理 新增消息大div的样式 */
.messageInput {
  min-width: 350px;
  max-width: 850px;
  margin-top: 10px;

  .el-button {
    float: right;
    margin-top: 5px;
  }
}

/* 开启/关闭比赛按钮div样式 */
.openGame {
  margin-bottom: 30px;
}

/* 每一条消息内容太长后缩略显示 */
.messageDataContent {
  width: 300px;
  display: block;
  text-overflow: ellipsis;
  /*超出内容用省略号*/
  overflow: hidden;
  /*内容超出后隐藏*/
  white-space: nowrap;
  /*文本不进行换行*/
}

/* 比赛编辑界面主容器 */
.gameEditContainer {
  position: relative;
  z-index: 999;
}

/* 侧边栏组件样式 */
.left {
  background-color: white
}


/* 日期中间的小横杠 */
.text-center {
  text-align: center;
}

.gameEditContainer {
  width: 100%;
  margin-top: 40px;
  background-color: white;
  border-radius: 10px;
  padding: 5px;
  /* min-height: 300px; */
}

/* 主容器 */
.mainContainer {
  .head {
    position: fixed;
    background-color: rgba(0, 0, 0, 0.5);
    width: 100%;
    height: 100px;
    top: 0;
    left: 0;
    text-align: center;
    z-index: 100;

    .pHead {
      color: white;
      font-size: 30px;
      font-weight: bold;
      padding-top: 20px;
    }
  }
}

/* 用来装比赛卡片的容器 */
.gameDivContainer {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
}

/* 比赛卡片 */
.gameDiv {
  width: 380px;
  height: 230px;
  background-color: rgb(240, 248, 255, 0.5);
  margin-top: 40px;
  margin-bottom: -20px;
  margin-left: 10px;
  margin-right: 10px;
  text-align: center;
  font-size: large;
  border-radius: 10px;
  color: aliceblue;
}

.gameDiv * {
  margin-top: 20px;
}

.gameDiv .el-button {
  font-weight: bold;
  width: 50%;
  height: 40px;
  font-size: 20px;
  margin-top: 80px;
  border-radius: 10px;
}

.gameDiv:hover {
  background-color: rgb(240, 248, 255, 0.7);
  color: black;
}

.add:hover {
  cursor: pointer;
}

.add svg {
  margin-top: 50px;
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
