package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.GamePermission;
import top.linjhs.usagictfplatformbackend.pojo.User;

import java.util.Map;

/**
 * 用户 Service
 *
 * @author LinJHS
 * @version 1.0
 */
public interface UserService {

    /**
     * 登录接口
     *
     * @param user 登录用户名和密码
     * @return top.linjhs.usagictfplatformbackend.pojo.User
     * @author LinJHS
     */
    User login(User user);

    /**
     * 注册接口
     *
     * @param user 注册用户名、密码和邮箱
     * @return boolean
     * @author LinJHS
     */
    boolean register(User user);

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param user 携带用户 ID
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author LinJHS
     */
    Map<String, Object> getUserSelf(User user);

    /**
     * 根据用户名查询用户信息
     *
     * @param user 携带用户名
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author LinJHS
     */
    Map<String, Object> getUserOther(User user);

    /**
     * 更新用户信息
     *
     * @param user 携带用户信息
     * @return boolean
     * @author LinJHS
     */
    boolean updateUser(User user);

//    /**
//     * 根据用户名查询用户加入的团队
//     *
//     * @param user 用户名
//     * @return java.lang.String
//     * @author LinJHS
//     */
//    String getTeams(User user);

    /**
     * 查询用户是否有权限访问某比赛
     *
     * @param gamePermission 接收用户名和比赛 ID
     * @return boolean
     * @author LinJHS
     */
    boolean checkGamePermission(GamePermission gamePermission);

    /**
     * 通过 gameId 获得排行榜
     *
     * @param gameId 比赛 ID
     * @return java.lang.String
     * @author LinJHS
     */
    String getRank(int gameId);
}
