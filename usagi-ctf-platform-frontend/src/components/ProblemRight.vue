<script setup>
import { ref, watch } from 'vue'
import { put } from '../utils/request'
import { time } from 'echarts'

const rank = ref(3) // 记录总排名的变量
const grades = ref(0) // 记录成绩的变量
const number = ref(0) // 记录攻克数量的变量

// 获取父组件传递的参数，并且随父组件进行更新
const props = defineProps(["problemList"]);
const problemList = ref(props.problemList)

// 计算总排名、成绩、攻克数量
number.value = problemList.value.data.filter(item => item.beSolved).length; // 计算攻克数量
grades.value = problemList.value.data.reduce((total, item) => {
	if(item.beSolved) {
		let currentScore = Math.floor(item.oriPts / 20 +
			(item.oriPts - item.oriPts / 20) / (Math.log2(item.solved.split(',').length + 3) - 1));
		return total + currentScore;
	}
	return total;
}, 0); // 计算成绩

// 监听父组件problemList变化更新计算总排名、成绩
watch(props, async (newName, oldName) => {
  problemList.value = newName.problemList
  // 计算总排名、成绩、攻克数量
	number.value = problemList.value.data.filter(item => item.beSolved).length; // 计算攻克数量
	grades.value = problemList.value.data.reduce((total, item) => {
		if(item.beSolved) {
			let currentScore = Math.floor(item.oriPts / 20 +
				(item.oriPts - item.oriPts / 20) / (Math.log2(item.solved.split(',').length + 3) - 1));
			return total + currentScore;
		}
		return total;
	}, 0); // 计算成绩
})

const activeNotice = ref('first')
const responseMessage = ref({
	"data": []
}) // 发送后端请求，拉取到的消息列表
const user = ref(JSON.parse(localStorage.getItem('User')))  // 从浏览器缓存获取用户信息,JSON.parse()将字符串转为对象
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

// 模拟点击某个比赛后，拉取到的通知信息
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

