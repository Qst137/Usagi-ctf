// 定义登录和注册的工具函数

// 导入Axios库
import axios from "axios";

const baseURL = import.meta.env.VITE_BASE_URL + "/login";
const timeout = 10000;
var headers = {
  "Access-Control-Allow-Origin": "*",
  "Content-Type": "application/json",
};

// 创建 Axios 实例
var request = axios.create({
  baseURL: baseURL,
  timeout: timeout,
  headers: headers,
});

const login = async (requestData) => {
  let res = await request.post("", requestData);
  return res;
};

const register = async (requestData) => {
  let res = await request.put("", requestData);
  return res;
};

// 导出函数
export { login, register };
