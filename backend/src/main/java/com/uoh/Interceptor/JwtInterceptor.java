package com.uoh.Interceptor;

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
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId != null) {
                UserHolder.setUserId(userId); // 放到 ThreadLocal
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.remove();
    }
}
