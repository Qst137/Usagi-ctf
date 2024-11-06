<script setup>
import router from '@/router';
import { reactive } from 'vue'
import { register } from '../utils/login';

const loginFormRef = ref()

// do not use same name with ref
const loginForm = ref({
  username: '',
  password: '',
  passwordAgain: '',
  name: '',
  idCard: '',
  email: '',
  phoneNumber: '',
})

const checkPassword = (rule, value, callback) => {
  if (!value.match('[a-zA-Z]+') || !value.match('[0-9]+')) {
    return callback(new Error('密码必须同时包括数字与字母'))
  }
  callback()
}

const checkPasswordAgain = (rule, value, callback) => {
  if (value !== loginForm.value.password) {
    return callback(new Error('两次输入密码不一致'))
  }
  callback()
}

const checkIdCard = (rule, value, callback) => {
  // 身份证校验
  var sum = 0,
    weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2],
    codes = "10X98765432"
  for (var i = 0; i < value.length - 1; i++) {
    sum += value[i] * weights[i];
  }
  var last = codes[sum % 11];
  if (value[value.length - 1] !== last) {
    return callback(new Error('请输入真实的身份证号'))
  }
  callback()
}

const rules = reactive({
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度需要为3到20', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包括数字、字母和下划线_', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 8, max: 18, message: '密码长度需要为8到18', trigger: 'blur' },
    { validator: checkPassword, trigger: 'blur' }
  ],
  passwordAgain: [
    { required: true, message: '请重复密码', trigger: 'blur' },
    { validator: checkPasswordAgain, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '真实姓名不能为空', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '身份证号不能为空', trigger: 'blur' },
    { min: 18, max: 18, message: '身份证号长度需要为18', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[Xx\d]$/, message: '身份证号格式错误', trigger: 'blur' },
    { validator: checkIdCard, trigger: 'blur' }
  ],
  email: [
    { max: 32, message: '邮箱长度不能超过32位', trigger: 'blur' },
    { pattern: /^(\w+([-.][A-Za-z0-9]+)*){3,18}@\w+([-.][A-Za-z0-9]+)*\.\w+([-.][A-Za-z0-9]+)*$/, message: '邮箱格式错误', trigger: 'blur' }
  ],
  phoneNumber: [
    { min: 11, max: 11, message: '手机号长度需要为11', trigger: 'blur' },
    { pattern: /^[1][3-9][0-9]{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ]
})


const waiting = ref(false)
const captchaSubmitRef = ref()
var hasLoadedCaptcha = false

const onSubmit = async () => {
  await loginFormRef.value.validate((valid, fields) => {
    if (valid) {
      waiting.value = true;
      if (!hasLoadedCaptcha) {
        setTimeout(() => {
          waiting.value = false;
        }, 1500)
        hasLoadedCaptcha = true;
        // 嵌入式
        initAliyunCaptcha({
          SceneId: '160t6lsu', // 场景ID。通过步骤一添加验证场景后，您可以在验证码场景列表，获取该场景的场景ID
          prefix: 'opib55', // 身份标。开通阿里云验证码2.0后，您可以在控制台概览页面的实例基本信息卡片区域，获取身份标
          mode: 'embed', // 验证码模式。embed表示要集成的验证码模式为嵌入式。无需修改
          element: '#captcha-element', // 页面上预留的渲染验证码的元素，与原代码中预留的页面元素保持一致。
          button: '#login-button', // 触发业务请求的元素。button表示单击登录按钮后，触发captchaVerifyCallback函数。您可以根据实际使用的元素修改element的值
          captchaVerifyCallback: captchaVerifyCallback, // 业务请求(带验证码校验)回调函数，无需修改
          onBizResultCallback: onBizResultCallback, // 业务请求结果回调函数，无需修改
          getInstance: getInstance, // 绑定验证码实例函数，无需修改
          slideStyle: {
            width: 320,
            height: 40,
          }, // 滑块验证码样式，支持自定义宽度和高度，单位为px。其中，width最小值为320 px
          language: 'cn', // 验证码语言类型，支持简体中文（cn）、繁体中文（tw）、英文（en）
          immediate: false, // 完成验证后，是否立即发送验证请求（调用captchaVerifyCallback函数）
        });
      } else {
        captchaSubmitRef.value.click();
      }
    }
  })
}

const alertMessage = ref("")
const alertType = ref("error")

let captcha;

// 绑定验证码实例函数。该函数为固定写法，无需修改
function getInstance(instance) {
  captcha = instance;
}

