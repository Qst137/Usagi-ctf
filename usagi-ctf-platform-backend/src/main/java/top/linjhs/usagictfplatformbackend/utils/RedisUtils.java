package top.linjhs.usagictfplatformbackend.utils;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.linjhs.usagictfplatformbackend.pojo.GamePermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用于操作 Redis 数据库
 *
 * @author LinJHS
 * @version 1.0
 */
@Slf4j
@Component
public class RedisUtils {

    protected final StringRedisTemplate stringRedisTemplate;
    private static final Long CACHE_TTL = 25L;
    protected static final Long CACHE_NULL_TTL = 5L;
    private static final Random random = new Random(6666);
    private static final String LOCK_KEY = "lock:";

    // 通用前缀
    public static final String USER_KEY = "cache:user:";
    public static final String GAME_KEY = "cache:game:";
    public static final String TEAM_KEY = "cache:team:";
    public static final String NOTICE_KEY = "cache:notice:";
    public static final String RANK_KEY = "cache:rank:";
    public static final String PERMISSION_KEY = "permission:";
    public static final String PROBLEM_KEY = "cache:problem:";

    /**
     * 构造函数
     *
     * @param stringRedisTemplate 传入该资源
     * @author LinJHS
     */
    public RedisUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成随机过期时间防止缓存雪崩
     *
     * @return java.lang.Long
     * @author LinJHS
     */
    private Long timeout() {
        return CACHE_TTL + random.nextLong(10);
    }

