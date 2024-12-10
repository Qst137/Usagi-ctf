package top.linjhs.usagictfplatformbackend.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import top.linjhs.usagictfplatformbackend.wrapper.MyHttpServletRequestWrapper;

import java.io.IOException;

/**
 * 拦截所有请求，修改使其可以多次读取 body
 *
 * @author LinJHS
 * @version 1.0
 */
@Component
@WebFilter(urlPatterns = "/ctf_api/**")
public class GlobalFilter implements Filter {

    /**
     * 替换了原有的 RequestWrapper 实现多次读取 body 数据
     *
     * @param servletRequest 请求
     * @param servletResponse 响应
     * @param filterChain filter 链
     * @author LinJHS
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {
            ServletRequest requestWrapper = new MyHttpServletRequestWrapper(
                    (HttpServletRequest) servletRequest);
            filterChain.doFilter(requestWrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
