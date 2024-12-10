package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 *
 * @author LinJHS
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String otherUsername;
    private String password;
    private String newPassword;
    private String userIntro;
    private String email;
    private String phoneNumber;
    private String name;
    private String idCard;
    private String teams;
    private Integer isCaptain;
}
