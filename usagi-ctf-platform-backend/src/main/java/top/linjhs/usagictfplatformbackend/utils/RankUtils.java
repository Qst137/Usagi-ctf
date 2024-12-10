package top.linjhs.usagictfplatformbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.linjhs.usagictfplatformbackend.mapper.ProblemMapper;
import top.linjhs.usagictfplatformbackend.mapper.TeamMapper;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于处理排行榜相关信息
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Component
public class RankUtils {

    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ParseUtils parseUtils;
    private static final double log2 = Math.log(2);

    public static List<Integer> gameList = new ArrayList<>();

    /**
     * 添加比赛
     *
     * @param gameId 比赛ID
     * @author LinJHS
     */
    public void addGame(int gameId) {
        // 开启比赛缓存
        redisUtils.openGamePermission(gameId);

        if (!gameList.contains(gameId)) { // 比赛不存在
            gameList.add(gameId);
        }
        updateGameRank(gameId);
    }


    /**
     * 通过比赛 ID 更新比赛排行榜
     *
     * @param gameId 比赛ID
     * @author LinJHS
     */
    private void updateGameRank(int gameId) {
        // teamId : [
        //     0: total,
        //     problemId: score,
        //     problemId: score,
        // ]
//        HashMap<Integer, List<HashMap<Integer, Integer>>> teamScore = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Object>> teamScore = new HashMap<>();
        List<Problem> problemList = problemMapper.getAllProblems("usagi_games_" + gameId);
        for (Problem problem : problemList) {
            String solvedTeam = problem.getSolved();
            int oriPts = problem.getOriPts();
            List<String> solvedTeamList = parseUtils.stringToList(solvedTeam);
            int solvedTeamNumber = solvedTeamList.size();
            // 分值公式
            // f = oriPts/20 + (oriPts - oriPts/20) / (log2(solvedTeamNumber+3)-1)
            int pts = (int) ((double) oriPts / 20 +
                    (oriPts - (double) oriPts / 20) / (Math.log(solvedTeamNumber + 3) / log2 - 1));
            // 将每个团队加上指定积分
            for (String teamId : solvedTeamList) {
                if (teamScore.containsKey(Integer.parseInt(teamId))) {
//                    teamScore.put(Integer.parseInt(teamId),
//                            new ArrayList<HashMap<Integer, Integer>>() {{
//                                add(new HashMap<Integer, Integer>() {{
//                                    put(0, pts);
//                                }});
//                                add(new HashMap<Integer, Integer>() {{
//                                    put(problem.getProblemId(), pts);
//                                }});
//                            }});
                    // 新建该团队的得分列表
//                    teamScore.put(Integer.parseInt(teamId),
//                            new HashMap<>() {{
//                                put(0, pts);
//                                put(problem.getProblemId(), pts);
//                            }});
                    HashMap<Integer, Object> problemScoreList = teamScore.get(Integer.parseInt(teamId));
                    problemScoreList.put(problem.getProblemId(), pts);
                    teamScore.put(Integer.parseInt(teamId), problemScoreList);
                } else {
                    Team tmpTeam = teamMapper.selectTeamById(new Team() {{
                        setId(Integer.parseInt(teamId));
                    }});
                    if (tmpTeam == null) { // 不存在该队伍
                        continue;
                    }
                    String teamName = tmpTeam.getTeamName();
//                    // 将 0 加上 pts，problemId 加上 pts
                    HashMap<Integer, Object> problemScoreList = new HashMap<>();
                    problemScoreList.put(0, teamName); // 记录队伍名
//                    problemScoreList.put(-1, teamName); // 记录队伍名
//                    problemScoreList.put(0, (Integer)problemScoreList.get(0) + pts); // 总分加上本题得分
                    problemScoreList.put(problem.getProblemId(), pts); // 记录单题得分
                    teamScore.put(Integer.parseInt(teamId), problemScoreList);
                }
            }
        }
        log.info(teamScore.toString());
        redisUtils.set(RedisUtils.RANK_KEY + gameId, teamScore);
    }

    /**
     * 更新所有比赛排行榜
     *
     * @author LinJHS
     */
    public void updateGameRankAll() {
        for (int gameId : gameList) {
            updateGameRank(gameId);
        }
    }

    /**
     * 关闭比赛
     *
     * @param gameId 比赛ID
     * @author LinJHS
     */
    public void closeGame(int gameId) {
        // 关闭比赛缓存
        redisUtils.closeGamePermission(gameId);
        gameList.remove(Integer.valueOf(gameId));
    }
}
