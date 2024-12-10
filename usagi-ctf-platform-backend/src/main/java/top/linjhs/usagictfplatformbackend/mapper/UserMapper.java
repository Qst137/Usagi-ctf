package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.linjhs.usagictfplatformbackend.pojo.User;

/**
 * 用户 Mapper
 *
 * @author LinJHS
 * @version 1.0
 */
@Mapper
public interface UserMapper {

    /**
     * 注册用户
     *
     * @param user 存储用户名和密码
     * @author LinJHS
     */
    void register(User user);

    /**
     * 通过用户名查询用户信息
     *
     * @param user 储存用户名
     * @return top.linjhs.usagictfplatformbackend.pojo.User
     * @author LinJHS
     */
    @Select("SELECT * FROM usagi_users WHERE username = #{username}")
    User getUserByName(User user);

    /**
     * 更新用户信息，使用 XML 文件映射
     *
     * @param user 储存用户信息
     * @author LinJHS
     */
    void updateUser(User user);
}
