package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.linjhs.usagictfplatformbackend.pojo.GameAndUser;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.GameService;


/**
 * 比赛 Controller
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@RestController
public class GameController {
    @Autowired
    private GameService gameService;

    /**
     * 拉取比赛列表
     *
     * @param user 传入用户名用于校验身份
     * @return Result
     * @author qst137
     */
    @PostMapping("ctf_api/games")
    public Result games(@RequestBody User user) {
        try {
            if (user.getUsername().equals("admin"))
                return Result.success(gameService.getAll());
            return Result.success(gameService.getAllOpen(user));
        } catch (Exception e) {
            return Result.fail();
        }
    }

    /**
     * 创建新比赛
     *
     * @param gameAndUser 传game参数，校验user是否为admin
     * @return Result
     * @author qst137
     */
    @PostMapping("ctf_api/game")
    public Result found(@RequestBody GameAndUser gameAndUser) {
        Game game = gameAndUser.getGame();
        User user = gameAndUser.getUser();
        try {
            if (game.getGameName() == null || game.getGameIntro() == null || game.getTimeStart() == null || game.getTimeEnd() == null) {
                return Result.fail(); //所有属性不得为空
            }
            if (user.getUsername().equals("admin")) {
                if (gameService.found(game)) {
                    return Result.success();
                }
                return Result.fail();
            }
            return new Result(403, "forbidden", null);

        } catch (Exception e) {
            return Result.fail();
        }
    }

    /**
     * 编辑比赛信息
     *
     * @param gameAndUser 传game参数，校验user是否为admin;game必须有id
     * @return Result
     * @author qst137
     */
    @PutMapping("ctf_api/game")
    public Result edit(@RequestBody GameAndUser gameAndUser) {
        Game game = gameAndUser.getGame();
        User user = gameAndUser.getUser();
        try {
            if (game.getGameName() == null && game.getGameIntro() == null && game.getTimeStart() == null && game.getTimeEnd() == null) {
                return Result.fail(); //全为空不显示更新成功
            }
            if (user.getUsername().equals("admin")) {
                if (gameService.updateInfo(game)) {
                    return Result.success();
                }
                return Result.fail();

            }
            return new Result(403, "forbidden", null);
        } catch (Exception e) {
            log.info(e.toString());
            return Result.fail();
        }
    }

    /**
     * 控制比赛开启/关闭
     *
     * @param gameAndUser 携带用户名与比赛 id
     * @param status      携带开 / 关
     * @return Result
     * @author qst137
     */
    @PatchMapping ("ctf_api/game/{status}")
    public Result switchOneGame(@RequestBody GameAndUser gameAndUser, @PathVariable(value = "status") Integer status) {
        Game game = gameAndUser.getGame();
        User user = gameAndUser.getUser();
        game.setIsOpen(status);
        try {
            if (user.getUsername().equals("admin")) {
                if (gameService.switchOne(game)) {
                    return Result.success();
                }
                return Result.fail();
            }
            return new Result(403, "forbidden", null);
        } catch (Exception e) {
            return Result.fail();
        }
    }

    /**
     * 删除比赛
     *
     * @param gameAndUser 携带用户名与比赛id
     * @return Result
     * @author qst137
     */
    @DeleteMapping("ctf_api/game")
    public Result deleteOneGame(@RequestBody GameAndUser gameAndUser) {
        Game game = gameAndUser.getGame();
        User user = gameAndUser.getUser();
        try {
            if (user.getUsername().equals("admin")) {
                if (gameService.remove(game)) {
                    return Result.success();
                }
                return Result.fail();
            }
            return new Result(403, "forbidden", null);
        } catch (Exception e) {
            log.info(e.getMessage());
            return Result.fail();
        }
    }


}
