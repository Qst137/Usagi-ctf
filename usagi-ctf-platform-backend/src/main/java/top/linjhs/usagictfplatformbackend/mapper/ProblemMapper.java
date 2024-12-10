package top.linjhs.usagictfplatformbackend.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.linjhs.usagictfplatformbackend.pojo.Problem;

import java.util.List;

/**
 * 题目 Mapper
 *
 * @author LinJHS
 * @version 1.0
 */
@Mapper
public interface ProblemMapper {

    /**
     * 查询指定比赛的指定题目
     *
     * @param problem 存储题目 ID
     * @return top.linjhs.usagictfplatformbackend.pojo.Problem
     * @author LinJHS
     */
    @Select("SELECT * FROM ${tableName} WHERE id = #{problemId}")
    Problem getProblemDetail(Problem problem);

    /**
     * 查询指定比赛的所有题目
     *
     * @param tableName 存储比赛表名
     * @return top.linjhs.usagictfplatformbackend.pojo.Problem
     * @author LinJHS
     */
    List<Problem> getAllProblems(String tableName);

    /**
     * 查询题目
     *
     * @param problem 传入比赛 ID、题目 ID
     * @return top.linjhs.usagictfplatformbackend.pojo.Problem
     * @author qst137
     */
    @Select("SELECT * FROM usagi_games_${gameId} WHERE id=#{problemId}")
    Problem selectProblem(Problem problem);
}
