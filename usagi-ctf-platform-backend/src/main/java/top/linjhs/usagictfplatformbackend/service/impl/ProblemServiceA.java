package top.linjhs.usagictfplatformbackend.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.mapper.ProblemMapper;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.service.ProblemService;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 题目 Service 实现类
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Service
public class ProblemServiceA implements ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 查询题目详细信息
     *
     * @param problem 传入比赛 ID、题目 ID、比赛表名
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author LinJHS
     */
    @Override
    public Map<String, Object> getProblemDetail(Problem problem) {
        problem.setTableName("usagi_games_" + problem.getGameId());

        Problem problemReceived = redisUtils.query(
                RedisUtils.GAME_KEY + problem.getGameId() + ":" + problem.getProblemId(),
                problem, Problem.class,
                problemMapper::getProblemDetail);
        if (null == problemReceived || !(problemReceived.getIsOpen()==1))
            return null;
        Map<String, Object> claims = new HashMap<>();
        claims.put("problemId", problemReceived.getProblemId());
        claims.put("problemName", problemReceived.getProblemName());
        claims.put("problemType", problemReceived.getProblemType());
        claims.put("problemIntro", problemReceived.getProblemIntro());
        claims.put("problemEnvi", problemReceived.getProblemEnvi());
        claims.put("problemAtta", problemReceived.getProblemAtta());
        claims.put("ortPts", problemReceived.getOriPts());
        claims.put("solved", problemReceived.getSolved());
        return claims;
    }
}
