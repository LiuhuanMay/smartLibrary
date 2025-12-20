package com.uoh.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class JWTUtilsTest {

    @Resource
    private JWTUtils jwtUtils;

    @Test
    void generateToken() {
        Long userId = 12123434343L;
        String token = jwtUtils.generateToken(userId);
        log.info(token);

    }

    @Test
    void getUserIdFromToken() {
        Long userIdFromToken = jwtUtils.getUserIdFromToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjEyMzQzNDM0MyIsImV4cCI6MTc2NjIwMzUyNSwiaWF0IjoxNzY2MjAxNzI1fQ.oGTOtONk2h6_Kb3Cah9T8o3EtpnlqogzxbshjtGal8Q");
        log.info(String.valueOf(userIdFromToken));
    }
}