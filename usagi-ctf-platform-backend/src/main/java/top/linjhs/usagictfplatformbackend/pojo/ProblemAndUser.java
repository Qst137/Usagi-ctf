package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 题目和用户实体类
 *
 * @author LinJHS
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProblemAndUser {
    private Problem problem;
    private User user;
}
