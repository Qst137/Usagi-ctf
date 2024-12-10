package top.linjhs.usagictfplatformbackend.service;

import java.util.List;

import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.User;

/**
 * 比赛 Service
 *
 * @author LinJHS
 * @version 1.0
 */
public interface GameService {
    /**
     * 拉取比赛列表
     *
     * @return List<Game>
     * @author qst137
     */
    List<Game> getAll();

    /**
     * 拉取比赛列表接口，仅拉取开放的比赛（面向用户）
     *
     * @param user 传入用户
     * @return List<Game>
     * @author qst137
     */
    List<Game> getAllOpen(User user);

    /**
     * 修改比赛信息
     *
     * @param game 携带比赛信息
     * @return boolean
     * @author qst137
     */
    boolean updateInfo(Game game);

    /**
     * 开关比赛
     *
     * @param game 携带比赛id
     * @return boolean
     * @author qst137
     */
    boolean switchOne(Game game);

    /**
     * 创建新比赛，添行+建表
     *
     * @param game 携带比赛信息
     * @return boolean
     * @author qst137
     */
    boolean found(Game game);

    /**
     * 移除比赛
     *
     * @param game 携带比赛id
     * @return boolean
     * @author qst137
     */
    boolean remove(Game game);




}
