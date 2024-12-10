package top.linjhs.usagictfplatformbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 题目实体类
 *
 * @author LinJHS
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    private Integer gameId;
    private String tableName;
    private Integer problemId;
    private String problemName;
    private String problemType;
    private String problemIntro;
    private String problemEnvi;
    private String problemAtta;
    private Integer oriPts;
    private String solved;
    private String flag;
    private Integer isOpen;
    private boolean beSolved;
}
