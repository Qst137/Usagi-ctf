package top.linjhs.usagictfplatformbackend.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.linjhs.usagictfplatformbackend.mapper.GameMapper;
import top.linjhs.usagictfplatformbackend.mapper.TeamMapper;
import top.linjhs.usagictfplatformbackend.mapper.UserMapper;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.Team;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.TeamService;
import top.linjhs.usagictfplatformbackend.utils.GeneralCheck;
import top.linjhs.usagictfplatformbackend.utils.ParseUtils;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.List;

/**
 * 队伍service实现类
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@Service
public class TeamServiceA implements TeamService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private GameMapper gameMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ParseUtils parseUtils;

    /**
     * 创建团队，同时将创建者加入团队
     *
     * @param team 传递队伍信息
     * @param user 传递队长 username
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTeam(Team team, User user) {

        if (GeneralCheck.incorrectTeamName(team.getTeamName())) { // 检查队伍名合法性
            return false;
        }
        team.setMembers(user.getUsername()); // 组建者直接加入并成为队长
        User captain = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class, userMapper::getUserByName); // 查到队长id
        if (!StringUtils.hasLength(captain.getTeams())) {
            captain.setIsCaptain(1);
        } else {
            log.info(captain.toString());
            return false;
        }
        teamMapper.createTeam(team);
        captain.setTeams("" + team.getId());
        userMapper.updateUser(captain);
        redisUtils.delete(RedisUtils.USER_KEY + captain.getUsername());
        return true;
    }

    /**
     * 将指定 id 的 user 加入指定 id 的团队
     *
     * @param team 传递队伍 id
     * @param user 传递 username
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMember(Team team, User user) {
        User userReceived = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class, userMapper::getUserByName);
        if (StringUtils.hasLength(userReceived.getTeams())) {
            return false;
        }
        Team teamReceived = redisUtils.query(RedisUtils.TEAM_KEY + team.getId(),
                team, Team.class, teamMapper::selectTeamById);
        List<String> memberList = parseUtils.stringToList(teamReceived.getMembers());
        memberList.add(user.getUsername());
        teamReceived.setMembers(parseUtils.listToString(memberList)); // 处理 memberList 转为字符串并放回 teamReceived;
        teamMapper.updateTeam(teamReceived);
        //更新 team.members
        List<String> teamList = parseUtils.stringToList(userReceived.getTeams());
        teamList.add("" + team.getId());
        userReceived.setTeams(parseUtils.listToString(teamList));
        userMapper.updateUser(userReceived);
        redisUtils.delete(RedisUtils.TEAM_KEY + team.getId());
        redisUtils.delete(RedisUtils.USER_KEY + user.getUsername()); // 更新后清除缓存
        return true;
    }

    /**
     * 删除团队
     *
     * @param team 传递队伍id
     * @param user 传递 username ，用于校验权限
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Team team, User user) {
        try{Team teamReceived = redisUtils.query(RedisUtils.TEAM_KEY + team.getId(),
                team, Team.class, teamMapper::selectTeamById);
            List<String> memberList = parseUtils.stringToList(teamReceived.getMembers());
            if (memberList.indexOf(user.getUsername()) != 0 && !user.getUsername().equals("admin")) // 校验队长身份
            {
                return false;
            }
            for (String m : memberList) { // 逐个删除成员 teams 中的该 teamId
                User u = new User();
                u.setUsername(m);
                User userReceived = redisUtils.query(RedisUtils.USER_KEY + m,
                        u, User.class, userMapper::getUserByName);
                userReceived.setIsCaptain(0);
                List<String> teamList = parseUtils.stringToList(userReceived.getTeams());
                teamList.remove("" + team.getId());
                String newTeams = parseUtils.listToString(teamList);
                userReceived.setTeams(newTeams);
                userMapper.updateUser(userReceived);
                redisUtils.delete(RedisUtils.USER_KEY + userReceived.getUsername()); // 更新后清除缓存
            }
            teamMapper.deleteTeam(team);
            redisUtils.delete(RedisUtils.TEAM_KEY + team.getId());
            return true;}
        catch(Exception e){
            log.info(e.toString());
            return false;
        }
    }

    /**
     * 报名比赛
     *
     * @param team 传递teamId
     * @param game 传递gameId
     * @param user 传递username
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signupGame(Team team, Game game, User user) {
        Team teamReceived = redisUtils.query(RedisUtils.TEAM_KEY + team.getId(),
                team, Team.class, teamMapper::selectTeamById);
        List<String> members = parseUtils.stringToList(teamReceived.getMembers());
        if (0 != members.indexOf(user.getUsername())) // 只有队长可以报名操作
            return false;
        List<String> gameIds = parseUtils.stringToList(teamReceived.getGames());
        if (gameIds.contains("" + game.getId())) // 不能重复加入
            return false;
        gameIds.add("" + game.getId());
        if (gameMapper.getGameById(game) == null) // 不能加入不存在的比赛
            return false;
        teamReceived.setGames(parseUtils.listToString(gameIds));
        teamMapper.updateTeam(teamReceived);
        redisUtils.delete(RedisUtils.TEAM_KEY + team.getId());
        return true;
    }

    /**
     * 接口：移除成员，允许队长 / 管理员操作，不允许移除队长
     *
     * @param user 传递 username
     * @param team 传递 teamId
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMember(User user, Team team) {
        Team teamReceived = redisUtils.query(RedisUtils.TEAM_KEY + team.getId(),
                team, Team.class, teamMapper::selectTeamById);
        List<String> members = parseUtils.stringToList(teamReceived.getMembers());
        if (0 != members.indexOf(user.getUsername()) && !user.getUsername().equals("admin")) //只有队长和管理员可以移除成员
            return false;
        if (user.getUsername().equals(user.getOtherUsername())) // 禁止移除队长
            return false;
        members.remove(user.getOtherUsername());
        User otherUser = new User();
        otherUser.setUsername(user.getOtherUsername());
        otherUser.setTeams("");
        teamReceived.setMembers(parseUtils.listToString(members));
        teamMapper.updateTeam(teamReceived);
        userMapper.updateUser(otherUser);
        redisUtils.delete(RedisUtils.TEAM_KEY + team.getId());
        redisUtils.delete(RedisUtils.USER_KEY + user.getOtherUsername());
        return true;
    }

    /**
     * 查询队伍信息
     *
     * @param team 携带 teamId
     * @return top.linjhs.usagictfplatformbackend.pojo.Team
     * @author qst137
     */
    @Override
    public Team getTeam(Team team){
        return redisUtils.query(RedisUtils.TEAM_KEY + team.getId(),
                team, Team.class, teamMapper::selectTeamById);
    }


//    /**
//     * 根据团队id查询报名的比赛id
//     *
//     * @param teamId 团队id
//     * @return java.util.List<java.lang.Integer>
//     * @author qst137
//     */
//    @Override
//    public List<Integer> getGamesOfTeam(Integer teamId) {
//        Team team = new Team();
//        team.setId(teamId);
//
//        Team teamReceived = redisUtils.query(RedisUtils.TEAM_KEY + teamId, team, Team.class,
//                teamMapper::selectTeamById);
//        if (!StringUtils.hasLength(teamReceived.getGames()))
//            return null;
//        return Arrays.stream(teamReceived.getGames().split(",")).map(s -> Integer.parseInt(s.trim()))
//                .collect(Collectors.toList());
//    }


}
