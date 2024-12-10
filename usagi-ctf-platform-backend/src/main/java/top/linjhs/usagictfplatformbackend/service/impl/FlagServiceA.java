package top.linjhs.usagictfplatformbackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.mapper.FlagMapper;
import top.linjhs.usagictfplatformbackend.mapper.ProblemMapper;
import top.linjhs.usagictfplatformbackend.mapper.UserMapper;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.FlagService;
import top.linjhs.usagictfplatformbackend.utils.DockerUtils;
import top.linjhs.usagictfplatformbackend.utils.ParseUtils;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.List;

@Service
public class FlagServiceA implements FlagService {
    @Autowired
    private DockerUtils dockerUtils;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private ParseUtils parseUtils;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FlagMapper flagMapper;

    /**
     * 提交 flag
     *
     * @param user    携带用户名
     * @param problem 携带 problemId，gameId 和 flag
     * @return int 1 为错误， 0 为正确
     * @author qst137
     */
    public boolean checkFlag(User user, Problem problem) {
        Problem problemReceived = redisUtils.query(
                RedisUtils.PROBLEM_KEY + problem.getGameId() + ":" + problem.getProblemId(),
                problem,
                Problem.class,
                problemMapper::selectProblem
        );
        User userReceived = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class,
                userMapper::getUserByName);
        if(userReceived.getTeams().isEmpty()){
            throw new RuntimeException();
        }
        List<String> list = parseUtils.stringToList(problemReceived.getSolved());

        if( problem.getFlag().equals(dockerUtils.generateFlag(user, problemReceived))) {
            if (!list.contains(userReceived.getTeams())) {
                list.add(userReceived.getTeams());
                problem.setSolved(
                        parseUtils.listToString(list)
                );
                flagMapper.updateSolved(problem);
                redisUtils.delete(RedisUtils.GAME_KEY + problem.getGameId() + ":PROBLEMS");
                redisUtils.delete(RedisUtils.PROBLEM_KEY + problem.getGameId() + ":" + problem.getProblemId());
            }
            return true;
        }
        return false;
    }
}
