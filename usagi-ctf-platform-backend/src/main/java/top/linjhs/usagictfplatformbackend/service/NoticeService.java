package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.Notice;

import java.util.List;

/**
 * 通知相关接口
 *
 * @author qst137
 * @version 1.0
 */
public interface NoticeService {
    /**
     * 新增通知
     *
     * @param notice 携带 notice 的所有信息和表名
     * @return boolean
     * @author qst137
     */
    boolean newNotice(Notice notice);

    /**
     * 删除通知
     *
     * @param notice 携带 noticeId 和 gameId
     * @return boolean
     * @author qst137
     */
    boolean deleteNotice(Notice notice);

    /**
     * 拉取指定比赛的所有通知
     *
     * @param notice 携带 noticeId 和 gameId
     * @return java.utils.List<top.linjhs.usagictfplatformbackend.pojo.Notice>
     * @author qst137
     */
    List<Notice> getNoticeByGame(Notice notice);
}
