package top.linjhs.usagictfplatformbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.linjhs.usagictfplatformbackend.utils.RankUtils;

/**
 * 定时任务配置类
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Component
public class TaskConfig {

    @Autowired
    private RankUtils rankUtils;

    @Scheduled(cron = "0 0/1 * * * *") // 每隔五分钟进行一次更新
    public void updateRank() {
        rankUtils.updateGameRankAll();
//        log.info("update");
    }
}
