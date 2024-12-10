package top.linjhs.usagictfplatformbackend.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.mapper.NoticeMapper;
import top.linjhs.usagictfplatformbackend.pojo.Notice;
import top.linjhs.usagictfplatformbackend.service.NoticeService;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NoticeServiceA implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 新增通知
     *
     * @param notice 携带 notice 的所有信息和表名
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean newNotice(Notice notice) {
        try {
            noticeMapper.addNotice(notice);
            redisUtils.delete(RedisUtils.NOTICE_KEY + notice.getGameId());
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }


    /**
     * 删除通知
     *
     * @param notice 携带 noticeId 和 gameId
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean deleteNotice(Notice notice) {
        try {
            noticeMapper.deleteNotice(notice);
            redisUtils.delete(RedisUtils.NOTICE_KEY + notice.getGameId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 拉取指定比赛的所有通知
     *
     * @param notice 携带gameId
     * @return java.utils.List<top.linjhs.usagictfplatformbackend.pojo.Notice>
     * @author qst137
     */
    @Override
    public List<Notice> getNoticeByGame(Notice notice) {
        try {
            return redisUtils.queryList(
                    RedisUtils.NOTICE_KEY + notice.getGameId(),
                    notice,
                    Notice.class,
                    noticeMapper::selectNoticeByGame
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }
}
