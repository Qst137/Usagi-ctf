package top.linjhs.usagictfplatformbackend.controlle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.linjhs.usagictfplatformbackend.pojo.*;
import top.linjhs.usagictfplatformbackend.service.UserService;
import top.linjhs.usagictfplatformbackend.utils.*;

import java.util.*;

/**
 * 用户 Controller
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口，并返回 cookie
     *
     * @param loginUser 接收验证码以及用户名、密码
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    @PostMapping("/ctf_api/login")
    public Result login(@RequestBody LoginUser loginUser) {
        try {
            if (loginUser.getCaptchaVerifyParam() == null || loginUser.getCaptchaVerifyParam().isEmpty())
                return Result.fail("No Captcha param detected.");
            if (!CaptchaUtils.checkCaptcha(loginUser.getCaptchaVerifyParam()))
                return Result.fail("You are a robot?");
        } catch (Exception ignoredE) {
            return Result.fail("You are a robot? [ERR: 001]");
        }
        User user = loginUser.getUser();
        if (GeneralCheck.incorrectUsername(user.getUsername()) ||
                GeneralCheck.incorrectPassword(user.getPassword()))
            return Result.fail("Data formatting error!");
        User checkUser = userService.login(user);
        if (null != checkUser) { // 登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", checkUser.getId());
            claims.put("username", checkUser.getUsername());
            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        } else { // 登录失败
            return Result.fail("Username or password error!");
        }
    }

    /**
     * 用户注册接口
     *
     * @param loginUser 接收验证码以及用户名、密码、邮箱等等
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    @PutMapping("/ctf_api/login")
    public Result register(@RequestBody LoginUser loginUser) {
        try {
            if (loginUser.getCaptchaVerifyParam() == null || loginUser.getCaptchaVerifyParam().isEmpty())
                return Result.fail("No Captcha param detected.");
            if (!CaptchaUtils.checkCaptcha(loginUser.getCaptchaVerifyParam()))
                return Result.fail("You are a robot?");
        } catch (Exception ignoredE) {
            return Result.fail("You are a robot? [ERR: 002]");
        }
        User user = loginUser.getUser();
        if (GeneralCheck.incorrectUsername(user.getUsername()) ||
                GeneralCheck.incorrectPassword(user.getPassword()) ||
                user.getName() == null || user.getName().isEmpty() ||
                GeneralCheck.incorrectIdCard(user.getIdCard()) ||
                (!(user.getEmail() == null || user.getEmail().isEmpty()) && GeneralCheck.incorrectEmail(user.getEmail())) ||
                (!(user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) && GeneralCheck.incorrectPhoneNumber(user.getPhoneNumber())))
            return Result.fail("Data formatting error!");
        // 检查实名信息
        try {
            if (!IdCardUtils.checkIdCard(user.getIdCard(), user.getName()))
                return Result.fail("The ID number doesn't match the name!");
        } catch (Exception ignoredE) {
            return Result.fail("The ID number doesn't match the name! [ERR: 03]");
        }

        if (userService.register(user)) { // 注册成功
            return Result.success();
        } else { // 注册失败
            return Result.fail("This user already exists!");
        }
    }

    /**
     * 查询用户信息
     *
     * @param user 接收用户 ID
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    @PostMapping("/ctf_api/users")
    public Result getUser(@RequestBody User user) {
        Map<String, Object> userReceived;
        if (!(user.getUsername() == null || user.getUsername().isEmpty())) { // 获取自己的用户信息（ID、username、email、userIntro）
            userReceived = userService.getUserSelf(user);
        } else if (!(user.getOtherUsername() == null || user.getOtherUsername().isEmpty())) { // 获取别人的用户信息（ID、username、userIntro）
            user.setUsername(user.getOtherUsername());
            userReceived = userService.getUserOther(user);
        } else {
            return Result.fail();
        }
        if (null == userReceived) // 查找不到用户
            return Result.success(); // 成功，返回空
        else
            return Result.success(userReceived);
    }

    /**
     * 更新用户信息
     *
     * @param user 接收更新的用户信息
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    @PutMapping("/ctf_api/users")
    public Result updateUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty())
            return Result.fail(); // 失败
        if (userService.updateUser(user))
            return Result.success();
        else
            return Result.fail();
    }

//    /**
//     * 查询用户所在团队
//     *
//     * @param user 接收用户名
//     * @return top.linjhs.usagictfplatformbackend.pojo.Result
//     * @author LinJHS
//     */
//    @PostMapping("/ctf_api/users/teams")
//    public Result getTeams(@RequestBody User user) {
//        if (null == user.getUsername() || user.getUsername().isEmpty())
//            return Result.fail();
//        String teamName = userService.getTeams(user);
//        if (teamName == null)
//            return Result.fail();
//        return Result.success(teamName);
//    }

    /**
     * 获取排行榜
     *
     * @param gamePermission 传入 gameId 和 username
     * @return top.linjhs.usagictfplatformbackend.pojo.Result
     * @author LinJHS
     */
    @PostMapping("/ctf_api/ranks")
    public Result getRank(@RequestBody GamePermission gamePermission) {
        if (gamePermission.getUsername() == null || gamePermission.getUsername().isEmpty())
            return Result.fail("Param incorrect!");
        if (gamePermission.getGameId() < 1)
            return Result.fail("Param incorrect!");
        if (!userService.checkGamePermission(gamePermission))
            return Result.fail("You do not have permission!");
        return Result.success(userService.getRank(gamePermission.getGameId()));
    }
}