// 点击某个比赛后，从后端拉取通知信息
put('notice', {
	"notice": {
		"gameId": Number(localStorage.getItem('gameId'))
	},
	"user": {
		"username": user.value.username
	}
	},
	res => {
		console.log("拉取比赛消息成功")
		responseMessage.value = res.data  // 将响应数据存储在响应式变量中
		// 转时间格式的函数
		responseMessage.value.data = responseMessage.value.data.map(item => {
			const date = new Date(item.time)
			// 转时间格式并且给月、日、时、分、秒补0
			item.time = `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')} ` + 
			`${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
			return item
		})
	},
	err => {
		console.error(err)
		console.log('拉取比赛消息失败')
	}
)


// // 转时间格式
// responseMessage.value = {
// 	"code": 200,
// 	"message": "success",
// 	"data": [
// 		{
// 			"id": 1,
// 			"gameId": null,
// 			"content": "Hello notice!",
// 			"type": "type1",
// 			"time": "1970-01-01T00:00:00.000+00:00"
// 		},
// 		{
// 			"id": 2,
// 			"gameId": null,
// 			"content": "Hello notice!saddadhkajhdjwkalhdwlahdjwakhdjahwldkhwjadhjaklshdjashdjkawhdjadlhawhdjaklhd",
// 			"type": "type2",
// 			"time": "1970-01-01T00:00:00.000+00:00"
// 		},
// 		{
// 			"id": 3,
// 			"gameId": null,
// 			"content": "Hello notice!saddadhkajhdjwkalhdwlahdjwakhdjahwldHello notice!Hello notice!Hello notice!",
// 			"type": "type3",
// 			"time": "1970-01-01T00:00:00.000+00:00"
// 		}]
// }
// console.log(responseMessage.value)
// // 转时间格式的函数
// responseMessage.value.data = responseMessage.value.data.map(item => {
// 	const date = new Date(item.time)
// 	// 转时间格式并且给月、日、时、分、秒补0
// 	item.time = `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')} ` + 
// 	`${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
// 	return item
// })
// console.log(responseMessage.value)


// 通知栏点击事件
const handleClick = (tab, event) => {
	//console.log(tab, event)
}

</script>

<template>
	<div class="containerR">
		<div class="top">
			<div class="topp">
				<div class="userInfo">
					<div class="userName">
						<div class="avatar">{{ user.username.charAt(0) }}</div>
						<div class="name">{{ user.username }}</div>
					</div>
					<div class="team">战队:{{ user.teamName }}({{ user.isCaptain == 1 ? '队长' : '队员' }})</div>
				</div>
				<div class="userGrade">
					<div class="rank">
						<div>{{ rank }}</div>
						<div>总排名</div>
					</div>
					<div class="grade">
						<div>{{ grades }}</div>
						<div>成绩</div>
					</div>
					<div class="number">
						<div>{{ number }}</div>
						<div>攻克数量</div>
					</div>
				</div>
			</div>
		</div>
		<div class="bottom">
			<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 256 256" class="listSvg">
				<g fill="#ffffff">
					<path d="M136 69.09v101.82l-93.76 28.76A8 8 0 0 1 32 192V48a8 8 0 0 1 10.24-7.67Z" opacity=".2" />
					<path
						d="m220.54 86.66l-176.06-54A16 16 0 0 0 24 48v144a16 16 0 0 0 16 16a16 16 0 0 0 4.52-.65L128 181.73V192a16 16 0 0 0 16 16h32a16 16 0 0 0 16-16v-29.9l28.54-8.75A16.09 16.09 0 0 0 232 138v-36a16.09 16.09 0 0 0-11.46-15.34ZM128 165l-88 27V48l88 27Zm48 27h-32v-15.18l32-9.82Zm40-54h-.11L144 160.08V79.92l71.89 22h.11v36Z" />
				</g>
			</svg>
			<div class="bottomm">
				<el-tabs v-model="activeNotice" class="notice" lazy="true" @tab-click="handleClick">
					<el-tab-pane label="全部消息" name="first">
						<!-- scrDi盒子用来添加响应式高度 -->
						<div class="scrDiv">
							<el-scrollbar>
								<ul>
									<li v-for="item in responseMessage.data">
										<p>{{ item.time }}</p>
										<p>{{ item.content }}</p>
									</li>
								</ul>
							</el-scrollbar>
						</div>
					</el-tab-pane>
					<el-tab-pane label="通知" name="second">
						<div class="scrDiv">
							<el-scrollbar>
								<ul>
									<li v-for="item in responseMessage.data">
										<!-- 在for循环里分别判断type，然后输出对应的通知类型 -->
										<template v-if="item.type === 'type1'">
											<p>{{ item.time }}</p>
											<p>{{ item.content }}</p>
										</template>
									</li>
								</ul>
							</el-scrollbar>
						</div>
					</el-tab-pane>
					<el-tab-pane label="动态" name="third">
						<div class="scrDiv">
							<el-scrollbar>
								<ul>
									<li v-for="item in responseMessage.data">
										<template v-if="item.type === 'type2'">
											<p>{{ item.time }}</p>
											<p>{{ item.content }}</p>
										</template>
									</li>
								</ul>
							</el-scrollbar>
						</div>
					</el-tab-pane>
					<el-tab-pane label="题目" name="fourth">
						<div class="scrDiv">
							<el-scrollbar>
								<ul>
									<li v-for="item in responseMessage.data">
										<template v-if="item.type === 'type3'">
											<p>{{ item.time }}</p>
											<p>{{ item.content }}</p>
										</template>
									</li>
								</ul>
							</el-scrollbar>
						</div>
					</el-tab-pane>
				</el-tabs>
			</div>
		</div>
	</div>
</template>

<style scoped>
.scrDiv {
	/* 响应式高度，同时最小高度与Middle保持对齐 */
	height: calc(100vh - 400px);
	min-height: 510px;
}

/* .notice{
    height: 100%;
}
.el-tab-pane{
    height: 100%;
} */

/* 去除通知横向滚动 */
:deep(.el-scrollbar__bar.is-horizontal) {
	height: 0 !important;
}

/* 去除通知纵向滚动 */
:deep(.el-scrollbar__bar.is-vertical) {
	width: 0 !important;
}

.listSvg {
	transform: scaleX(-1);
	margin-top: 10px;
}

.containerR {
	width: 400px;
	height: 700px;
	/* background-color: #fff; */
	flex-shrink: 0;
}

.top {
	width: 100%;
	height: 140px;
	/* background-color: aquamarine; */
}

.topp {
	box-sizing: border-box;
	width: 90%;
	height: 90%;
	/* background-color: blueviolet; */
	border-radius: 8px;
	/* padding-left: 15px; */
}

/* 个人信息展示 */
.userInfo {
	width: 100%;
	height: 50px;
	/* background-color: black; */
	color: white;
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 20px;
	overflow: hidden;
}

/* 个人信息展示头像加名字 */
.userName {
	/* background-color: aqua; */
	display: flex;
	align-items: center;
	padding-left: 30px;

	.avatar {
		width: 50px;
		height: 50px;
		border-radius: 10px;
		background-color: var(--color-primary-light);
		text-align: center;
		line-height: 50px;
		font-size: 20px;
		font-weight: bold;
	}

	.name {
		font-size: 22px;
		font-weight: bold;
		margin-left: 5px;
	}
}

.team {
	font-size: 18px;
	font-weight: bold;
}

.userGrade {
	/* background-color: black; */
	width: 100%;
	display: flex;
	text-align: center;
	justify-content: space-around;
	margin-top: 20px;
}

.userGrade>div>div:nth-child(1) {
	color: #fff;
	font-size: 18px;
	font-weight: bold;
	margin-bottom: 2px;
}

.userGrade>div>div:nth-child(2) {
	color: var(--text-color-dim);
	font-size: 14px;
}

.bottom {
	width: 100%;
	height: 560px;
	/* background-color: blueviolet; */
	display: flex;
	justify-content: center;
}

.bottomm {
	/* background-color: aqua; */
	width: 85%;
	height: 100%;
}

:deep(.el-scrollbar__bar.is-horizontal) {
	height: 0 !important;
}

.notice ul {
	list-style: none;
	color: #fff;
	width: 100%;
	/* background-color: black; */
	padding: 0;
	margin: 0;
}

/* 标签栏容器样式 */
:deep(.el-tabs__nav-scroll) {
	/* background-color: black; */
	font-size: 30px;
	margin-left: 10px;
}

/* 标签头样式 */
:deep(.el-tabs--top .el-tabs__item.is-top) {
	font-size: 15px;
	font-weight: bold;
	color: #fff;
}

:deep(.el-tabs__item.is-top:hover) {
	color: var(--color-primary-light);
}

:deep(.el-tabs__item.is-top.is-active) {
	color: var(--color-primary-light);
}

/* 标签下方横线样式 */
:deep(.el-tabs__nav-wrap::after) {
	position: static !important;
}

/* 消息显示的时间标签部分 */
ul>li>p:nth-child(1) {
	border-bottom: 1px solid;
	width: 45%;
}
</style>