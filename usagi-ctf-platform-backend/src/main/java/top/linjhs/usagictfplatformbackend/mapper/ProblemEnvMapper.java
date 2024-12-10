package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.linjhs.usagictfplatformbackend.pojo.Problem;

/**
 * 题目环境 mapper
 *
 * @author qst137
 * @version 1.0
 */
@Mapper
public interface ProblemEnvMapper {
    /**
     * 查询附件位置
     *
     * @param problem 传递题目 id
     * @return Java.lang.String
     * @author qst137
     */
    @Select("SELECT problem_atta FROM usagi_games_${gameId} where id=#{problemId}")
    String findAttachment (Problem problem);

    /**
     * 查询 Dockerfile 位置
     *
     * @param problem 传递题目 id
     * @return Java.lang.String
     * @author qst137
     */
    @Select("SELECT problem_envi FROM usagi_games_${gameId} where id=#{problemId}")
    String findDockerfile (Problem problem);
}
