package com.agent.ai_doc_agent.config;

import com.agent.ai_doc_agent.util.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //对于通过配置类手动注册的组件（如拦截器、过滤器等），使用显式构造函数是最直接和可靠的方式，确保依赖能够正确传递
    //所以这里没有用lombok的@RequiredArgsConstructor注解

    private final JwtUtil jwtUtil;

    public WebConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(jwtUtil))
                .addPathPatterns("/**")// 所有路径
                .excludePathPatterns("/user/login", "/user/register");// 放行登录注册路径
    }
}