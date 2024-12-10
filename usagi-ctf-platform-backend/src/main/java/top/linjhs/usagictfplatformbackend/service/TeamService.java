package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.Team;
import top.linjhs.usagictfplatformbackend.pojo.User;

/**
 * 团队Service
 *
 * @author qst137
 * @version 1.0
 */
public interface TeamService {
    /**
     * 接口：创建团队，同时将创建者加入团队
     *
     * @param team 传递队伍信息
     * @param user 传递userid
     * @return boolean
     * @author qst137
     */
    boolean createTeam(Team team, User user);

    /**
     * 接口：删除团队
     *
     * @param team 传递队伍id
     * @param user 传递 username ，用于校验权限
     * @return boolean
     * @author qst137
     */
    boolean deleteTeam(Team team, User user);

    /**
     * 接口：将指定 id 的 user 加入指定 id 的团队
     *
     * @param team 传递队伍id
     * @param user 传递username
     * @return boolean
     * @author qst137
     */
    boolean addMember(Team team, User user);

    /**
     * 报名比赛
     *
     * @param team 传递 teamId
     * @param game 传递 gameId
     * @param user 传递 username
     * @return boolean
     * @author qst137
     */
    boolean signupGame(Team team, Game game, User user);

    /**
     * 接口：移除成员
     *
     * @param user 传递 username
     * @param team 传递 teamId
     * @return boolean
     * @author qst137
     */
    boolean removeMember(User user,Team team);

    /**
     * 查询队伍信息
     *
     * @param team 携带 teamId
     * @return top.linjhs.usagictfplatformbackend.pojo.Team
     * @author qst137
     */
    Team getTeam(Team team);

//    /**
//     * 接口：根据团队id查询报名的比赛id
//     *
//     * @param teamId 团队id
//     * @return java.util.List<java.lang.Integer>
//     * @author qst137
//     */
//    List<Integer> getGamesOfTeam(Integer teamId);

}
