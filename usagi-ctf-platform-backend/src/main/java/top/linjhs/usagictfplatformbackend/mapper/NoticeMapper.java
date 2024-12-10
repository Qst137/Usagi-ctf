package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import top.linjhs.usagictfplatformbackend.pojo.Notice;

import java.util.List;

/**
 * 通知 mapper
 *
 * @author qst137
 * @version 1.0
 */
@Mapper
public interface NoticeMapper {
    /**
     * 添加通知
     *
     * @param notice 携带通知内容
     * @author qst137
     */
    void addNotice(Notice notice);

    /**
     * 删除通知
     *
     * @param notice 携带通知 ID 与表名
     * @author qst137
     */
    @Delete("DELETE FROM usagi_games_notice_${gameId} WHERE id = #{id}")
    void deleteNotice(Notice notice);

    /**
     * 返回某一类所有通知
     *
     * @param notice 携带通知类型与表名
     * @return java.utils
     * @author qst137
     */
    List<Notice> selectNoticeByGame(Notice notice);


}
