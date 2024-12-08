<script setup>
import CommonHeader from '@/components/CommonHeader.vue';
import CommonFooter from '@/components/CommonFooter.vue';
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <common-header/>
      </el-header>
      <el-main class="mainPage">
        <router-view></router-view>
      </el-main>
      <el-footer style="padding: 0;"><common-footer/></el-footer>
    </el-container>
  </div>
</template>

<style scoped>
.mainPage{
  min-height: calc(100vh - 110px - 60px);
}
</style>
