package top.linjhs.usagictfplatformbackend.service.impl;

import com.github.dockerjava.api.model.Container;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.linjhs.usagictfplatformbackend.pojo.MyContainer;
import top.linjhs.usagictfplatformbackend.service.DockerManageService;
import top.linjhs.usagictfplatformbackend.utils.DockerUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DockerManageServiceA implements DockerManageService {

    @Autowired
    private DockerUtils dockerUtils;

    /**
     * 查看全部容器
     *
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    @Override
    public List<MyContainer> listAll() {
        return dockerUtils.listContainerWithPrefix("/PROB_")
                .stream().toList().stream().map(MyContainer::fromContainer).toList();
    }

    /**
     * 查看指定 game 的容器
     *
     * @param gameId 指定 game
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    @Override
    public List<MyContainer> listByGame(Integer gameId) {
        return dockerUtils.listContainerWithPrefix("/PROB_" + gameId)
                .stream()
                .map(MyContainer::fromContainer)
                .collect(Collectors.toList()
                );
    }

    /**
     * 查看指定 problem 的容器
     *
     * @param gameId    指定 game
     * @param problemId 指定 problem
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    @Override
    public List<MyContainer> listByProblem(Integer gameId, Integer problemId) {
        return dockerUtils.listContainerWithPrefix("/PROB_" + gameId + "_" + problemId)
                .stream()
                .map(MyContainer::fromContainer)
                .collect(Collectors.toList()
                );
    }

    /**
     * 查看指定 user 的容器
     *
     * @param username 指定 user
     * @return java.util.List<top.linjhs.usagictfplatformbackend.pojo.MyContainer>
     * @author qst137
     */
    @Override
    public List<MyContainer> listByUsername(String username) {
        List<Container> list = dockerUtils.listContainerWithSuffix(username);
        list.removeIf(c -> !c.getNames()[0].startsWith("/PROB_"));
        return list.stream()
                .map(MyContainer::fromContainer)
                .collect(Collectors.toList()
                );
    }
}
