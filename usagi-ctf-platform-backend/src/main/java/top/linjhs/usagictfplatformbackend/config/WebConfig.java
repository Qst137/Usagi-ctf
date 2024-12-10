package top.linjhs.usagictfplatformbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.linjhs.usagictfplatformbackend.interceptor.LoginCheckInterceptor;

/**
 * 配置登录校验拦截器
 *
 * @author LinJHS
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    /**
     * 添加拦截器
     *
     * @author LinJHS
     * @param registry 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor).addPathPatterns("/ctf_api/**").excludePathPatterns("/ctf_api/login");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
