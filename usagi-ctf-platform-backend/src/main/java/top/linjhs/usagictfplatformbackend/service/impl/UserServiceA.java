package top.linjhs.usagictfplatformbackend.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import top.linjhs.usagictfplatformbackend.mapper.TeamMapper;
import top.linjhs.usagictfplatformbackend.mapper.UserMapper;
import top.linjhs.usagictfplatformbackend.pojo.GamePermission;
import top.linjhs.usagictfplatformbackend.pojo.Team;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.UserService;
import top.linjhs.usagictfplatformbackend.utils.GeneralCheck;
import top.linjhs.usagictfplatformbackend.utils.ParseUtils;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.*;

/**
 * 用户 Service 实现类
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Service
public class UserServiceA implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TeamMapper teamMapper;

    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private ParseUtils parseUtils;

    private static final String passwordSalt = "9@8Y*C8)h+Bn^$Me2";

    /**
     * 登录接口
     *
     * @param user 登录用户名和密码
     * @return top.linjhs.usagictfplatformbackend.pojo.User
     * @author LinJHS
     */
    @Override
    public User login(User user) {
        User userReceived = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class,
                userMapper::getUserByName);
        if (null == userReceived)
            return null;
        if (userReceived.getPassword().equals(DigestUtils.md5DigestAsHex((user.getPassword() + passwordSalt).getBytes())))
            return userReceived;
        return null;
    }

    /**
     * 注册
     *
     * @param user 登录用户名、密码和邮箱
     * @return boolean
     * @author LinJHS
     */
    @Override
    public boolean register(User user) {
        try {
            user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + passwordSalt).getBytes()));
            userMapper.register(user);
            redisUtils.delete(RedisUtils.USER_KEY + user.getUsername());
        } catch (Exception e) { // 遇到冲突
            return false;
        }
        return true;
    }

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param user 携带用户 ID
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author LinJHS
     */
    @Override
    public Map<String, Object> getUserSelf(User user) {
        User userReceive = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class,
                userMapper::getUserByName);
        if (null == userReceive)
            return null;
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userReceive.getId());
        claims.put("username", userReceive.getUsername());
        claims.put("userIntro", userReceive.getUserIntro());
        claims.put("email", userReceive.getEmail());
        claims.put("phoneNumber", userReceive.getPhoneNumber());
        claims.put("name", userReceive.getName());
        claims.put("isCaptain", userReceive.getIsCaptain());

        // 获取用户团队名
        String teamId;
        teamId = userReceive.getTeams();
        if (teamId == null || teamId.isEmpty()) { // 获取的队伍ID为空
            claims.put("teamId", -1);
            claims.put("teamName", "");
            claims.put("teamIntro", "");
            claims.put("teamMembers", "");
            claims.put("teamGames", "");
        } else {
            Team team = new Team();
            team.setId(Integer.parseInt(teamId));
            Team teamReceive = redisUtils.query(RedisUtils.TEAM_KEY + teamId,
                    team, Team.class,
                    teamMapper::selectTeamById);
            if (teamReceive == null) {
                claims.put("teamId", -1);
                claims.put("teamName", "");
                claims.put("teamIntro", "");
                claims.put("teamMembers", "");
                claims.put("teamGames", "");
            } else {
                claims.put("teamId", team.getId());
                claims.put("teamName", teamReceive.getTeamName());
                claims.put("teamIntro", teamReceive.getTeamIntro());
                claims.put("teamMembers", teamReceive.getMembers());
                claims.put("teamGames", teamReceive.getGames());
            }
        }
        return claims;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param user 携带用户名
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author LinJHS
     */
    @Override
    public Map<String, Object> getUserOther(User user) {
        User userReceive = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class,
                userMapper::getUserByName);
        if (null == userReceive)
            return null;
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userReceive.getId());
        claims.put("username", userReceive.getUsername());
        claims.put("userIntro", userReceive.getUserIntro());
        return claims;
    }

    /**
     * 更新用户信息
     *
     * @param user 携带需要更新的用户信息
     * @return boolean
     * @author LinJHS
     */
    @Override
    public boolean updateUser(User user) {
        try {
            if (GeneralCheck.incorrectUsername(user.getUsername())) // 用户名不合法
                return false;
            if (!(user.getNewPassword() == null || user.getNewPassword().isEmpty())) { // 检查是否更新密码，如果需要更新密码需要携带旧密码
                if (user.getPassword() == null || user.getPassword().isEmpty()) // 没有携带旧密码
                    return false;
                if (null == login(user))// 携带的密码错误
                    return false;
                if (GeneralCheck.incorrectPassword(user.getNewPassword())) // 密码不合法
                    return false;
                // 将新的密码赋值到旧的密码里面
                user.setPassword(DigestUtils.md5DigestAsHex((user.getNewPassword() + passwordSalt).getBytes()));
            }
            if (!(user.getEmail() == null || user.getEmail().isEmpty()) && GeneralCheck.incorrectEmail(user.getEmail())) // 邮箱不合法
                return false;
            if (!(user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) && GeneralCheck.incorrectPhoneNumber(user.getPhoneNumber())) // 手机号不合法
                return false;
            userMapper.updateUser(user);
            redisUtils.delete(RedisUtils.USER_KEY + user.getUsername());
            return true;
        } catch (Exception ignoredE) {
            return false;
        }
    }

//    /**
//     * 根据用户名查询用户加入的团队
//     *
//     * @param user 用户名
//     * @return java.lang.String
//     * @author LinJHS
//     */
//    @Override
//    public String getTeams(User user) {
//        User userReceive = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
//                user, User.class,
//                userMapper::getUserByName);
//        if (null == userReceive)
//            return null;
//        String teamId;
//        teamId = userReceive.getTeams();
//        if (!StringUtils.hasLength(teamId)) // 获取的队伍为空
//            return "";
//        Team team = new Team();
//        team.setId(Integer.parseInt(teamId));
//        Team teamReceive = redisUtils.query(RedisUtils.TEAM_KEY + teamId,
//                team, Team.class,
//                teamMapper::selectTeamById);
//        if (teamReceive == null)
//            return null;
//        return teamReceive.getTeamName();
//    }

    /**
     * 查询用户是否有权限访问某比赛
     *
     * @param gamePermission 接收用户名和比赛 ID
     * @return boolean
     * @author LinJHS
     */
    @Override
    public boolean checkGamePermission(GamePermission gamePermission) {
        User user = new User();
        user.setUsername(gamePermission.getUsername());
        int gameId = gamePermission.getGameId();
        User userReceive = redisUtils.query(RedisUtils.USER_KEY + user.getUsername(),
                user, User.class,
                userMapper::getUserByName);
        if (null == userReceive)
            return false;
        String teamId;
        teamId = userReceive.getTeams();
        if (!StringUtils.hasLength(teamId)) // 获取的队伍为空
            return false;
        Team team = new Team();
        team.setId(Integer.parseInt(teamId));
        Team teamReceive = redisUtils.query(RedisUtils.TEAM_KEY + teamId,
                team, Team.class,
                teamMapper::selectTeamById);
        if (teamReceive == null)
            return false;
        List<String> gameList = parseUtils.stringToList(teamReceive.getGames());
        for (String element : gameList) {
            if (Integer.parseInt(element) == gameId)
                return true;
        }
        return false;
    }

    /**
     * 通过 gameId 获得排行榜
     *
     * @param gameId 比赛 ID
     * @return java.lang.String
     * @author LinJHS
     */
    @Override
    public String getRank(int gameId) {
        return redisUtils.get(RedisUtils.RANK_KEY + gameId);
    }
}
