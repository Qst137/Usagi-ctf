<script setup>
</script>

<template>
  <div>
    <router-view></router-view>
    <div class="main" />
  </div>
</template>

<style scoped>
@keyframes move {
  from {
    transform: rotate(180deg);
  }

  to {
    transform: rotate(540deg);
  }
}

.main {
  position: fixed;
  top: -400%;
  bottom: -400%;
  left: -400%;
  right: -400%;
  background: conic-gradient(from 180deg at 50% 50%,
      #01082C -97.5deg,
      #100133 14.05deg,
      #040989 54.01deg,
      #430383 113.42deg,
      #030668 185.62deg,
      #100133 262.5deg,
      #01082C 374.05deg);
  filter: blur(60px) brightness(1.0);
  z-index: -1;
  animation: 60s move linear infinite;
}
</style>
