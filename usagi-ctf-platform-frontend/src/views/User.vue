<script setup>
import router from '@/router';
import { ref, reactive, nextTick, onBeforeMount } from 'vue';
import { post, put, del } from '../utils/request.js'

const userChanged = reactive({ // 用户修改后的数据，默认和原始数据一样
  "userIntro": "",
  "email": "",
  "phoneNumber": "",
  "password": "",
  "newPassword": "",
})

const user = ref({ // 原始用户数据
  "username": "",
  "userIntro": "",
  "name": "",
  "email": "",
  "phoneNumber": "",
  "teamId": -1,
  "teamName": "",
  "teamIntro": "",
  "teamMembers": [],
  "teamGames": [],
  "isCaptain": false,
});

onBeforeMount(() => {
  initUser(); // 初始化获得用户数据

})

const initUser = () => {
  // 获取用户数据
  post('/users', { username: localStorage.getItem('Username') },
    (res) => {
      if (res.data.data !== null) {
        // 原始数据
        user.value.username = res.data.data.username
        user.value.name = res.data.data.name
        user.value.userIntro = res.data.data.userIntro
        user.value.email = res.data.data.email
        user.value.phoneNumber = res.data.data.phoneNumber
        user.value.teamId = res.data.data.teamId
        user.value.teamName = res.data.data.teamName
        user.value.teamIntro = res.data.data.teamIntro
        user.value.teamMembers = res.data.data.teamMembers === '' ? [] : res.data.data.teamMembers.split(',')
        user.value.teamGames = res.data.data.teamGames === '' ? [] : res.data.data.teamGames.split(',').map((value) => { return Number(value) })
        user.value.isCaptain = res.data.data.isCaptain
        localStorage.setItem('User', JSON.stringify(user.value))
        // 编辑过的数据
        userChanged.userIntro = res.data.data.userIntro
        userChanged.email = res.data.data.email
        userChanged.phoneNumber = res.data.data.phoneNumber
        if (user.value.username === 'admin')
          router.push('/admin')
      } else {
        localStorage.removeItem('Username')
        localStorage.removeItem('User')
        localStorage.removeItem('Token')
        router.push('/login')
      }
    },
    (res) => {
      localStorage.removeItem('Username')
      localStorage.removeItem('User')
      localStorage.removeItem('Token')
      router.push('/login')
    }
  )

}

const userEdit = reactive({ // 是否显示 input 框
  "userIntro": false,
  "email": false,
  "phoneNumber": false,
});

var userSend = {
  username: localStorage.getItem('Username'),
  userIntro: '',
  email: '',
  phoneNumber: '',
  password: '',
  newPassword: ''
}

const canShowSendButton = ref(false);

const userIntroRef = ref() // 绑定元素
const emailRef = ref() // 绑定元素
const phoneNumberRef = ref() // 绑定元素

const dialogVisible = ref(false) // 修改密码的对话框
const dialogMessage = ref('')

const dialogShow = (value) => {
  if (value === true) {
    dialogVisible.value = true
  } else {
    dialogVisible.value = false
    userChanged.password = '',
      userChanged.newPassword = ''
  }
  dialogMessage.value = ''
  checkChange();
}

const dialogConfirm = () => {
  var result = checkPassword(userChanged.password)
  if (null !== result) { // 密码不符合要求
    dialogMessage.value = result
    return;
  }
  result = checkPassword(userChanged.newPassword)
  if (null !== result) { // 新密码不符合要求
    dialogMessage.value = result
    return;
  }
  dialogMessage.value = ''
  dialogVisible.value = false
  checkChange();
}

const inputOnShow = (type) => {
  if (type === 'userIntro') {
    userEdit.userIntro = true;
    // 获取焦点
    nextTick(() => {
      userIntroRef.value.focus()
    })

  } else if (type === 'email') {
    userEdit.email = true
    // 获取焦点
    nextTick(() => {
      emailRef.value.focus()
    })
  } else if (type === 'phoneNumber') {
    userEdit.phoneNumber = true
    // 获取焦点
    nextTick(() => {
      phoneNumberRef.value.focus()
    })
  }
}

const inputOnChange = (type) => {
  if (type === 'userIntro') {
    userEdit.userIntro = false
  } else if (type === 'email') {
    userEdit.email = false
  } else if (type === 'phoneNumber') {
    userEdit.phoneNumber = false
  }
  checkChange();
}

