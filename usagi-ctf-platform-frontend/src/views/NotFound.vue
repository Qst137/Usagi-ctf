<script setup>
import { ref,onBeforeUnmount } from "vue"
import router from "@/router";

const count = ref(5); // 倒计时5秒
const timer = setInterval(() => {
  count.value--;
  if (count.value <= 0) {
    clearInterval(timer);
    router.push("/home");
  }
}, 1000);

onBeforeUnmount(() => {
  clearInterval(timer);
})

</script>

<template>
  <div class="container">
    <div class="title">404</div>
    <div class="content">您所访问的页面不存在</div>
    <div class="content">将在 {{ count }} 秒后回到首页</div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.container .title {
  font-size: 5rem;
  color: var(--text-color);
}

.container .content {
  margin-top: 1rem;
  font-size: 1.5rem;
  color: var(--text-color-dim);
}
</style>
