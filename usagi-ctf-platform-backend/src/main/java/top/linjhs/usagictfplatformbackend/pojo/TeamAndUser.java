package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类，封装了队伍和用户
 *
 * @author qst137
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamAndUser {
    Team team;
    User user;
}