const md5 = async (text) => {
  const hashBuffer = await crypto.subtle.digest("SHA-256", (new TextEncoder()).encode(text));
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map((b) => b.toString(16).padStart(2, "0")).join("");
  return hashHex;
}

const checkChange = async () => {
  if (userChanged.userIntro !== user.value.userIntro) {
    userSend.userIntro = userChanged.userIntro;
  } else {
    userSend.userIntro = '';
  }
  if (userChanged.email !== user.value.email) {
    userSend.email = userChanged.email;
  } else {
    userSend.email = '';
  }
  if (userChanged.phoneNumber !== user.value.phoneNumber) {
    userSend.phoneNumber = userChanged.phoneNumber;
  } else {
    userSend.phoneNumber = '';
  }
  if (userChanged.password !== '') {
    userSend.password = await md5(userChanged.password + user.value.username)
    userSend.newPassword = await md5(userChanged.newPassword + user.value.username)
  } else {
    userSend.password = '';
    userSend.newPassword = '';
  }
  if (userSend.userIntro !== '' || userSend.email !== '' || userSend.phoneNumber !== '' || userSend.password !== '') {
    canShowSendButton.value = true;
  } else {
    canShowSendButton.value = false;
  }
  alertMessage.value = "";
}

const alertMessage = ref("")
const alertType = ref("error")

const submitUserInfo = () => {
  var result
  alertType.value = 'error'
  if (userSend.userIntro !== '') {
    result = checkUserIntro(userSend.userIntro);
    if (result !== null) {
      alertMessage.value = result;
      return;
    }
  }
  if (userSend.email !== '') {
    result = checkEmail(userSend.email);
    console.log(result)
    if (result !== null) {
      alertMessage.value = result;
      return;
    }
  }
  if (userSend.phoneNumber !== '') {
    result = checkPhoneNumber(userSend.phoneNumber);
    if (result !== null) {
      alertMessage.value = result;
      return;
    }
  }
  alertMessage.value = "";

  // 上传用户信息
  put('/users', userSend,
    (res) => {
      user.value.email = userChanged.email
      user.value.phoneNumber = userChanged.phoneNumber
      user.value.userIntro = userChanged.userIntro
      localStorage.setItem('User', JSON.stringify(user.value))
      canShowSendButton.value = false;
      userChanged.password = ''
      userChanged.newPassword = ''
      alertType.value = 'success'
      alertMessage.value = "信息更新成功！";
    },
    (res) => {
      alertMessage.value = "信息更新失败，请检查你的信息是否有误";
    }
  )

  console.log("submit");
}

const checkUserIntro = (value) => {
  if (value.length > 100) {
    return "个人介绍长度不能超过100个字符"
  }
  return null;
}

const checkEmail = (value) => {
  if (value.length > 32) {
    return "邮箱长度不能超过32位"
  }
  if (!value.match(/^(\w+([-.][A-Za-z0-9]+)*){3,18}@\w+([-.][A-Za-z0-9]+)*\.\w+([-.][A-Za-z0-9]+)*$/)) {
    return "邮箱格式错误"
  }
  return null;
}

const checkPhoneNumber = (value) => {
  if (value.length !== 11) {
    return "手机号长度需要为11"
  }
  if (!value.match('^[1][3-9][0-9]{9}$')) {
    return "手机格式错误"
  }
  return null;
}

const checkPassword = (value) => {
  if (value.length > 18 || value.length < 8) {
    return '密码长度需要为8到18'
  }
  if (!value.match('[a-zA-Z]+') || !value.match('[0-9]+')) {
    return '密码必须同时包括数字与字母'
  }
  return null;
}

const logout = () => {
  localStorage.removeItem('Token')
  router.push('/home')
}

// ----------- 以下是团队相关 -----------

const teamButtonVisible = ref(true)
const teamInfo = ref({
  teamName: '',
  teamIntro: ''
})
const teamDialogVisible = ref(false)
const createTeamErrorMessage = ref('')

