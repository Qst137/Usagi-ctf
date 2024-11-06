// 定义需要带token请求的工具函数

// 导入 Axios 库
import axios from "axios";
import router from "@/router";

const baseURL = import.meta.env.VITE_BASE_URL;
const timeout = 10000;

const initRequest = () => {
  const headers = {
    "Access-Control-Allow-Origin": "*",
    Token: localStorage.getItem("Token"),
    "Content-Type": "application/json",
  };

  // 创建 Axios 实例
  const request = axios.create({
    baseURL: baseURL,
    timeout: timeout,
    headers: headers,
  });

  return request;
};

// 判断登录是否过期
const generalCheck = (responseData) => {
  // 如果返回 true 则不需要处理
  if (responseData.data.code == 401) {
    // 登录过期或没有登录，清除 Token 并跳转到登录页面
    localStorage.removeItem("Token");
    router.push("/login");
    return true;
  }
  return false;
};

// 定义需要带 Token 请求的工具函数
const get = (url, requestData, success, fail) => {
  initRequest()
    .get(url, { data: requestData })
    .then((res) => {
      if (res.data.code === 200) {
        // 只有响应码 200 的情况才是正确的
        success(res);
      } else {
        if (!generalCheck(res)) fail(res);
      }
    })
    .catch((err) => {
      // 发请求失败，跳转到 404 页面
      router.push("/404");
    });
};

const put = (url, requestData, success, fail) => {
  // 使用 put 传递请求体，直接传递 requestData 即可
  initRequest()
    .put(url, requestData)
    .then((res) => {
      if (res.data.code === 200) {
        success(res);
      } else {
        if (!generalCheck(res)) fail(res);
      }
    })
    .catch((err) => {
      router.push("/404");
    });
};

const post = (url, requestData, success, fail) => {
  // 使用 post 传请求体，只有直接写 requestData 才能传递请求体
  initRequest()
    .post(url, requestData)
    .then((res) => {
      if (res.data.code === 200) {
        success(res);
      } else {
        if (!generalCheck(res)) fail(res);
      }
    })
    .catch((err) => {
      router.push("/404");
      console.log("post请求失败，then都没进去");
    });
};

const del = (url, requestData, success, fail) => {
  // 使用 delete 传请求体，参数格式为 {data: requestData}、{params: requestData}
  initRequest()
    .delete(url, { data: requestData })
    .then((res) => {
      if (res.data.code === 200) {
        success(res);
      } else {
        if (!generalCheck(res)) fail(res);
      }
    })
    .catch((err) => {
      router.push("/404");
    });
};

const patch = (url, requestData, success, fail) => {
  // 使用 patch 传请求体，只有直接写 requestData
  initRequest()
    .patch(url, requestData)
    .then((res) => {
      if (res.data.code === 200) {
        success(res);
      } else {
        if (!generalCheck(res)) fail(res);
      }
    })
    .catch((err) => {
      router.push("/404");
    });
};

// 导出函数
export { get, put, post, del, patch };
