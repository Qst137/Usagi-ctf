package top.linjhs.usagictfplatformbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.linjhs.usagictfplatformbackend.pojo.MyContainer;
import top.linjhs.usagictfplatformbackend.utils.DockerUtils;

/**
 * 容器定时关闭类
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@Component
public class ContainerTaskConfig {

    @Autowired
    private DockerUtils dockerUtils;

    /**
     * 监测超过两小时的容器
     *
     * @author qst137
     */
    @Scheduled(cron = "0 0/1 * * * *") // 每隔一分钟进行一次更新
    public void checkContainers() {
        dockerUtils.listContainerWithPrefix("")
                .forEach(con->{
                     MyContainer m = MyContainer.fromContainer(con);
                     if(m.getLeftTime()<65){
                         dockerUtils.killContainerAndRemove(m.getName().substring(1));
                     }
                });
    }
}