const createTeam = (value) => {
  if (value === 1) { // 第一步，显示输入框
    teamButtonVisible.value = false
    return;
  }
  if (value === 2) { // 验证填写的信息，发送请求，如果成功则关闭输入框
    if (teamInfo.value.teamName.length > 20 || teamInfo.value.teamName.length === 0) {
      createTeamErrorMessage.value = '请输入20个字符以内的团队名称'
      return;
    }
    const regex = /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/;
    if (regex.test(teamInfo.value.teamName) === false) {
      createTeamErrorMessage.value = '团队名只能由中英文、数字或下划线组成'
      return;
    }
    if (teamInfo.value.teamIntro.length > 100 || teamInfo.value.teamIntro.length === 0) {
      createTeamErrorMessage.value = '请输入100个字符以内的团队介绍'
      return;
    }
    createTeamErrorMessage.value = ''
    post('/team', { user: { username: user.value.username }, team: teamInfo.value },
      (res) => {
        alertType.value = 'success'
        alertMessage.value = '创建队伍成功！'
        teamButtonVisible.value = false
        user.value.teamId = 0;
        user.value.teamName = teamInfo.value.teamName
        user.value
        user.value.teamIntro = teamInfo.value.teamIntro
        localStorage.setItem('User', JSON.stringify(user.value))
      },
      (res) => {
        alertType.value = 'error'
        alertMessage.value = '创建队伍失败！'
      }
    );
    // ----- 造数据区域 -----
    // teamInfo.value.teamName = '测试队伍'
    // teamInfo.value.teamIntro = '测试队伍介绍'
    // teamButtonVisible.value = false
    // ----- 造数据结束 -----
    return;
  }
  if (value === 3) { // 不发送请求，关闭弹窗
    teamInfo.value = {
      teamName: '',
      teamIntro: ''
    }
    teamButtonVisible.value = true
    return;
  }
}

const inviteUser = () => {
  ElMessageBox.prompt('请输入队员的用户名', '添加成员', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern:
      /^[a-zA-Z0-9]{3,20}$/,
    inputErrorMessage: 'Invalid Username',
  })
    .then(({ value }) => {
      post('/users', { otherUsername: value },
        (res) => {
          if (res.data.data === null || res.data.data === '') {
            ElMessage.error('用户不存在')
          } else {
            put('/team/01', { user: { username: user.value.username, otherUsername: value }, team: { id: user.value.teamId } },
              (res) => {
                ElMessage.success('添加成员成功！')
                user.value.teamMembers.push()
                localStorage.setItem('User', JSON.stringify(user.value))
              },
              (res) => {
                ElMessage.error('添加成员失败')
              }
            );
          }
        },
        (res) => {
          ElMessage.error('用户不存在')
        }
      )
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: 'Input canceled',
      })
    })
  put('/team/01', { user: { otherUsername: teamUsername.value }, team: { id: teamInfo.id } },
    (res) => {
      alertType.value = 'success'
      alertMessage.value = '添加成员成功！'
      teamButtonVisible.value = false
      user.value.teamId = 0;
      user.value.teamName = teamInfo.value.teamName
      user.value
      user.value.teamIntro = teamInfo.value.teamIntro
      localStorage.setItem('User', JSON.stringify(user.value))
    },
    (res) => {
      alertType.value = 'error'
      alertMessage.value = '创建队伍失败！'
    }
  );
}

const deleteTeamUser = (index) => {
  ElMessageBox.confirm(
    `确认移除队员 "${user.value.teamMembers[index]}"`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      del('/team/01', { user: { username: user.value.username, otherUsername: user.value.teamMembers[index] }, team: { id: user.value.teamId } },
        (res) => {
          ElMessage.success('移除成员成功！')
          user.value.teamMembers.splice(index, 1)
          localStorage.setItem('User', JSON.stringify(user.value))
        },
        (res) => {
          ElMessage.error('移除成员失败')
        }
      );
    })
    .catch(() => { })
}

const deleteTeam = () => {
  ElMessageBox.confirm(
    "确认解散战队？",
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      del('/team', { user: { username: user.value.username }, team: { id: user.value.teamId } },
        (res) => {
          ElMessage.success('解散战队成功！')
          user.value.teamId = -1
          user.value.teamName = ''
          user.value.teamIntro = ''
          user.value.teamMembers = []
          localStorage.setItem('User', JSON.stringify(user.value))
        },
        (res) => {
          ElMessage.error('解散战队失败')
        }
      );
    })
    .catch(() => { })
}


</script>

