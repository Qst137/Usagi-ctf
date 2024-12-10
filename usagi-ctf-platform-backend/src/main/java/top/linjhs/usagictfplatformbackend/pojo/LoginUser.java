package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录用实体类
 *
 * @author LinJHS
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private String captchaVerifyParam;
    private User user;
}
