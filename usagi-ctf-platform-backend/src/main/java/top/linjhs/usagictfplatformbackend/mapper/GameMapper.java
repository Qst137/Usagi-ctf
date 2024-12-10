package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.linjhs.usagictfplatformbackend.pojo.Game;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 用户 Mapper
 *
 * @author LinJHS
 * @version 1.0
 */
@Mapper
public interface GameMapper {
    /**
     * 获取全部比赛信息
     *
     * @return List<Game>
     * @author qst137
     */
    List<Game> getAllGames();

    /**
     * 获取全部开着的比赛信息
     *
     * @return List<Game>
     * @author qst137
     */
    List<Game> getAllOpenGames();

    /**
     * 获取单个比赛信息
     *
     * @param game 存id
     * @return Game
     * @author qst137
     */
    @Select("SELECT * FROM usagi_games WHERE id = #{id}")
    Game getGameById(Game game);

    /**
     * 插入新比赛
     *
     * @param game 存储比赛信息（无is_open)
     * @author qst137
     */
    void addGame(Game game);

    /**
     * 为新比赛建表
     *
     * @param game 存储比赛id
     * @author qst137
     */

    void createGameTable(Game game);

    /**
     * 更新比赛信息
     *
     * @param game 存储比赛信息
     * @author qst137
     */

    void updateGame(Game game);

    /**
     * 控制比赛开关
     *
     * @param game 存储id
     * @author qst137
     */
    @Update("UPDATE usagi_games SET is_open = #{isOpen} WHERE id = #{id}")
    void switchGame(Game game);

    /**
     * 删除usagi_table中比赛
     *
     * @param game 存储id
     * @author qst137
     */
    @Delete("DELETE FROM usagi_games WHERE id=#{id}")
    void deleteGame(Game game);

    /**
     * 删除比赛表
     *
     * @param game 存储 id
     * @author qst137
     */
    @Update("DROP TABLE usagi_games_#{id}")
    void dropGameTable(Game game);

    /**
     * 创建公告表
     *
     * @param game 携带比赛 id
     * @author qst137
     */
    void createNoticeTable(Game game);

    /**
     * 删除公告表
     *
     * @param game 携带比赛 id
     * @author qst137
     */
    @Update("DROP TABLE usagi_games_notice_#{id}")
    void dropNoticeTable(Game game);
}
