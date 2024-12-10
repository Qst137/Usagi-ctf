package top.linjhs.usagictfplatformbackend.utils;


/**
 * 用于进行通用检查，比如用户名密码检查
 *
 * @author LinJHS
 * @version 1.0
 */
public class GeneralCheck {

    private static final String regexString = "^[a-zA-Z0-9]+$";
    private static final String teamString = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
    private static final String regexMd5 = "^[a-z0-9]+$";
    private static final String regexEmail = "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$";
    private static final String regexPhoneNumber = "^[1][3-9][0-9]{9}$";
    private static final String regexIdCard = "^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[Xx\\d]$";

    /**
     * 检查用户名是否符合要求
     *
     * @author LinJHS
     * @param username 用户名
     * @return boolean
     */
    public static boolean incorrectUsername(String username) {
        if (null == username || username.length()>20 || username.length()<3)
            return true;
        return !username.matches(regexString);
    }

    /**
     * 检查队名合法性
     *
     * @author qst137
     * @param teamName 队伍名
     * @return boolean
     */
    public static boolean incorrectTeamName(String teamName){
        if((null == teamName || teamName.length()>30 || teamName.length()<2))
            return true;
        return !teamName.matches(teamString);
    }

    /**
     * 检查密码是否符合要求
     *
     * @author LinJHS
     * @param password 密码
     * @return boolean
     */
    public static boolean incorrectPassword(String password) {
        if (null == password || password.length()!=64)
            return true;
        return !password.matches(regexMd5);
    }

    /**
     * 检查邮箱是否符合要求
     *
     * @author LinJHS
     * @param email 邮箱
     * @return boolean
     */
    public static boolean incorrectEmail(String email) {
        if (null == email || email.length()>32)
            return true;
        return !email.matches(regexEmail);
    }

    /**
     * 检查手机号是否符合要求
     *
     * @param phone 手机号
     * @return boolean
     * @author LinJHS
     */
    public static boolean incorrectPhoneNumber(String phone) {
        if (null == phone || phone.length()!=11)
            return true;
        return !phone.matches(regexPhoneNumber);
    }

    /**
     * 检查身份证号是否符合要求
     *
     * @param idCard 身份证号
     * @return boolean
     * @author LinJHS
     */
    public static boolean incorrectIdCard(String idCard) {
        if (null == idCard || idCard.length()!=18)
            return true;
        return !idCard.matches(regexIdCard);
    }
}
