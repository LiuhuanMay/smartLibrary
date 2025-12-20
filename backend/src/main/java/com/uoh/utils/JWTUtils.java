package com.uoh.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JWTUtils {


    private static final String SECRET = "lizhangdan";



    /**
     * 生成JWT token
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + 30 * 60 * 1000); // 30分钟有效

        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .sign(Algorithm.HMAC256(SECRET));
    }



    /**
     * 解析JWT token
     */
    public Long getUserIdFromToken(String token) {
        try {
            // 构建验证器
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                    .build();

            // 校验 token
            DecodedJWT jwt = verifier.verify(token);

            // 获取 sub 字段，也就是 userId
            String userIdStr = jwt.getSubject();
            return Long.valueOf(userIdStr);

        } catch (JWTVerificationException e) {
            // token 校验失败（过期 / 篡改 / 无效）
            return null;
        }
    }
}