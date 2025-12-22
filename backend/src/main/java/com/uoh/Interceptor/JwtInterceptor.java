package com.uoh.Interceptor;

import com.uoh.common.ErrorCode;
import com.uoh.exception.BusinessException;
import com.uoh.utils.JWTUtils;
import com.uoh.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JWTUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String authHeader = request.getHeader("Authorization");

        // 没有 token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String token = authHeader.substring(7);

        try {
            // 校验 token
            Long userId = jwtUtils.getUserIdFromToken(token);

            if (userId == null) {
                // token 无效或解析失败
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }

            // token 合法，放到 ThreadLocal
            UserHolder.setUserId(userId);

            return true; // 放行请求

        } catch (Exception e) {
            // token 解析异常（过期 / 篡改）
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.remove();
    }
}
