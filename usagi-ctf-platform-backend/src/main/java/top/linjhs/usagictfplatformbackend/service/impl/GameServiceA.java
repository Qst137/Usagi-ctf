package top.linjhs.usagictfplatformbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.linjhs.usagictfplatformbackend.mapper.TeamMapper;
import top.linjhs.usagictfplatformbackend.mapper.UserMapper;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import top.linjhs.usagictfplatformbackend.pojo.Team;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.GameService;
import top.linjhs.usagictfplatformbackend.mapper.GameMapper;
import top.linjhs.usagictfplatformbackend.utils.ParseUtils;
import top.linjhs.usagictfplatformbackend.utils.RankUtils;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 比赛 Service 实现类
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Service
public class GameServiceA implements GameService {

    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RankUtils rankUtils;
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private ParseUtils parseUtils;

    /**
     * 拉取比赛列表接口，拉取全部比赛（面向 admin ）
     *
     * @return List<Game>
     * @author qst137
     */
    @Override
    public List<Game> getAll() {
        return redisUtils.queryList(RedisUtils.GAME_KEY + "ALL_GAMES", Game.class,
                gameMapper::getAllGames);
    }

    /**
     * 拉取比赛列表接口，仅拉取开放的比赛（面向用户），并返回当前用户是否报名了此比赛
     *
     * @param user 携带用户名，用于判断该用户是否报名该比赛
     * @return List<Game>
     * @author qst137
     */
    @Override
    public List<Game> getAllOpen(User user) {
        List<Game> list = redisUtils.queryList(RedisUtils.GAME_KEY + "ALL_GAMES_OPEN", Game.class,
                gameMapper::getAllOpenGames);
        User userReceived = redisUtils.query(
                RedisUtils.USER_KEY + user.getUsername(),
                user, User.class, userMapper::getUserByName);
        List<String> listApplied = new ArrayList<>();
        if (StringUtils.hasLength(userReceived.getTeams())) {
            Team team = new Team();
            team.setId(Integer.parseInt(userReceived.getTeams()));
            listApplied.addAll(parseUtils.stringToList(redisUtils.query(RedisUtils.TEAM_KEY + team.getId(), team,
                    Team.class, teamMapper::selectTeamById).getGames()));
        }
        for (Game g : list) {
            if (listApplied.contains("" + g.getId())) {
                g.setIsApplied(1);
            } else {
                g.setIsApplied(0);
            }
        }
        return list;

    }

    /**
     * 修改比赛信息接口
     *
     * @param game 携带比赛信息
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean updateInfo(Game game) {
        try {
            gameMapper.updateGame(game);
            redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES_OPEN");//清空缓存
            redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES");//清空缓存
        } catch (Exception e) {
            log.info(e.getCause().getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据id开关指定比赛接口
     *
     * @param game 携带比赛id
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean switchOne(Game game) {
        try {
            gameMapper.switchGame(game);
            if(game.getIsOpen().equals(1)){
                rankUtils.addGame(game.getId());
            }if(game.getIsOpen().equals(0)){
                rankUtils.closeGame(game.getId());
            }
            redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES_OPEN");//清空缓存
            redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES");//清空缓存
        } catch (Exception e) {
            return false;
        }
        return true;


    }

    /**
     * 创建新比赛，添行+建表
     *
     * @param game 存储新比赛的信息
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean found(Game game) {
        gameMapper.addGame(game);
        gameMapper.createGameTable(game);
        redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES_OPEN"); // 清空缓存
        redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES"); // 清空缓存
        gameMapper.createNoticeTable(game);
        redisUtils.delete(RedisUtils.NOTICE_KEY + game.getId());
        rankUtils.addGame(game.getId());
        return true;
        // controller要检查一系列属性不为空
    }

    /**
     * 移除比赛
     *
     * @param game 携带比赛id
     * @return boolean
     * @author qst137
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Game game) {
        gameMapper.deleteGame(game);
        gameMapper.dropGameTable(game);
        redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES_OPEN");//清空缓存
        redisUtils.delete(RedisUtils.GAME_KEY + "ALL_GAMES");//清空缓存
        gameMapper.dropNoticeTable(game);
        redisUtils.delete(RedisUtils.NOTICE_KEY + game.getId());
        rankUtils.closeGame(game.getId());
        return true;
    }

}
