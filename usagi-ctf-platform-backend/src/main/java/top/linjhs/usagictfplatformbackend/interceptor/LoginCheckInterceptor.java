package top.linjhs.usagictfplatformbackend.interceptor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.utils.JwtUtils;
import top.linjhs.usagictfplatformbackend.wrapper.MyHttpServletRequestWrapper;

/**
 * 登录校验拦截器
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 登录校验预处理拦截器
     *
     * @param request  请求
     * @param response 响应
     * @param handler  用不到
     * @return boolean
     * @author LinJHS
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = request.getHeader("Token");

        if (!StringUtils.hasLength(jwt)) { // 没有携带 JWT 令牌
            String error = JSONObject.toJSONString(new Result(401, "not_login", null));
            response.getWriter().write(error);
            return false;
        }

        // 检查令牌是否有效
        Claims claims;
        try { // 解析令牌
            claims = JwtUtils.parseJwt(jwt);
        } catch (Exception e) { // 令牌解析失败
            String error = JSONObject.toJSONString(new Result(401, "not_login", null));
            response.getWriter().write(error);
            return false;
        }

        // 检查 body 中的数据是否和令牌相符
        try {
            String jsonParam = ((MyHttpServletRequestWrapper) request).getBody();
            JSONObject paramData = (JSONObject) JSON.parse(jsonParam);
            if ((paramData.containsKey("username") && !paramData.get("username").equals(claims.get("username"))) ||
                    (paramData.containsKey("user") && ((JSONObject) paramData.get("user")).containsKey("username") &&
                            !((JSONObject) paramData.get("user")).get("username").equals(claims.get("username"))) ||
                    (paramData.containsKey("id") && paramData.get("id") != claims.get("id"))) {
                // 令牌中的 username 或 id 与 body 中的不一样
                String error = JSONObject.toJSONString(new Result(400, "param_error", null));
                response.getWriter().write(error);
                return false;
            }
        } catch (Exception ignoredE) {
            // 数据不是 json 格式
            String error = JSONObject.toJSONString(new Result(400, "not_json", null));
            response.getWriter().write(error);
            return false;
        }
        return true;
    }
}
