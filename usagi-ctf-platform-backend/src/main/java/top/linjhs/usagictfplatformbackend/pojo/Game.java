package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 比赛实体类
 *
 * @author qst137
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game {
    private Integer id;
    private String gameName;
    private String gameIntro;
    private Timestamp timeStart;
    private Timestamp timeEnd;
    private Integer isOpen;
    private Integer isApplied;
}
