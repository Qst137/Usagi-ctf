package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import top.linjhs.usagictfplatformbackend.pojo.Problem;

/**
 * Flag 提交相关 Mapper
 *
 * @author qst137
 * @version 1.0
 */
@Mapper
public interface FlagMapper {
    /**
     * 更新解出列表
     *
     * @param problem 携带题目信息与 solved
     * @author qst137
     */
    @Update("UPDATE usagi_games_${gameId} SET solved=#{solved} WHERE id=#{problemId}")
    void updateSolved(Problem problem);
}
