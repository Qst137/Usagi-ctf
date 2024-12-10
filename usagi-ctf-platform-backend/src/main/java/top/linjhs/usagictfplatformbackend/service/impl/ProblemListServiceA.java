package top.linjhs.usagictfplatformbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.mapper.ProblemListMapper;
import top.linjhs.usagictfplatformbackend.mapper.UserMapper;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.ProblemListService;
import top.linjhs.usagictfplatformbackend.utils.ParseUtils;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.List;

/**
 * 题目列表
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@Service
public class ProblemListServiceA implements ProblemListService {
    @Autowired
    private ProblemListMapper problemListMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ParseUtils parseUtils;
    @Autowired
    private UserMapper userMapper;

    /**
     * 拉取题目列表
     *
     * @param user 用户名
     * @param game 比赛 Id
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.Problem>
     */
    public List<Problem> getProblems(User user, Game game) {
        Integer gameId = game.getId();
        List<Problem> list = redisUtils.queryList(
                RedisUtils.GAME_KEY + game.getId() + ":PROBLEMS",
                game,
                Problem.class,
                problemListMapper::getAll);
        list.forEach(problem -> problem.setGameId(gameId));
        if (user.getUsername().equals("admin")) {
            return list;
        }
        User userReceived = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class,
                userMapper::getUserByName);
        list.forEach(problem -> {
            if (problem.getProblemEnvi() == null || problem.getProblemEnvi().isEmpty()) {
                problem.setProblemEnvi("no Environment");
            } else {
                problem.setProblemEnvi("hidden");
            }
            problem.setFlag("hidden");
            List<Integer> solved = parseUtils.
                    stringToList(problem.getSolved()).
                    stream().
                    map(Integer::parseInt).
                    toList();
            problem.setBeSolved(solved.contains(Integer.parseInt(("0" + userReceived.getTeams()))));
        });
        list.removeIf(problem -> problem.getIsOpen().equals(0));
        return list;
    }
}
