<script setup>


import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import router from "@/router";

const activeIndex = ref('0')

const route = useRoute();

const path = ['home', 'problem', 'rank', 'user']

const handleSelect = (key, keyPath) => {
  // console.log(path[key]);
  router.push({ name: path[key] });
}

onMounted(() => {
  changeIndex(route.path);
})

watch(route, (to, from) => {
  changeIndex(to.path);
})

const changeIndex = (path) => {
  switch (path) {
    case '/home':
      activeIndex.value = '0';
      break;
    case '/problem':
      activeIndex.value = '1';
      break;
    case '/rank':
      activeIndex.value = '2';
      break;
    case '/user':
    case '/admin':
      activeIndex.value = '3';
      break;
    case '/login':
    case '/register':
      break;
    default:
      if (path.match('^/problem/.*'))
        activeIndex.value = '1';
      else if (path.match('^/rank/.*'))
        activeIndex.value = '2';
      else
        activeIndex.value = '0';
      break;
  }
}

</script>

<template>
  <div class="container">
    <el-menu :default-active="activeIndex" class="menu" mode="horizontal" background-color="transparent" text-color="#fff"
      active-text-color="#c6b4f8" @select="handleSelect" :ellipsis="false">
      <el-menu-item class="menu-item" index="0">主页</el-menu-item>
      <el-menu-item class="menu-item" index="1">比赛列表</el-menu-item>
      <div class="logo">Usagi CTF Platform</div>
      <el-menu-item class="menu-item" index="2">排行榜</el-menu-item>
      <el-menu-item class="menu-item" index="3">个人中心</el-menu-item>
    </el-menu>
  </div>
</template>

<style scoped>
.logo {
  text-align: center;
  width: 300px;
  font-weight: bold;
  font-size: 30px;
  color: #fff;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  white-space: nowrap;
  line-height: var(--el-menu-item-height);
  margin: 0;
}

.menu {
  margin: 0 10%;
  width: 80%;
  justify-content: space-around;
  border-bottom: 0;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.2);
}

.menu .menu-item {
  font-size: var(--el-font-size-large);
  font-weight: bold;
  width: 150px;
}

.container:deep(.el-popper) {
  background: transparent!important;
}

.container:deep(.el-menu) {
  background-color: transparent!important;
}
</style>
