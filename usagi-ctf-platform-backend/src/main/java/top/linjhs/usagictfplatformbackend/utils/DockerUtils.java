package top.linjhs.usagictfplatformbackend.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.User;

import java.io.File;
import java.time.Duration;
import java.util.List;

/**
 * 用于操作 docker
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@Component
public class DockerUtils {

    private static DockerClient dockerClient;
    private static final BuildImageResultCallback callback = new BuildImageResultCallback();
    @Value("${flag-seed}")
    private String adminSalt;

    /**
     * 构造方法，初始化 dockerClient
     *
     * @author qst137
     */
    public DockerUtils() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    /**
     * 尝试连接 DockerClient
     *
     * @return boolean
     * @author qst137
     */
    public boolean testDockerClient() {
        try {
            log.info("Docker client connected. => " + dockerClient.listContainersCmd().exec());
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    /**
     * 用 dockerfile 创建镜像
     *
     * @param position  dockerfile 位置
     * @param imageName 镜像名
     * @return boolean
     * @author qst137
     */
    public boolean buildImage(String position, String imageName) {
        try {
            File dockerfile = new File(position);
            dockerClient.buildImageCmd(dockerfile).withTag("local/" + imageName).exec(callback).awaitImageId();
            log.info("Image built. => " + dockerClient.listImagesCmd().exec().get(0));
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    /**
     * 删除镜像
     *
     * @param imageName 镜像名
     * @return boolean
     * @author qst137
     */
    public boolean removeImage(String imageName) {
        try {
            dockerClient.removeImageCmd("local/" + imageName).exec();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建容器并启动，将 flag 传入环境变量，返回 80 端口的映射端口
     *
     * @param imageName     要使用的镜像
     * @param containerName 容器名
     * @param flag          flag 内容
     * @return java.lang.Integer
     * @author qst137
     */
    public Integer createContainerAndStart(String containerName, String imageName, String flag) {
        try {
            dockerClient.createContainerCmd("").withName(containerName).withEnv("USAGI_FLAG=" + flag)
                    .withPortBindings(PortBinding.parse("80:")).withImage("local/" + imageName).exec();
            log.info(
                    "Container created. => " + dockerClient.listContainersCmd().withShowAll(true).exec().get(0)
            );
            dockerClient.startContainerCmd(containerName).exec();
            try {
                Thread.sleep(1000);
            } catch (Exception ignoredE) {
            }
            Container container = dockerClient.listContainersCmd().withShowAll(true).exec().get(0);
            log.info("Container started. => " + container);
            return container.getPorts()[0].getPublicPort();
        } catch (Exception e) {
            log.info(e.getMessage());
            return -1;
        }
    }

    /**
     * 关闭容器并删除
     *
     * @param containerName 容器名
     * @return java.lang.Integer
     * @author qst137
     */
    public boolean killContainerAndRemove(String containerName) {
        try {
            dockerClient.killContainerCmd(containerName).exec();
            log.info("Container killed. => " + containerName);
        } catch (Exception e) {
            log.info("Container do not running. =>" + containerName);
        }
        try {
            dockerClient.removeContainerCmd(containerName).exec();
            log.info("Container removed. => " + containerName);
            return true;
        } catch (Exception e) {
            log.info("Container do not exist. =>" + containerName);
            return false;
        }
    }

    /**
     * flag 预处理，加动态部分
     *
     * @param user    用于特征生成 flag
     * @param problem 携带flag ，并用于特征生成 flag
     * @return java.lang.String
     * @author qst137
     */
    public String generateFlag(User user, Problem problem) {
        String flagSuffix = DigestUtils.md5DigestAsHex((adminSalt + user.getUsername() + "#" + problem.getGameId() + "#"
                + problem.getProblemId()).getBytes()).substring(0, 16);
        return problem.getFlag().replace("{}", flagSuffix);
    }

    /**
     * 返回指定前缀的容器列表
     *
     * @param prefix 指定的前缀
     * @return java.utils<com.github.dockerjava.api.model.Container>
     * @author qst137
     */
    public List<Container> listContainerWithPrefix(String prefix) {
        List<Container> list = dockerClient.listContainersCmd().withShowAll(true).exec();
        list.removeIf(c -> c.getNames().length == 0 || !c.getNames()[0].startsWith(prefix));
        return list;

    }

    /**
     * 返回指定后缀的容器列表
     *
     * @param suffix 指定的后缀
     * @return java.utils<com.github.dockerjava.api.model.Container>
     * @author qst137
     */
    public List<Container> listContainerWithSuffix(String suffix) {
        try {
            List<Container> list = dockerClient.listContainersCmd().withShowAll(true).exec();
            list.removeIf(c -> c.getNames().length == 0 || !c.getNames()[0].endsWith(suffix));
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回指定前缀的镜像列表
     *
     * @param prefix 指定的前缀
     * @return List<Image>
     * @author qst137
     */
    public List<Image> listImageWithPrefix(String prefix) {
        try {
            List<Image> list = dockerClient.listImagesCmd().exec();
            list.removeIf(c -> !(c.getRepoTags().length==0)&&!c.getRepoTags()[0].startsWith(prefix));
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回当前用户开启的容器数量
     *
     * @param username 用户名
     * @return int
     * @author qst137
     */
    public int containerNumber(String username){
        try {
            List<Image> list = dockerClient.listImagesCmd().exec();
            list.removeIf(
                    c->c.getRepoTags()==null||c.getRepoTags().length==0||!c.getRepoTags()[0].endsWith("_"+username)
            );
            return list.size();
        } catch (Exception e) {
            return -1;
        }

    }


}
