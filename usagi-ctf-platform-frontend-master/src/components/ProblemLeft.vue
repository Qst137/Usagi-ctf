<script setup>
import { ref, watch,defineProps,defineEmits  } from 'vue'

const isAll = ref(true)
const MiscNumber = ref(0)
const CryptoNumber = ref(0)
const PwnNumber = ref(0)
const WebNumber = ref(0)
const ReverseNumber = ref(0)

// 获取父组件传递的参数，并且随父组件进行更新
const props = defineProps(["selectedIndex","problemList"]);
const selectedIndex = ref(props.selectedIndex)
const problemList = ref(props.problemList)
// 将变化的题目类型选择信息传递给父组件
const emit = defineEmits(['update:selectedIndex']);
const updateSelectedIndex = () => {
  emit('update:selectedIndex', selectedIndex.value);
};

console.log(problemList.value.data)
// 遍历problemList，获取各个题目类型的数量
// TODO: 为什么这里的遍历不行，注意数据的使用时机，异步！
for (let i = 0; i < problemList.value.data.length; i++) {
    if (problemList.value.data[i].problemType === 'Misc') {
      MiscNumber.value++
    } else if (problemList.value.data[i].problemType === 'Crypto') {
      CryptoNumber.value++
    } else if (problemList.value.data[i].problemType === 'Pwn') {
      PwnNumber.value++
    } else if (problemList.value.data[i].problemType === 'Web') {
      WebNumber.value++
    } else if (problemList.value.data[i].problemType === 'Reverse') {
      ReverseNumber.value++
    }
}

// 监听父组件传递的参数，当参数改变时，更新本组件的参数
watch(props, async (newName, oldName) => {
  problemList.value = newName.problemList
  MiscNumber.value=0
  CryptoNumber.value=0
  PwnNumber.value=0
  WebNumber.value=0
  ReverseNumber.value=0
  for (let i = 0; i < problemList.value.data.length; i++) {
    if (problemList.value.data[i].problemType === 'Misc') {
      MiscNumber.value++
    } else if (problemList.value.data[i].problemType === 'Crypto') {
      CryptoNumber.value++
    } else if (problemList.value.data[i].problemType === 'Pwn') {
      PwnNumber.value++
    } else if (problemList.value.data[i].problemType === 'Web') {
      WebNumber.value++
    } else if (problemList.value.data[i].problemType === 'Reverse') {
      ReverseNumber.value++
    }
}
})

const selectList = (e) => {
  // this.selectedIndex++;
  if (e.target.tagName === 'LI') {
    // 如果点击的是 <li> 元素，则更新 selectedIndex
    const index = Array.from(e.target.parentElement.children).indexOf(e.target)
    selectedIndex.value = index
    isAll.value = false
    // selectedIndex的值发生变化时，更新父组件的selectedIndex
    updateSelectedIndex();
  }
}

watch(isAll, async (newName, oldName) => {
  if (newName == true) {
    selectedIndex.value = -1
    updateSelectedIndex();
  } else if (selectedIndex.value == -1 && newName == false) {
    selectedIndex.value = 0
    updateSelectedIndex();
  }
})

</script>

<template>
  <div class="containerL">
    <div class="title">
      <svg xmlns="http://www.w3.org/2000/svg" width="24px" height="24px" viewBox="0 0 256 256">
        <path fill="#f0f0f0"
          d="M80 64a8 8 0 0 1 8-8h128a8 8 0 0 1 0 16H88a8 8 0 0 1-8-8Zm136 56H88a8 8 0 0 0 0 16h128a8 8 0 0 0 0-16Zm0 64H88a8 8 0 0 0 0 16h128a8 8 0 0 0 0-16ZM44 52a12 12 0 1 0 12 12a12 12 0 0 0-12-12Zm0 64a12 12 0 1 0 12 12a12 12 0 0 0-12-12Zm0 64a12 12 0 1 0 12 12a12 12 0 0 0-12-12Z" />
      </svg>
      <span class="listText">题目列表</span>
    </div>
    <ul @click="selectList" class="selectList">
      <li :class="{ selected: selectedIndex == 0 }"><span>Misc</span><span>{{ MiscNumber }}</span></li>
      <li :class="{ selected: selectedIndex == 1 }"><span>Crypto</span><span>{{ CryptoNumber }}</span></li>
      <li :class="{ selected: selectedIndex == 2 }"><span>Pwn</span><span>{{ PwnNumber }}</span></li>
      <li :class="{ selected: selectedIndex == 3 }"><span>Web</span><span>{{ WebNumber }}</span></li>
      <li :class="{ selected: selectedIndex == 4 }"><span>Reverse</span><span>{{ ReverseNumber }}</span></li>
    </ul>
    <div class="rabbit1">
      <div>
        <el-switch v-model="isAll"></el-switch>
        <span>显示全部题目</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.containerL {
  width: 300px;
  height: 600px;
  flex-shrink: 0;
  color: var(--text-color);
  /* background-color: blueviolet; */
}

.title {
  width: 250px;
  height: 30px;
  line-height: 30px;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  margin-left: 10px;
}

.title svg {
  margin-right: 4px;
}

.title .listText {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-color);
}

.containerL ul {
  /*清除默认样式*/
  list-style: none;
  margin: 0;
  padding: 0;
}

.containerL ul li {
  margin-top: 5px;
  width: 200px;
  height: 50px;
  color: var(--text-color);
  border-radius: 12px;
  border: var(--card-border-transparent);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--font-size-large);
  font-weight: bold;
  padding: 0 10px;
  box-sizing: border-box;
  transition: border-color .25s, background-color .25s;
}

.containerL ul li:hover {
  /* 鼠标停留变色 */
  border: var(--card-border);
}

.selectList {
  cursor: pointer;
}

.selected {
  /* li选中类 */
  background-color: var(--color-primary-light);
}

.rabbit1 {
  background-image: url('../assets/usagiRabbit/趴着.png');
  background-size: 250px 250px;
  background-position: center;
  background-repeat: no-repeat;
  border: 1px solid transparent;
  width: 300px;
  height: 300px;
}

.rabbit1>div {
  height: 50px;
  width: 200px;
  display: flex;
  align-items: center;
  margin-top: 50px;
  margin-left: 10px;
}

.rabbit1>div>span {
  color: aliceblue;
  font-weight: 600;
  font-size: 17px;
  margin-left: 5px;
}
</style>
  