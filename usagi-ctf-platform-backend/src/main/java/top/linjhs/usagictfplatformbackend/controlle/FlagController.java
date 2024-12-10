package top.linjhs.usagictfplatformbackend.controlle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.linjhs.usagictfplatformbackend.pojo.Problem;
import top.linjhs.usagictfplatformbackend.pojo.ProblemAndUser;
import top.linjhs.usagictfplatformbackend.pojo.Result;
import top.linjhs.usagictfplatformbackend.pojo.User;
import top.linjhs.usagictfplatformbackend.service.FlagService;

/**
 * flag 有关 controller
 *
 * @author qst137
 * @version 1.0
 */
@RestController
public class FlagController {
    @Autowired
    private FlagService flagService;
    /**
     * 提交 flag
     *
     * @param problemAndUser  携带用户，题目与 flag
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author qst137
     */
    @PostMapping("ctf_api/flag")
    public Result checkFlag(@RequestBody ProblemAndUser problemAndUser){
        Problem problem = problemAndUser.getProblem();
        User user = problemAndUser.getUser();
        try {
            if (flagService.checkFlag(user, problem)) {

                return new Result(200, "Flag 正确", null);
            }
            return new Result(200, "Flag 错误", null);
        }catch (Exception e){
            return Result.fail();
        }

    }
}
