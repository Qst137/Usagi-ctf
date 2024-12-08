<script setup>
import { ref } from 'vue'
import router from '../router';
import { post } from '../utils/request';

const gameList = ref([])
post('/games', { username: localStorage.getItem('Username') },
  (res) => {
    gameList.value = res.data.data
  },
  (res) => {
    localStorage.removeItem('Token')
    localStorage.removeItem('Username')
    localStorage.removeItem('User')
  },
)

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

// 转到指定页面
const goToGame = (game) => {
  router.push(`/rank/${game.id}`)
}
</script>

<template>
  <div class="container">
    <div class="game-list">
      <div class="card" v-for="(item, index) in gameList" :key="index" @click="goToGame(item)">
        <div class="note">{{ item.gameName }}</div>
        <p>{{ item.gameIntro }}</p>
        <p style="opacity: .8;">
          比赛时间：{{ timeFormat(item.timeStart) }} 至 {{ timeFormat(item.timeEnd) }}
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.game-list {
  color: var(--text-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  /* background-color: red; */
}

.card {
  width: 800px;
  padding: 20px;
  box-sizing: border-box;
  border-radius: 30px 0 30px 0;
  border: var(--card-border);
  transition: border-color .25s;
  cursor: pointer;
}

.card:hover {
  border: var(--card-border-hover);
}

.card~.card {
  margin-top: 20px;
}

.card .note {
  width: 250px;
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
