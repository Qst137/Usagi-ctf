package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.DockerManageService;

/**
 * 容器管理 Controller
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@RestController
public class DockerManageController {
    @Autowired
    DockerManageService dockerManageService;

    /**
     * 查看所有容器
     *
     * @param user 携带操作者
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("/ctf_api/container/0")
    public Result getAll(@RequestBody User user) {
        if (!user.getUsername().equals("admin")) {
            return Result.fail();
        }
        return Result.success(dockerManageService.listAll());
    }

//    /**
//     * 查看指定比赛的容器
//     *
//     * @param gameAndUser 携带 gameId 和请求人 username
//     * @return top.linjhs.usagictfplatformbackend.pojo.Result
//     * @author qst137
//     */
//    @PostMapping("/ctf_api/container/1")
//    public Result byGame(@RequestBody GameAndUser gameAndUser) {
//        if (!gameAndUser.getUser().getUsername().equals("admin")) {
//            return Result.fail();
//        }
//        return Result.success(dockerManageService.listByGame(gameAndUser.getGame().getId()));
//
//    }
//
//    /**
//     * 查看指定题目的容器
//     *
//     * @param problemAndUser 携带 gameId，problemId 和请求人身份
//     * @return top.linjhs.usagictfplatformbackend.pojo.Result
//     * @author qst137
//     */
//    @PostMapping("/ctf_api/container/2")
//    public Result byProblem(@RequestBody ProblemAndUser problemAndUser) {
//        if (!problemAndUser.getUser().getUsername().equals("admin")) {
//            return Result.fail();
//        }
//        return Result.success(
//                dockerManageService.listByProblem(
//                        problemAndUser.getProblem().getGameId(),
//                        problemAndUser.getProblem().getProblemId()
//                )
//        );
//
//    }
//
//    /**
//     * 查看指定用户开的容器
//     *
//     * @param user 携带请求人身份和被查看的用户的身份
//     * @return top.linjhs.usagictfplatformbackend.pojo.Result
//     * @author qst137
//     */
//    @PostMapping("/ctf_api/container/3")
//    public Result byUsername(@RequestBody User user) {
//
//        if (!user.getUsername().equals("admin")) {
//            return Result.fail();
//        }
//        return Result.success(dockerManageService.listByUsername(user.getOtherUsername()));
//
//
//    }
}
