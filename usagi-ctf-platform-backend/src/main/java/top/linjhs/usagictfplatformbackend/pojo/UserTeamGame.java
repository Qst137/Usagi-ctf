package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类，封装 user team game
 *
 * @author qst137
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamGame {
    private User user;
    private Team team;
    private Game game;
}