<template>
  <div class="alertSpace">
    <el-alert class="el-alert" :title="alertMessage" :type="alertType" center show-icon :closable="false"
      v-if="alertMessage !== ''" />
  </div>
  <!-- 用户个人信息界面 -->
  <div class="userInfo">
    <div class="mainCard">
      <div class="avatar">
        <svg xmlns="http://www.w3.org/2000/svg" width="50px" height="50px" viewBox="0 0 256 256">
          <path fill="#5d2aec"
            d="M230.92 212c-15.23-26.33-38.7-45.21-66.09-54.16a72 72 0 1 0-73.66 0c-27.39 8.94-50.86 27.82-66.09 54.16a8 8 0 1 0 13.85 8c18.84-32.56 52.14-52 89.07-52s70.23 19.44 89.07 52a8 8 0 1 0 13.85-8ZM72 96a56 56 0 1 1 56 56a56.06 56.06 0 0 1-56-56Z" />
        </svg>
      </div>
      <div class="text">
        <div class="item username">
          {{ user.username }}
        </div>
        <div class="item pointer label" v-if="userEdit.userIntro">
          个人介绍：
          <el-input class="wideInput" v-model="userChanged.userIntro" placeholder="请输入个人介绍" ref="userIntroRef"
            @blur="inputOnChange('userIntro')" maxlength="100" />
        </div>
        <div class="item pointer" v-else @click="inputOnShow('userIntro')">
          个人介绍：<div class="multiRow">{{ userChanged.userIntro }}</div>
          <div class="edit">
            <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 256 256">
              <path fill="#f0f0f0aa"
                d="m227.31 73.37l-44.68-44.69a16 16 0 0 0-22.63 0L36.69 152A15.86 15.86 0 0 0 32 163.31V208a16 16 0 0 0 16 16h44.69a15.86 15.86 0 0 0 11.31-4.69L227.31 96a16 16 0 0 0 0-22.63ZM92.69 208H48v-44.69l88-88L180.69 120ZM192 108.68L147.31 64l24-24L216 84.68Z" />
            </svg>
          </div>
        </div>
        <div class="item">
          实名信息：{{ user.name }}
        </div>
        <div class="item pointer label" v-if="userEdit.email">
          邮箱：
          <el-input class="wideInput" v-model="userChanged.email" placeholder="请输入邮箱" ref="emailRef"
            @blur="inputOnChange('email')" maxlength="32" />
        </div>
        <div class="item pointer" v-else @click="inputOnShow('email')">
          邮箱：{{ userChanged.email }}
          <div class="edit">
            <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 256 256">
              <path fill="#f0f0f0aa"
                d="m227.31 73.37l-44.68-44.69a16 16 0 0 0-22.63 0L36.69 152A15.86 15.86 0 0 0 32 163.31V208a16 16 0 0 0 16 16h44.69a15.86 15.86 0 0 0 11.31-4.69L227.31 96a16 16 0 0 0 0-22.63ZM92.69 208H48v-44.69l88-88L180.69 120ZM192 108.68L147.31 64l24-24L216 84.68Z" />
            </svg>
          </div>
        </div>
        <div class="item pointer label" v-if="userEdit.phoneNumber">
          手机号：
          <el-input v-model="userChanged.phoneNumber" placeholder="请输入手机号" ref="phoneNumberRef"
            @blur="inputOnChange('phoneNumber')" maxlength="11" />
        </div>
        <div class="item pointer" v-else @click="inputOnShow('phoneNumber')">
          手机号：{{ userChanged.phoneNumber }}
          <div class="edit">
            <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 256 256">
              <path fill="#f0f0f0aa"
                d="m227.31 73.37l-44.68-44.69a16 16 0 0 0-22.63 0L36.69 152A15.86 15.86 0 0 0 32 163.31V208a16 16 0 0 0 16 16h44.69a15.86 15.86 0 0 0 11.31-4.69L227.31 96a16 16 0 0 0 0-22.63ZM92.69 208H48v-44.69l88-88L180.69 120ZM192 108.68L147.31 64l24-24L216 84.68Z" />
            </svg>
          </div>
        </div>
        <div class="item pointer" @click="dialogShow(true)">
          [ 修改密码 ]
          <div class="edit">
            <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 256 256">
              <path fill="#f0f0f0aa"
                d="m227.31 73.37l-44.68-44.69a16 16 0 0 0-22.63 0L36.69 152A15.86 15.86 0 0 0 32 163.31V208a16 16 0 0 0 16 16h44.69a15.86 15.86 0 0 0 11.31-4.69L227.31 96a16 16 0 0 0 0-22.63ZM92.69 208H48v-44.69l88-88L180.69 120ZM192 108.68L147.31 64l24-24L216 84.68Z" />
            </svg>
          </div>
        </div>
        <div class="buttons">
          <div class="sendButton" v-if="canShowSendButton">
            <button @click="submitUserInfo">提交更改</button>
          </div>
          <div class="logoutButton">
            <button @click="logout">登出</button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" title="修改密码" width="40%">
      <el-input v-model="userChanged.password" type="password" placeholder="请输入旧密码" show-password />
      <div style="margin-top: 10px;"></div>
      <el-input v-model="userChanged.newPassword" type="password" placeholder="请输入新密码" show-password />
      <div v-if="dialogMessage !== ''" style="color: var(--color-error);margin-top: 10px;">{{ dialogMessage }}</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogShow(false)">取消</el-button>
          <el-button type="primary" @click="dialogConfirm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    <div class="teamCard">
      <div v-if="user.teamId === -1">
        <div class="title">所在战队：暂无加入的战队</div>
        <button class="button" v-if="teamButtonVisible" @click="createTeam(1)">创建战队</button>
        <div v-else class="createTeamForm">
          <div class="item label">
            战队名称：
            <el-input class="createTeamInput" v-model="teamInfo.teamName" placeholder="请输入战队名" maxlength="20" />
          </div>
          <div class="item label">
            战队介绍：
            <el-input class="createTeamInput" v-model="teamInfo.teamIntro" placeholder="请输入战队介绍" maxlength="100" />
          </div>
          <button class="button" @click="createTeam(2)" style="margin-right: 20px;">提交信息</button>
          <button class="button" @click="createTeam(3)">取消</button>
          <div v-if="createTeamErrorMessage.length > 0" style="color: var(--color-error);">
            {{ createTeamErrorMessage }}
          </div>
        </div>

      </div>
      <div v-else>
        <div class="title">
          所在战队：<span style="color: var(--text-color);">{{ user.teamName }}</span>
        </div>
        <div class="position team-item">
          个人身份：<span style="color: var(--text-color);">{{ user.isCaptain ? "队长" : "队员" }}</span>
        </div>
        <div class="intro team-item">
          战队介绍：<span style="color: var(--text-color);">{{ user.teamIntro }}</span>
        </div>
        <div class="intro team-item">
          战队成员：<span style="color: var(--text-color);">{{ user.teamMembers.join(', ') }}</span>
        </div>
        <div class="inviteUser" v-if="user.isCaptain">
          <el-dialog v-model="teamDialogVisible" title="编辑队员">
            <table>
              <tr>
                <td class="table-title">用户名</td>
                <td class="table-title">身份</td>
                <td class="table-title">操作</td>
              </tr>
              <tr v-for="(item, index) in user.teamMembers">
                <td>{{ item }}</td>
                <td>{{ index === 0 ? '队长' : '队员' }}</td>
                <td>
                  <el-button @click="deleteTeamUser(index)" :disabled="index === 0" type="danger" text>移除成员</el-button>
                </td>
              </tr>
            </table>
            <el-button style="margin-top: 20px;" @click="inviteUser(0)" type="primary">添加成员</el-button>
          </el-dialog>
          <button class="button" @click="() => { teamDialogVisible = true }">编辑成员</button>
          <button class="button error" @click="deleteTeam">解散战队</button>
          <!-- <div v-else class="addTeamUserForm">
            <div class="item label">
              用户名：
              <el-input class="createTeamInput" v-model="teamInfo.teamName" placeholder="请输入用户名" maxlength="20" />
            </div>
            <button class="button" v-if="teamUsername !== teamUsernameChecked" @click="createTeam(2)"
              style="margin-right: 20px;">用户</button>
            <button class="button" @click="createTeam(3)" style="margin-right: 20px;">提交信息</button>
            <button class="button" @click="createTeam(4)">取消</button>
            <div v-if="createTeamErrorMessage.length > 0" style="color: var(--color-error);">
              {{ createTeamErrorMessage }}
            </div>
          </div> -->
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes alert-show {
  0% {
    opacity: 0;
  }

  100% {
    opacity: 1;
  }
}

