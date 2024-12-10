package top.linjhs.usagictfplatformbackend.utils;

import com.aliyun.tea.*;
import com.aliyun.captcha20230305.models.*;
import com.aliyun.teaopenapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 验证码校验类
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Component
public class CaptchaUtils {
    private static com.aliyun.captcha20230305.Client client = null;

    /**
     * 初始化 client
     *
     * @author LinJHS
     */
    private static void init() throws Exception {
        // ====================== 1. 初始化配置 ======================
        Config config = new Config();
        // 设置您的AccessKey ID 和 AccessKey Secret。
        // getEnvProperty只是个示例方法，需要您自己实现AccessKey ID 和 AccessKey Secret安全的获取方式。
        config.accessKeyId = CaptchaUtils.getEnvProperty("ACCESS_KEY_ID");
        config.accessKeySecret = CaptchaUtils.getEnvProperty("ACCESS_KEY_SECRET");
        //设置请求地址
        config.endpoint = "captcha.cn-shanghai.aliyuncs.com";
        // 设置连接超时为5000毫秒
        config.connectTimeout = 5000;
        // 设置读超时为5000毫秒
        config.readTimeout = 5000;
        client = new com.aliyun.captcha20230305.Client(config);
    }

    /**
     * 获取属性
     *
     * @param propertyName 需要获取的属性值
     * @return java.lang.String
     * @author LinJHS
     */
    private static String getEnvProperty(String propertyName) {
        return "null";
    }

    /**
     * 人机验证
     *
     * @param captchaVerifyParam 验证码验证参数
     * @return boolean
     * @author LinJHS
     */
    public static boolean checkCaptcha(String captchaVerifyParam) throws Exception {
        if(client == null)
            CaptchaUtils.init();
        // ====================== 2. 初始化客户端（实际生产代码中建议复用client） ======================
        // 创建APi请求
        VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest();
        // 前端传来的验证参数 CaptchaVerifyParam
        request.captchaVerifyParam = captchaVerifyParam;
        // ====================== 3. 发起请求） ======================
        try {
            VerifyIntelligentCaptchaResponse resp = client.verifyIntelligentCaptcha(request);
            // 建议使用您系统中的日志组件，打印返回
            // 获取验证码验证结果（请注意判空），将结果返回给前端。出现异常建议认为验证通过，优先保证业务可用，然后尽快排查异常原因。
            if(resp.statusCode!=200) { // 出现错误，可能是服务欠费了
                log.info("Captcha occurs an ERROR! [statusCode]");
                return false;
            }
            if(!resp.body.success) { // 请求失败
                log.info("Captcha occurs an ERROR! [not success]");
                return false;
            }
            return resp.body.result.verifyResult;
        } catch (TeaException error) {
            // 出现异常建议认为验证通过，优先保证业务可用，然后尽快排查异常原因。
            log.info("Captcha occurs an ERROR! [TeaException]");
            return false;
        } catch (Exception _error) {
//            TeaException error = new TeaException(_error.getMessage(), _error);
            // 出现异常建议认为验证通过，优先保证业务可用，然后尽快排查异常原因。
            log.info("Captcha occurs an ERROR! [Exception]");
            return false;
        }
    }
}
