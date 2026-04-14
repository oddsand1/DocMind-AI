package com.agent.ai_doc_agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    // 给Java配置调用接口的工具
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
