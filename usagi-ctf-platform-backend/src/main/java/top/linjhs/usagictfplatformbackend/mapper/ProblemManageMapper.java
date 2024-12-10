package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import top.linjhs.usagictfplatformbackend.pojo.Problem;

/**
 * 题目管理 Mapper
 *
 * @author qst137
 * @version 1.0
 */
@Mapper
public interface ProblemManageMapper {
    /**
     * 添加题目
     *
     * @param problem 携带添加的题目的所有信息
     * @author qst137
     */
    void addProblem (Problem problem);

    /**
     * 删除题目
     *
     * @param problem 携带题目 id
     * @author qst137
     */
    @Delete("DELETE FROM usagi_games_${gameId} WHERE id=#{problemId}")
    void deleteProblem (Problem problem);

    /**
     * 修改题目信息
     *
     * @param problem 携带修改的题目信息
     * @author qst137
     */
    void updateProblem (Problem problem);

}
