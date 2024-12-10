package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.linjhs.usagictfplatformbackend.pojo.*;
import top.linjhs.usagictfplatformbackend.service.TeamService;

@Slf4j
@RestController
public class TeamController {
    @Autowired
    private TeamService teamService;

    /**
     * 创建新团队，将创建者设为第一个成员
     *
     * @param teamAndUser 传入 team 信息，和队长 username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/team")
    public Result createTeam(@RequestBody TeamAndUser teamAndUser) {
        User user = teamAndUser.getUser();
        Team team = teamAndUser.getTeam();
        try {
            if (teamService.createTeam(team, user))
                return Result.success();
            return Result.fail();
        } catch (Exception e) {
            log.info(e.getCause().getMessage());
            return Result.fail();
        }
    }

    /**
     * 解散团队
     *
     * @param teamAndUser 传入teamId 和 username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @DeleteMapping("ctf_api/team")
    public Result deleteTeam(@RequestBody TeamAndUser teamAndUser) {
        User user = teamAndUser.getUser();
        Team team = teamAndUser.getTeam();
        try {
            if (teamService.deleteTeam(team, user))
                return Result.success();
            return Result.fail();
        } catch (Exception e) {
            return Result.fail();
        }
    }

    /**
     * 加入团队
     *
     * @param teamAndUser 传入 teamId username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PutMapping("ctf_api/team/01")
    public Result addMember(@RequestBody TeamAndUser teamAndUser) {
        User user = teamAndUser.getUser();
        Team team = teamAndUser.getTeam();
        user.setUsername(user.getOtherUsername());
        try {
            if (teamService.addMember(team, user))
                return Result.success();
            return Result.fail();
        } catch (Exception e) {
            log.info(e.getCause().getMessage());
            return Result.fail();
        }
    }

    /**
     * 加入比赛
     *
     * @param userTeamGame 传入 teamId username gameId
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/team/01")
    public Result signupGame(@RequestBody UserTeamGame userTeamGame){
        User user = userTeamGame.getUser();
        Team team = userTeamGame.getTeam();
        Game game = userTeamGame.getGame();
        try {
            if (teamService.signupGame(team, game, user))
                return Result.success();
            return Result.fail();
        } catch (Exception e) {
            return Result.fail();
        }
    }
    /**
     * 移除团队成员
     *
     * @param teamAndUser 传入teamId 两个username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @DeleteMapping("ctf_api/team/01")
    public Result removeMember(@RequestBody TeamAndUser teamAndUser){
        User user = teamAndUser.getUser();
        Team team = teamAndUser.getTeam();
        try {
            if (teamService.removeMember(user,team))
                return Result.success();
            return Result.fail();
        } catch (Exception e) {
            return Result.fail();
        }
    }
    /**
     * 查询队伍信息
     *
     * @param team 传入 teamId
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PutMapping("ctf_api/team")
    public Result getTeam(@RequestBody Team team){
        Team teamReceived = teamService.getTeam(team);
        log.info(team.toString());
        if (teamReceived==null){
            return Result.fail();
        }
        return Result.success(teamReceived);
    }


}
