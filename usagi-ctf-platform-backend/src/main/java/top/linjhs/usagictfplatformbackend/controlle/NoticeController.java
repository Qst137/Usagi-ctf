package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.linjhs.usagictfplatformbackend.pojo.Notice;
import top.linjhs.usagictfplatformbackend.pojo.NoticeAndUser;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.NoticeService;
import top.linjhs.usagictfplatformbackend.service.UserService;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

/**
 * 通知 Controller
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 发布通知
     *
     * @param noticeAndUser 携带通知信息和请求人身份
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/notice")
    public Result createNotice(@RequestBody NoticeAndUser noticeAndUser) {
        Notice notice = noticeAndUser.getNotice();
        User user = noticeAndUser.getUser();
        if (!user.getUsername().equals("admin")) {
            return Result.fail();
        }
            if (noticeService.newNotice(notice)) {
                return Result.success();
            }
            return Result.fail();
    }

    /**
     * 删除通知
     *
     * @param noticeAndUser 携带通知 id 和请求人身份
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @DeleteMapping("ctf_api/notice")
    public Result deleteNotice(@RequestBody NoticeAndUser noticeAndUser) {
        Notice notice = noticeAndUser.getNotice();
        User user = noticeAndUser.getUser();
        if (!user.getUsername().equals("admin")) {
            return Result.fail();
        }
        try {
            if (noticeService.deleteNotice(notice)) {
                return Result.success();
            }
            return Result.fail();
        } catch (Exception e) {
            return Result.fail();
        }
    }

    /**
     * 拉取某一比赛的所有通知
     *
     * @param noticeAndUser 携带比赛 id 和请求人身份
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PutMapping ("ctf_api/notice")
    public Result pullNotices(@RequestBody NoticeAndUser noticeAndUser) {
        Notice notice = noticeAndUser.getNotice();
        User user = noticeAndUser.getUser();
        if (!redisUtils.checkUserGamePermission(
                user.getUsername(),
                notice.getGameId(),
                userService::checkGamePermission)
                &&
                !user.getUsername().equals("admin")
        ) {
            return Result.fail();
        }
        return Result.success(noticeService.getNoticeByGame(notice));
    }
}
