package com.uoh.config;


import com.uoh.Interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/auth/**",             // 登录接口
                        "/doc.html",          // Knife4j 文档页面
                        "/v3/api-docs/**",    // Swagger JSON
                        "/swagger-ui/**",     // Swagger UI
                        "/webjars/**"         // 静态资源
                );
    }
}

