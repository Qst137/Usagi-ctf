package top.linjhs.usagictfplatformbackend.service.impl;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.mapper.ProblemEnvMapper;
import top.linjhs.usagictfplatformbackend.mapper.ProblemMapper;
import top.linjhs.usagictfplatformbackend.pojo.MyContainer;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.ProblemEnvService;
import top.linjhs.usagictfplatformbackend.utils.DockerUtils;
import top.linjhs.usagictfplatformbackend.utils.RedisUtils;

import java.util.List;

@Slf4j
@Service
public class ProblemEnvServiceA implements ProblemEnvService {

    @Autowired
    private DockerUtils dockerUtils;
    @Autowired
    private ProblemEnvMapper problemEnvMapper;
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 创建镜像
     *
     * @param problem  传入要构建的题目
     * @param filePath dockerfile 路径
     * @return boolean
     * @author qst137
     */
    @Override
    public boolean buildImage(Problem problem, String filePath) {
        return dockerUtils.buildImage(problem.getProblemEnvi(),
                problem.getGameId() + "/" + problem.getProblemId()
        );
    }

    /**
     * 开启容器，返回端口
     *
     * @param problem 开启的题目
     * @param user    操作者
     * @return java.lang.Integer
     * @author qst137
     */
    @Override
    public Integer startContainer(Problem problem, User user) {
        Problem problemReceived =
                redisUtils.query(
                        RedisUtils.PROBLEM_KEY+problem.getGameId()+":"+problem.getProblemId(),
                        problem,
                        Problem.class,
                        problemMapper::selectProblem
                );
        return dockerUtils.createContainerAndStart(
                "PROB_" + problem.getGameId() + "_" + problem.getProblemId() + "_" + user.getUsername(),
                problem.getGameId() + "/" + problem.getProblemId(),
                dockerUtils.generateFlag(
                        user,
                        problemReceived
                )
        );
    }

    /**
     * 删除容器
     *
     * @param problem 删除的题目
     * @param user    操作者
     * @return Java.lang.Boolean
     * @author qst137
     */
    @Override
    public Boolean removeContainer(Problem problem, User user) {
        return dockerUtils.killContainerAndRemove(
                "PROB_" + problem.getGameId() + "_" + problem.getProblemId() + "_" + user.getUsername()
        );
    }

    /**
     * 检查题目环境
     *
     * @param problem 待检查的题目 Id
     * @return int 有镜像为 0 ,无镜像为 -1 ,非容器题为 1
     * @author qst137
     */
    public int testEnvironment(Problem problem) {
        problem.setTableName("usagi_games_"+problem.getGameId());
        log.info(problem.toString());
        problem.setProblemEnvi(problemEnvMapper.findDockerfile(problem));
        if(problem.getProblemEnvi().isEmpty()){
            return 1;
        }
        List<Image> imgs = dockerUtils.listImageWithPrefix("");
        imgs.removeIf(image -> image.getRepoTags().length > 0 && !image.getRepoTags()[0]
                .equals("local/" + problem.getGameId() + "/" + problem.getProblemId()+":latest"));
        if (imgs.isEmpty()) {
            return -1;
        }
        return 0;


    }

    /**
     * (重新) 构建题目环境
     *
     * @param problem 待构建的题目 Id
     * @return int
     * @author qst137
     */
    public int buildEnvironment(Problem problem) {
        try {
            if (testEnvironment(problem) == 1) {
                log.info("test1");
                return 1;
            }
            if (testEnvironment(problem) == 0) {
                log.info("test2");
                dockerUtils.removeImage(problem.getGameId() + "/" + problem.getProblemId());

            }
            log.info("test3");
            dockerUtils.buildImage(problem.getProblemEnvi(),
                    problem.getGameId() + "/" + problem.getProblemId());
            return 0;
        } catch (Exception e) {
            log.info(e.getMessage());
            return -1;
        }

    }

    /**
     * 检查当前题目环境
     *
     * @param problem 传入题目
     * @param user 传入用户
     * @return int
     * @author qst137
     */
    public MyContainer checkContainer(Problem problem, User user){
        List<Container> list=dockerUtils.listContainerWithSuffix(
                "/PROB_" + problem.getGameId() + "_" + problem.getProblemId() + "_" + user.getUsername()
        );
        if(list.isEmpty()){
            return null;
        }return MyContainer.fromContainer(list.get(0));

    }


}