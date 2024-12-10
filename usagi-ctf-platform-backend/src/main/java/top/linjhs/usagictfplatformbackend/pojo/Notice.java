package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Notice 实体类
 *
 * @author qst137
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private Integer id;
    private Integer gameId;
    private String content;
    private String type;
    private Timestamp time;
}
