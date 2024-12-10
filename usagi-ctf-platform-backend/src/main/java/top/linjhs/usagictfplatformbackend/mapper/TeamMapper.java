package top.linjhs.usagictfplatformbackend.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.linjhs.usagictfplatformbackend.pojo.Team;

/**
 * 团队Mapper
 *
 * @author qst137
 * @version 1.0
 */
@Mapper
public interface TeamMapper {
    /**
     * 创建队伍 ,XML 映射，会给队伍添上 id
     *
     * @param team 携带队伍信息
     * @author qst137
     */
    void createTeam(Team team);

    /**
     * 根据id查找队伍
     *
     * @param team 携带队伍 id
     * @return Team
     * @author qst137
     */
    @Select("SELECT * FROM usagi_teams WHERE id=#{id}")
    Team selectTeamById(Team team);

    /**
     * 更新队伍信息，XML映射
     *
     * @param team 携带队伍信息
     * @author qst137
     */
    void updateTeam(Team team);

    /**
     * 删除队伍信息
     *
     * @param team 携带队伍 id
     * @author qst137
     */
    @Delete("DELETE from usagi_teams where id=#{id}")
    void deleteTeam(Team team);
}
