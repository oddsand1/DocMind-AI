package com.agent.ai_doc_agent.config;

import com.agent.ai_doc_agent.util.CurrentUser;
import com.agent.ai_doc_agent.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    //拦截器基于spring框架，实现了HandlerInterceptor接口
    //对于通过配置类手动注册的组件（如拦截器、过滤器等），使用显式构造函数是最直接和可靠的方式，确保依赖能够正确传递
    //所以这里没有用lombok的@RequiredArgsConstructor注解
    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        String token = auth.substring(7);
        try {
            Long userId = jwtUtil.extractUserId(token);
            CurrentUser.set(userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentUser.clear();
    }
}