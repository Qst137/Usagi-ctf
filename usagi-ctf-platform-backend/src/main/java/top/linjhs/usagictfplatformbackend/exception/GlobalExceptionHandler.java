package top.linjhs.usagictfplatformbackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.linjhs.usagictfplatformbackend.pojo.Result;

/**
 * 全局异常处理器
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有异常并返回 Result
     *
     * @author LinJHS
     * @param e 异常
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     */
    @ExceptionHandler(Exception.class) // 捕获所有异常
    public Result ex(Exception e) {
        e.printStackTrace();
        return new Result(500,"unexpected_error",null);
    }

}
