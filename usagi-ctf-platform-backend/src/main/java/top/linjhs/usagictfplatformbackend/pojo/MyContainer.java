package top.linjhs.usagictfplatformbackend.pojo;

import com.github.dockerjava.api.model.Container;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类，封装了 Container 里我能用到的属性
 *
 * @author qst137
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyContainer {
    /**
     * 构造方法
     *
     * @param container 源 container
     * @return top.linjhs.usagictfplatformbackend.pojo.myContainer
     * @author qst137
     */
    public static MyContainer fromContainer(Container container) {
        Integer port;
        String name;
        long leftTime;
        if (container.getPorts().length == 0) {
            port = -1;
        } else {
            port = container.getPorts()[0].getPublicPort();
        }
        if (container.getNames().length == 0) {
            name = "NO_NAME_CONTAINER";
        } else {
            name = container.getNames()[0];
        }
        if(container.getNames().length !=0&&container.getNames()[0].startsWith("/PROB_")){
            leftTime = 7200-System.currentTimeMillis()/1000+container.getCreated();
        }else{
            leftTime = 10000L;
        }
        return new MyContainer(
                container.getId(),
                name,
                leftTime,
                container.getImage(),
                port
        );

    }

    String id;
    String name;
    Long leftTime;
    String image;
    Integer port;
}
