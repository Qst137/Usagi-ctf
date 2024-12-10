package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 队伍实体类
 *
 * @author qst137
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private Integer id;
    private String teamName;
    private String teamIntro;
    private String members;
    private String games;
}
