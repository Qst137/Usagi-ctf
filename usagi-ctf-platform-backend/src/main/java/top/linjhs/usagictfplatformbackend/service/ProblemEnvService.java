package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.MyContainer;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;
/**
 * 题目环境 Service
 *
 * @author qst137
 * @version 1.0
 */
public interface ProblemEnvService {
    /**
     * 创建镜像
     *
     * @param problem  传入要构建的题目
     * @param filePath dockerfile 路径
     * @return boolean
     * @author qst137
     */
    boolean buildImage(Problem problem, String filePath);

    /**
     * 开启容器，返回端口
     *
     * @param problem 开启的题目
     * @param user    操作者
     * @return java.lang.Integer
     * @author qst137
     */
    Integer startContainer(Problem problem, User user);

    /**
     * 删除容器
     *
     * @param problem 删除的题目
     * @param user    操作者
     * @return Java.lang.Boolean
     * @author qst137
     */
    Boolean removeContainer(Problem problem, User user);

    /**
     * 检查题目环境
     *
     * @param problem 待检查的题目 Id
     * @return int 有镜像为 0 ,无镜像为 -1 ,非容器题为 1
     * @author qst137
     */
    int testEnvironment(Problem problem);

    /**
     * (重新) 构建题目环境
     *
     * @param problem 待构建的题目 Id
     * @return int
     * @author qst137
     */
    int buildEnvironment(Problem problem);

    /**
     * 检查当前题目环境
     *
     * @param problem 传入题目
     * @param user 传入用户
     * @return int
     * @author qst137
     */
    MyContainer checkContainer(Problem problem, User user);

}
