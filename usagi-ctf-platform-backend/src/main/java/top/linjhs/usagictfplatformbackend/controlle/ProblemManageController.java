package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.ProblemAndUser;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.service.ProblemManageService;

/**
 * 题目管理 Controller
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@RestController
public class ProblemManageController {
    @Autowired
    private ProblemManageService problemManageService;

    /**
     * 添加题目
     *
     * @param problemAndUser 携带题目信息和 Username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("/ctf_api/prob")
    public Result addProb(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        if (!problemAndUser.getUser().getUsername().equals("admin")) {
            return Result.fail();
        }
        if (problemManageService.addProblem(problem))
            return Result.success();
        return Result.fail();
    }

    /**
     * 删除题目
     *
     * @param problemAndUser 携带题目信息和 Username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @DeleteMapping("/ctf_api/prob")
    public Result deleteProb(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        if (!problemAndUser.getUser().getUsername().equals("admin")) {
            return Result.fail();
        }
        if (problemManageService.removeProblem(problem))
            return Result.success();
        return Result.fail();
    }

    /**
     * 修改题目
     *
     * @param problemAndUser 携带新增题目信息和 Username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PutMapping("/ctf_api/prob")
    public Result updateProb(@RequestBody ProblemAndUser problemAndUser) {
        Problem problem = problemAndUser.getProblem();
        log.info(problem.toString());
        if (!problemAndUser.getUser().getUsername().equals("admin")) {
            return Result.fail();
        }
        if (problemManageService.updateProblem(problem))
            return Result.success();
        return Result.fail();
    }
}
