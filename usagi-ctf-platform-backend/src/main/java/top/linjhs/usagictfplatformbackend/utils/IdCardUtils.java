package top.linjhs.usagictfplatformbackend.utils;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 实名认证
 *
 * @author LinJHS
 * @version 1.0
 */
public class IdCardUtils {
    // 市场链接：https://market.aliyun.com/products/57000002/cmapi026109.html
    private static final String appCode = "dafb02f2479c44188b119481def44154";
    private static final String url = "https://eid.shumaidata.com/eid/check";

    /**
     * 发送校验请求
     *
     * @param params 姓名身份证
     * @return boolean
     * @author LinJHS
     */
    private static boolean postForm(Map<String, String> params) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        for (String key : params.keySet()) {
            formbuilder.add(key, params.get(key));
        }
        FormBody body = formbuilder.build();
        Request request = new Request.Builder().url(url).addHeader("Authorization", "APPCODE " + appCode).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200)
            return false; // 可能是接口欠费了
        assert response.body() != null; // 消警告
        String res = response.body().string();
        JSONObject result = JSONObject.parseObject(res);
        if (!result.get("code").equals("0")) { // 参数错误
            return false;
        }
        // 校验成功
        return ((JSONObject) result.get("result")).get("res").equals("1");
    }

    /**
     * 校验身份证和姓名是否匹配
     *
     * @param idCard 身份证
     * @param name   姓名
     * @return boolean
     * @author LinJHS
     */
    public static boolean checkIdCard(String idCard, String name) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("idcard", idCard);
        params.put("name", name);
        return postForm(params);
    }
}
