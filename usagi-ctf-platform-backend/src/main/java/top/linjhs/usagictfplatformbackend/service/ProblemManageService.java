package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.Problem;

/**
 * 题目管理 Service
 *
 * @author qst137
 * @version 1.0
 */
public interface ProblemManageService {

    /**
     * 添加题目
     *
     * @param problem 携带题目信息
     * @return boolean
     * @author qst137
     */
    boolean addProblem(Problem problem);

    /**
     * 删除题目
     *
     * @param problem  携带题目 id
     * @return boolean
     * @author qst137
     */
    boolean removeProblem(Problem problem);

    /**
     * 更改题目
     *
     * @param problem 携带题目信息
     * @return boolean
     * @author qst137
     */
    boolean updateProblem(Problem problem);
}
