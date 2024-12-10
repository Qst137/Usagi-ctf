package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.linjhs.usagictfplatformbackend.pojo.*;
import top.linjhs.usagictfplatformbackend.service.ProblemEnvService;
import top.linjhs.usagictfplatformbackend.utils.DockerUtils;

/**
 * 题目环境 Controller
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@RestController
public class ProblemEnvController {
    @Autowired
    ProblemEnvService problemEnvService;

    @Autowired
    DockerUtils dockerUtils;

    /**
     * 检查镜像 (管理员操作)
     *
     * @param problemAndUser 携带
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/image")
    public Result testImage(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        User user = problemAndUser.getUser();
        if (!user.getUsername().equals("admin")) {
            return Result.fail();
        }
        switch (problemEnvService.testEnvironment(problem)) {
            case 0 -> {
                return new Result(200, "镜像存在", null);
            }
            case -1 -> {
                return new Result(200, "镜像不存在", null);
            }
            case 1 -> {
                return new Result(200, "非容器题", null);
            }
        }
        return Result.fail();
    }

    /**
     * 构建 / 重构镜像 (管理员操作)
     *
     * @param problemAndUser 携带用户名和要构建的题目
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PutMapping("ctf_api/image")
    public Result createImage(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        User user = problemAndUser.getUser();
        if (!user.getUsername().equals("admin")) {
            return Result.fail();
        }
        switch (problemEnvService.buildEnvironment(problem)) {
            case 0 -> {
                return new Result(200, "构建成功", null);
            }
            case 1 -> {
                return new Result(200, "非容器题", null);
            }
            case -1 -> {
                return new Result(404, "容器题构建失败", null);
            }
        }
        return Result.fail();
    }


    /**
     * 启动题目容器
     *
     * @param problemAndUser 携带 gameId ， problemId 和 username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/container")
    public Result startProblem(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        User user = problemAndUser.getUser();
        int number = dockerUtils.containerNumber(user.getUsername());
        if (number >= 2) {
            return new Result(404, "最多同时开启两个容器", null);
        }
        if (number == -1) {
            log.info("pass1");
            return Result.fail();
        }
        try {
            int port = problemEnvService.startContainer(problem, user);
            if (port == -1) {
                return Result.fail();
            }
            return Result.success(port);
        } catch (Exception e) {
            return Result.fail();
        }
    }

    /**
     * 销毁题目容器
     *
     * @param problemAndUser 携带 gameId ， problemId 和 username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @DeleteMapping("ctf_api/container")
    public Result killProblem(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        User user = problemAndUser.getUser();
        if (
                problemEnvService.removeContainer(problem, user)
        ) {
            return Result.success();
        }
        return Result.fail(); //容器不存在
    }

    /**
     * 检查题目容器
     *
     * @param  problemAndUser 携带 gameId ， problemId 和 username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/check")
    public Result checkContainer(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        User user = problemAndUser.getUser();
        try {
            MyContainer myContainer = problemEnvService.checkContainer(problem, user);
            if(myContainer==null){
                return Result.success();
            }
            return Result.success(myContainer);
        } catch (Exception e) {
            return Result.fail();
        }

    }
}
