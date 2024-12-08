<script setup>
</script>

<template>
  <div class="container">
    <div class="card">
      <p class="note">平台介绍</p>
      <p>欢迎来到 Usagi CTF 竞技场！</p>
      <p>本平台由林逸涵、郭航江、贾博三个人共同搭建。</p>
      <p>CTF (Capture The Flag) 中文一般译作夺旗赛，在网络安全领域中指的是网络安全技术人员之间进行技术竞技的一种比赛形式。</p>
    </div>
    <div class="card">
      <p class="note">题目类型</p>
      <p>Web</p>
      <p>Reverse</p>
      <p>Crypto</p>
      <p>Pwn</p>
      <p>Misc</p>
    </div>
    <div class="card">
      <p class="note">平台规则</p>
      <p>绝大多数情况下，本平台 flag 形式为 flag{This_is_your_flag}，请提交包含flag{}的完整flag，来进行得分；如果flag是其他形式，题目中会阐明；</p>
      <p>禁止攻击本平台，或向平台发送大量流量，没有任何题目需要使用扫描器；</p>
    </div>
    <div class="card">
      <p class="note">版权信息</p>
      <p>Copyright © 2023 by Yihan Lin, Hangjiang Guo and Bo Jia. All rights reserved.</p>
      <p>另外，还要感谢以下开源项目：</p>
      <p class="thanks">VueJS
        <a href="https://github.com/vuejs/core/blob/main/LICENSE">license</a>
        <a href="https://github.com/vuejs/core">homepage</a>
      </p>
      <p class="thanks">Element Plus
        <a href="https://github.com/element-plus/element-plus/blob/dev/README.md">license</a>
        <a href="https://github.com/element-plus/element-plus">homepage</a>
      </p>
      <p class="thanks">Phosphor Icons
        <a href="https://github.com/phosphor-icons/core/blob/main/LICENSE">license</a>
        <a href="https://github.com/phosphor-icons/core/tree/main">homepage</a>
      </p>
    </div>

  </div>
</template>

<style scoped>
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

.card~.card {
  margin-top: 20px;
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

.thanks a {
  margin-left: 10px;
  color: var(--text-color-primary);
  /* font-weight: bold; */
  text-decoration: none;
  border-bottom: .1rem solid var(--text-color-primary);
}

.thanks a:hover {
  color: var(--text-color-primary-light);
  border-bottom: .1rem solid var(--text-color-primary-light);
}
</style>