    /**
     * 往 Redis 中写入键值对
     *
     * @param key   需要设置的 key
     * @param value 需要设置的 value
     * @author LinJHS
     */
    public void set(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), timeout(), TimeUnit.MINUTES);
    }

    /**
     * 在 Redis 中读取指定键值对
     *
     * @param key 需要查询的 key
     * @author LinJHS
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 查询指定数据
     *
     * @param key        查询 key
     * @param id         查询 ID
     * @param type       返回数据类型
     * @param dbFallback 缓存没有数据时，需要执行从数据库获取数据的方法（返回类型为 List<type> ）
     * @return R
     * @author LinJHS
     */
    public <R, ID> R query(String key, ID id, Class<R> type, Function<ID, R> dbFallback) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(json)) {
            return JSON.parseObject(json, type);
        }
        if (null != json) { // 返回的是空值
            return null;
        }
        R r = dbFallback.apply(id);
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        this.set(key, r);
        return r;
    }

    /**
     * 无参查询指定数据，返回列表
     *
     * @param key        查询 key
     * @param type       返回数据类型
     * @param dbFallback 缓存没有数据时，需要执行从数据库获取数据的方法
     * @return List<R>
     * @author qst137
     */
    public <R> List<R> queryList(String key, Class<R> type, Supplier<List<R>> dbFallback) {
        String json = this.stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(json)) {
            return JSON.parseArray(json, type);
        }
        if (null != json) { // 返回的是空值
            return new ArrayList<>();
        }
        List<R> r = dbFallback.get();
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return new ArrayList<>();
        }
        this.set(key, r);
        return r;
    }

    /**
     * 有参查询指定数据，返回列表
     *
     * @param key        查询 key
     * @param id         查询 id
     * @param type       返回数据类型
     * @param dbFallback 缓存没有数据时，需要执行从数据库获取数据的方法
     * @return List<R>
     * @author qst137
     */
    public <R, ID> List<R> queryList(String key, ID id, Class<R> type, Function<ID, List<R>> dbFallback) {
        String json = this.stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(json)) {
            return JSON.parseArray(json, type);
        }
        if (null != json) { // 返回的是空值
            return new ArrayList<>();
        }
        List<R> r = dbFallback.apply(id);
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return new ArrayList<>();
        }
        this.set(key, r);
        return r;
    }

    /**
     * 查询指定数据
     *
     * @param key        查询 key
     * @param id         查询 ID
     * @param type       返回数据类型
     * @param dbFallback 缓存没有数据时，需要执行从数据库获取数据的方法
     * @return R
     * @author LinJHS
     */
    public <R, ID> R queryWithMutex(String key, ID id, Class<R> type, Function<ID, R> dbFallback) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(json)) {
            return JSON.parseObject(json, type);
        }
        if (null != json) { // 返回的是""，防止缓存穿透
            return null;
        }
        String lockKey = LOCK_KEY + key;
        R r;
        try {
            boolean isLock = tryLock(lockKey);
            if (!isLock) { // 被其他的上锁了
                while (true) {
                    Thread.sleep(50);
                    R checkR = getData(key, type); // 每隔 50ms 获取一次数据
                    if (checkR != null) { // 成功获取到数据
                        return checkR;
                    }
                    if (checkLock(lockKey)) { // 发现锁消失了（却没有数据）就退出
                        return null;
                    }
                }
            }
            // 获取锁成功，需要 double check
            R checkR = getData(key, type);
            if (checkR != null) { // 成功获取到数据
                return checkR;
            }

            r = dbFallback.apply(id);
            if (r == null) { // 防止缓存穿透
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            this.set(key, r);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(lockKey);
        }
        return r;
    }

    /**
     * 获得数据
     *
     * @param key  数据的key
     * @param type 返回数据类型
     * @return R
     * @author LinJHS
     */
    private <R> R getData(String key, Class<R> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(json)) {
            return JSON.parseObject(json, type);
        }
        return null;
    }

    /**
     * 获取互斥锁
     *
     * @param key 互斥锁的 key
     * @return boolean
     * @author LinJHS
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10L, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(flag);
    }

    /**
     * 检查互斥锁
     *
     * @param key 互斥锁的 key
     * @return boolean
     * @author LinJHS
     */
    private boolean checkLock(String key) {
        Boolean flag = stringRedisTemplate.hasKey(key);
        return Boolean.TRUE.equals(flag);
    }

    /**
     * 删除互斥锁
     *
     * @param key 互斥锁的 key
     * @author LinJHS
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除 key
     *
     * @param key 需要删除的 key
     * @author LinJHS
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 检查用户是否拥有查看比赛的权限
     *
     * @param username   用户名
     * @param gameId     比赛 ID
     * @param dbFallback 默认传入 userService::checkGamePermission
     * @return boolean
     * @author LinJHS
     */
    public boolean checkUserGamePermission(String username, int gameId, Function<GamePermission, Boolean> dbFallback) {
        String gameToken = stringRedisTemplate.opsForValue().get(PERMISSION_KEY + gameId);
        if (!StringUtils.hasLength(gameToken)) { // 比赛不存在 or 未开始
            return false;
        }
        String checkPermission = stringRedisTemplate.opsForValue().get(PERMISSION_KEY + gameToken + username);
        if (StringUtils.hasLength(checkPermission)) { // 找到该用户，有权限
            return true;
        }
        if (checkPermission != null) { // 找到该用户，无权限（防止缓存穿透）
            return false;
        }
        GamePermission gamePermission = new GamePermission();
        gamePermission.setGameId(gameId);
        gamePermission.setUsername(username);
        // 未找到该用户，进行权限查询
        if (Boolean.TRUE.equals(dbFallback.apply(gamePermission))) { // 有权限
            set(PERMISSION_KEY + gameToken + ":" + username, "1");
            return true;
        } else { // 无权限
            set(PERMISSION_KEY + gameToken + ":" + username, "");
            return false;
        }
    }

    /**
     * 开启比赛
     * 注意！通过 RankUtils 调用，不要手动调用
     *
     * @param gameId 比赛 ID
     * @author LinJHS
     */
    public void openGamePermission(int gameId) {
        UUID uuid = UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(PERMISSION_KEY + gameId,
                uuid.toString());
    }

    /**
     * 关闭比赛
     *
     * @param gameId 比赛 ID
     * @author LinJHS
     */
    public void closeGamePermission(int gameId) {
        delete(PERMISSION_KEY + gameId);
    }
}
