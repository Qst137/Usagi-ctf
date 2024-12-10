package top.linjhs.usagictfplatformbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.mapper.ProblemManageMapper;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.service.ProblemManageService;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;
@Slf4j
@Service
public class ProblemManageServiceA implements ProblemManageService {
    @Autowired
    private ProblemManageMapper problemManageMapper;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 添加题目
     *
     * @param problem 携带题目信息
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean addProblem(Problem problem) {
        try {
            problemManageMapper.addProblem(problem);
            redisUtils.delete(RedisUtils.GAME_KEY+problem.getGameId()+":PROBLEMS");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除题目
     *
     * @param problem  携带题目 id
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean removeProblem(Problem problem){
        try{
            problemManageMapper.deleteProblem(problem);
            redisUtils.delete(RedisUtils.GAME_KEY+problem.getGameId()+":PROBLEMS");
            redisUtils.delete(RedisUtils.PROBLEM_KEY+problem.getGameId()+":"+problem.getProblemId());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 更改题目
     *
     * @param problem 携带题目信息
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean updateProblem(Problem problem){
        try{
            log.info(problem.toString());
            problemManageMapper.updateProblem(problem);
            redisUtils.delete(RedisUtils.GAME_KEY+problem.getGameId()+":PROBLEMS");
            redisUtils.delete(RedisUtils.PROBLEM_KEY+problem.getGameId()+":"+problem.getProblemId());
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
