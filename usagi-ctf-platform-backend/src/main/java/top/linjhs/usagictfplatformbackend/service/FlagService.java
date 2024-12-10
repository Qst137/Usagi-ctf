package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;

/**
 * Flag 提交有关 Service
 *
 * @author qst137
 * @version 1.0
 */
public interface FlagService {
    /**
     * 提交 flag
     *
     * @param user 携带用户名
     * @param problem 携带 problemId，gameId 和 flag
     * @return int 1 为错误， 0 为正确
     * @author qst137
     */
    boolean checkFlag(User user, Problem problem);

}
