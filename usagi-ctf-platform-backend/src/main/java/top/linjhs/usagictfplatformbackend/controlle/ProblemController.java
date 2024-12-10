package top.linjhs.usagictfplatformbackend.controlle;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.ProblemAndUser;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.ProblemService;
import top.linjhs.usagictfplatformbackend.service.UserService;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.Map;

/**
 * 题目 Controller
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@RestController
public class ProblemController {

    @Autowired
    private ProblemService problemService;
    @Autowired
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取题目详情
     *
     * @param problemAndUser 传入题目和用户
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    @GetMapping("/ctf_api/problems")
    public Result getProblemDetail(@RequestBody ProblemAndUser problemAndUser) {
        User user = problemAndUser.getUser();
        Problem problem = problemAndUser.getProblem();
        if (null == user || null == problem ||
                null == user.getUsername() || 0 == problem.getProblemId() || 0 == problem.getGameId())
            return Result.fail();
//        redisUtils.openGamePermission(problem.getGameId());
        if (!redisUtils.checkUserGamePermission(user.getUsername(), problem.getGameId(),
                userService::checkGamePermission)) { // 没有查看比赛的权限
            return Result.fail();
        }
        Map<String, Object> problemReceived = problemService.getProblemDetail(problem);
        return Result.success(problemReceived);
    }

}