.alertSpace {
  height: 50px;

  .el-alert {
    width: 600px;
    margin: 0 auto;
    animation: alert-show .25s;
  }
}

.userInfo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.mainCard {
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  width: 600px;
  min-height: 280px;
  border: var(--card-border);
  transition: border-color .25s;
  border-radius: 20px;
  padding: 18px;
  color: var(--text-color-dim);
}

.mainCard:hover {
  border: var(--card-border-hover);
}

.mainCard .avatar {
  margin-right: 20px;
}

.mainCard .avatar svg {
  border-radius: 50%;
  border: 3px solid var(--color-primary-light);
  /* border: var(--card-border-hover); */
}

.mainCard .text {
  display: block;
}

.mainCard .text .item {
  white-space: nowrap;
  color: var(--text-color-dim);
  width: fit-content;
  margin-top: 2px;
  font-size: 1rem;
  border-radius: 8px;
  border: var(--card-border-transparent);
  transition: border-color .25s;
  padding: 2px 10px;
  display: flex;
  flex-direction: row;
  align-items: center;

  .edit {
    padding-top: 5px;
    margin-left: 3px;
    margin-right: -3px;
    transition: opacity .25s;
    opacity: 0;
  }
}

.mainCard .text .item:hover {
  border: var(--card-border-hover);

  .edit {
    opacity: 1;
  }
}

