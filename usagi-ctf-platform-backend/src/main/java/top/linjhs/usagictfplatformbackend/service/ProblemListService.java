package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;

import java.util.List;

/**
 * 题目列表 Service
 *
 * @author qst137
 * @version 1.0
 */
public interface ProblemListService {
    /**
     * 拉取题目列表，附带当前队伍解题情况
     *
     * @param user 携带请求人 username
     * @param game 携带比赛 Id
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.Problem>
     * @author qst137
     */
    List<Problem> getProblems(User user, Game game);
}