const md5 = async (text) => {
  const hashBuffer = await crypto.subtle.digest("SHA-256", (new TextEncoder()).encode(text));
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map((b) => b.toString(16).padStart(2, "0")).join("");
  return hashHex;
}

// 业务请求(带验证码校验)回调函数
/**
 * @name verifyCaptchaCallback
 * @function
 * 请求参数：由验证码脚本回调的验证参数，不需要做任何处理，直接传给服务端即可
 * @params {string} captchaVerifyParam
 * 返回参数：字段名固定，captchaResult为必选；如无业务验证场景时，bizResult为可选
 * @returns {{captchaResult: boolean, bizResult?: boolean|undefined}} 
 */
async function captchaVerifyCallback(captchaVerifyParam) {
  var loginFormValue = {
    username: loginForm.value.username,
    password: loginForm.value.password,
    name: loginForm.value.name,
    idCard: loginForm.value.idCard,
    email: loginForm.value.email,
    phoneNumber: loginForm.value.phoneNumber,
  };
  // 进行md5运算
  // 进行 loginFormValue.password + loginFormValue.username 的md5运算
  loginFormValue.password = await md5(loginFormValue.password + loginFormValue.username)
  let res = await register({
    captchaVerifyParam: captchaVerifyParam, // 验证码参数
    user: loginFormValue // 业务参数
  })
  var captchaResult = false // 验证结果
  var bizResult = false // 登录结果
  if (res.status !== 200) {
    router.push('/404')
  } else {
    if (res.data.code === 200) {
      alertType.value = "success";
      alertMessage.value = "注册成功";
      bizResult = true
      captchaResult = true
    } else {
      if (res.data.data.search("robot") === -1) // 机器人验证通过
        captchaResult = true
      alertMessage.value = res.data.data;
      waiting.value = false;
    }
  }
  const verifyResult = {
    captchaResult: captchaResult, // 验证码验证是否通过，boolean类型，必选
    bizResult: bizResult, // 业务验证是否通过，boolean类型，可选，若为无业务验证结果的场景，bizResult可以为空
  };
  return verifyResult;
}

// 业务请求结果回调函数
function onBizResultCallback(bizResult) {
  waiting.value = false;
  if (bizResult === true) {
    router.push('/login')
  } else {
    // alert("注册失败，请检查用户名或密码！")
  }
}


</script>

<template>
  <div class="alertSpace">
    <el-alert class="el-alert" :title="alertMessage" :type="alertType" center show-icon :closable="false"
      v-if="alertMessage !== ''" />
  </div>
  <div class="card">
    <p class="title">注册</p>
    <el-form ref="loginFormRef" :model="loginForm" label-width="auto" :rules="rules" class="label">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="loginForm.password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="重复密码" prop="passwordAgain">
        <el-input type="password" v-model="loginForm.passwordAgain" placeholder="请重复密码" show-password />
      </el-form-item>
      <el-form-item label="真实姓名" prop="name">
        <el-input v-model="loginForm.name" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="loginForm.idCard" placeholder="请输入身份证号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="loginForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="手机" prop="phoneNumber">
        <el-input v-model="loginForm.phoneNumber" placeholder="请输入手机号" />
      </el-form-item>
      <div id="captcha-element"></div>
      <div style="height: 20px;"></div>
      <el-form-item>
          <el-button class="login-button" size="large" color="#3d09d2" type="primary" @click="onSubmit"
            :disabled="waiting">{{ waiting ? '提交中……' : '提交' }}</el-button>
      </el-form-item>
      <button type="button" id="login-button" style="display: none;" ref="captchaSubmitRef"></button>
    </el-form>
    <div class="note">已有账号？<router-link to="/login">点此登录</router-link></div>
  </div>
</template>

<style scoped>
.alertSpace {
  height: 50px;

  .el-alert {
    width: 600px;
    margin: 0 auto;
  }
}

.card {
  width: 400px;
  margin: 20px auto;
  padding: 20px;
  border: var(--card-border);
  transition: border-color .25s;
  border-radius: 20px;
}

.card:hover {
  border: var(--card-border-hover);
}

.card .title {
  color: var(--text-color);
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: bold;
}

.card .login-button {
  font-size: medium;
  font-weight: bold;
  color: var(--text-color);
  margin: 0 auto;
  width: 50%;
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
  color: var(--text-color);
}

.note {
  color: var(--text-color);
  text-align: center;
}

.note a {
  color: var(--text-color-primary);
  /* font-weight: bold; */
  text-decoration: none;
  border-bottom: .1rem solid var(--text-color-primary);
}

.note a:hover {
  color: var(--text-color-primary-light);
  border-bottom: .1rem solid var(--text-color-primary-light);
}
</style>