.mainCard .text .item .multiRow {
  width: 300px;
  white-space: normal;
}

.mainCard .text .item .wideInput {
  width: 300px;
}

.mainCard .text .username {
  margin-top: -7px;
  font-size: 1.5rem;
  color: var(--text-color);
  border-radius: 10px;
}


.mainCard .text .pointer:hover {
  cursor: pointer;
}

.mainCard .text .buttons {
  display: flex;
  margin-top: 20px;
}

.mainCard .text .buttons button {
  margin-right: 20px;
  width: 100px;
  height: 40px;
  background-color: transparent;
  color: var(--text-color);
  font-size: var(--font-size-medium);
  cursor: pointer;
  border-radius: 10px;
  transition: border-color .25s, background-color .25s;

  &:hover {
    border: var(--card-border-hover);
    background-color: var(--color-primary-light);
  }

  &:active {
    background-color: var(--color-primary);
  }
}

.mainCard .text .sendButton button {
  border: var(--card-border);

  &:hover {
    border: var(--card-border-hover);
    background-color: var(--color-primary-light);
  }

  &:active {
    background-color: var(--color-primary);
  }
}


.mainCard .text .logoutButton button {
  border: var(--card-border-error);

  &:hover {
    border: var(--card-border-error-hover);
    background-color: var(--color-error-light);
  }

  &:active {
    background-color: var(--color-error);
  }
}


.teamCard {
  box-sizing: border-box;
  width: 600px;
  min-height: 200px;
  border: var(--card-border);
  transition: border-color .25s;
  border-radius: 20px;
  margin-top: 20px;
  padding: 18px 40px;
  color: var(--text-color-dim);

  &:hover {
    border: var(--card-border-hover);
  }

  .title {
    font-size: 1.3rem;
  }

  .team-item {
    margin-top: 10px;
  }

  &:deep(.el-table) {
    background-color: transparent;
  }

  &:deep(.el-table__cell) {
    background-color: transparent;
  }
}

.teamCard .button {
  margin-top: 20px;
  width: 100px;
  height: 40px;
  background-color: transparent;
  color: var(--text-color);
  font-size: var(--font-size-medium);
  cursor: pointer;
  border-radius: 10px;
  transition: border-color .25s, background-color .25s;
  border: var(--card-border);

  &:hover {
    border: var(--card-border-hover);
    background-color: var(--color-primary-light);
  }

  &:active {
    background-color: var(--color-primary);
  }

  &.error {
    margin-left: 20px;
    border: var(--card-border-error);
  }

  &.error:hover {
    background-color: var(--color-error);
  }
}

.teamCard .createTeamForm .item {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-top: 10px;

  .createTeamInput {
    width: 400px;
  }
}

.teamCard .inviteUser table {
  border-collapse: collapse;

  .table-title {
    font-weight: bold;
    color: #0f0f0f;
  }

  & td {
    padding: 8px 16px;
    text-align: left;
    border-bottom: 1px solid #ddd;
  }
}



.label:deep(.el-form-item__label) {
  color: var(--text-color);
  font-weight: 600;
}

.label:deep(.el-input__wrapper) {
  background-color: transparent !important;
  border: var(--card-border);
  transition: border-color .25s;
  box-shadow: none;
}

.label:deep(.el-input__wrapper.is-focus) {
  border: var(--card-border-hover);
}

.label:deep(.el-input__wrapper:hover) {
  border: var(--card-border-hover);
}

.label:deep(.is-error .el-input__wrapper) {
  border: var(--card-border-error);
}

.label:deep(.el-input__inner) {
  font-family: var(--el-font-family);
  color: var(--text-color);
}
</style>
