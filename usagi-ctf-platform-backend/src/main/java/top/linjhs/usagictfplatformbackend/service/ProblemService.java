package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.Problem;

import java.util.Map;

/**
 * 题目 Service
 *
 * @author LinJHS
 * @version 1.0
 */
public interface ProblemService {

    /**
     * 查询题目详细信息
     *
     * @param problem 传入比赛 ID、题目 ID、比赛表名
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author LinJHS
     */
    Map<String, Object> getProblemDetail(Problem problem);
}
