package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于检查用户比赛权限实体类
 *
 * @author LinJHS
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePermission {
    private String username;
    private int gameId;
}
