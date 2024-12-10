package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类，封装了比赛和用户
 *
 * @author qst137
 * @version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameAndUser {
    private User user;
    private Game game;
}
