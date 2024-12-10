package top.linjhs.usagictfplatformbackend.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 包装原生的 HttpServletRequest 对象，并添加多次获取 body 数据的方法
 *
 * @author LinJHS
 * @version 1.0
 */
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * 默认构造器调用保存 body 数据
     *
     * @param request 请求
     * @author LinJHS
     */
    public MyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String bodyString = getBodyString(request);
        body = bodyString.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取 body 的数据并存下来
     *
     * @param request 原始数据
     * @return java.lang.String
     * @author LinJHS
     */
    private String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder str = new StringBuilder();
        InputStream inputStream = request.getInputStream();
        try (inputStream; BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            char[] bodyCharBuffer = new char[1024];
            while (true) {
                int len = reader.read(bodyCharBuffer);
                if (len == -1)
                    break;
                str.append(new String(bodyCharBuffer, 0, len));
            }
        } catch (IOException ignoredE) {
        }
        return str.toString();
    }

    /**
     * 获取 body 字符串
     *
     * @return java.lang.String
     * @author LinJHS
     */
    public String getBody() {
        return new String(body);
    }

    /**
     * 重写 getReader() 接口，替换为获取预先保存的数据
     *
     * @return java.io.BufferedReader
     * @author LinJHS
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 重写 getInputStream() 接口，替换为预先保存的 body
     *
     * @return jakarta.servlet.ServletInputStream
     * @author LinJHS
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
