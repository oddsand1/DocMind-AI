package com.agent.ai_doc_agent.config;

import com.agent.ai_doc_agent.exception.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableAsync
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // 密码加密工具（不变）
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  新版 Spring Security 6.x 正确写法（无任何报错）
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        http
                // 关闭 CSRF 防护（新写法）
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行登录、注册、验证码和刷新Token接口
                        .requestMatchers("/user/login", "/user/register", "/user/captcha", "/user/refresh-token").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 添加 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置异常处理，让异常能够被 GlobalExceptionHandler 捕获
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"用户未登录\"}");
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(403);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"无权访问该资源\"}");
                    })
                );

        return http.build();
    }
}