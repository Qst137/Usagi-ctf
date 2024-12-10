package top.linjhs.usagictfplatformbackend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author LinJHS
 * @version 1.0
 */
public class JwtUtils {
    private static final String signKey = "Usagi_w4r2x2H^HHih7kv1ho#JUz3KGM5AlqZ6KR53g1";
    private static final Long expireTime = 24*3600*1000L;
    private static Key key;

    /**
     * 生成 JWT 令牌
     *
     * @author LinJHS
     * @param claims 令牌中携带的数据
     * @return java.lang.String
     */
    public static String generateJwt(Map<String,Object> claims){
        if(null == key)
            key = Keys.hmacShaKeyFor(signKey.getBytes());
        return Jwts.builder()
                .addClaims(claims)
                .signWith(key)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime)) // 设置有效期一天
                .compact();
    }

    /**
     * 解析 JWT 令牌
     *
     * @author LinJHS
     * @param jwt 传入的令牌
     * @return io.jsonwebtoken.Claims
     */
    public static Claims parseJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
