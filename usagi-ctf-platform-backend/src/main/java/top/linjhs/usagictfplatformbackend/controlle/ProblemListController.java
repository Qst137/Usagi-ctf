package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.GameAndUser;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.ProblemListService;

/**
 * 题目列表 controller
 *
 * @author qst137
 * @version 1.0
 */
@RestController
@Slf4j
public class ProblemListController {
    @Autowired
    private ProblemListService problemListService;
    /**
     * 查询指定比赛的 id
     *
     * @param gameAndUser 携带 gameId 和 userName
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("/ctf_api/problems")
    public Result getProblems(@RequestBody GameAndUser gameAndUser){
        User user  =gameAndUser.getUser();
        Game game = gameAndUser.getGame();
//        if (!redisUtils.checkUserGamePermission(user.getUsername(), game.getId(),
//                userService::checkGamePermission)) { // 没有查看比赛的权限
//            log.info("test");
//            return Result.fail();
//        }
        return Result.success(problemListService.getProblems(user,game));

    }
}
