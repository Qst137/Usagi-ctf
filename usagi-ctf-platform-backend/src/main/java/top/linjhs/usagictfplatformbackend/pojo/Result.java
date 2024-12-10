package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result 实体类，用于向用户返回结果
 *
 * @author LinJHS
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;
    private String message;
    private Object data;

    /**
     * 返回成功，无携带数据
     *
     * @author LinJHS
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     */
    public static Result success() {
        return new Result(200,"success",null);
    }

    /**
     * 返回成功，并携带数据
     *
     * @author LinJHS
     * @param data 返回需要携带的数据
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     */
    public static Result success(Object data) {
        return new Result(200,"success",data);
    }

    /**
     * 返回失败，默认信息
     *
     * @author LinJHS
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     */
    public static Result fail() {
        return new Result(404,"fail",null);
    }
    /**
     * 返回失败，并携带错误信息
     *
     * @param data 返回错误信息
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    public static Result fail(Object data) {
        return new Result(404,"fail",data);
    }
}
