package top.linjhs.usagictfplatformbackend.service;

import top.linjhs.usagictfplatformbackend.pojo.MyContainer;

import java.util.List;

/**
 * 容器管理 Service
 *
 * @author qst137
 * @version 1.0
 */
public interface DockerManageService
{
    /**
     * 查看全部容器
     *
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    List<MyContainer> listAll();

    /**
     * 查看指定 game 的容器
     *
     * @param gameId 指定 game
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    List<MyContainer> listByGame(Integer gameId);

    /**
     * 查看指定 problem 的容器
     *
     * @param gameId    指定 game
     * @param problemId 指定 problem
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    List<MyContainer> listByProblem(Integer gameId, Integer problemId);

    /**
     * 查看指定 user 的容器
     *
     * @param username 指定 user
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    List<MyContainer> listByUsername(String username);


}
