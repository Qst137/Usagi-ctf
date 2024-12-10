package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;

import java.util.List;

/**
 * 题目列表 Mapper
 *
 * @author qst137
 * @version 1.0
 */

@Mapper
public interface ProblemListMapper {
    /**
     * 返回题目列表
     *
     * @param game 携带 gameId
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.Problem>
     * @author qst137
     */
    List<Problem> getAll(Game game);

}
