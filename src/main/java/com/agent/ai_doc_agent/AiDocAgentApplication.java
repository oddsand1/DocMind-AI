package com.agent.ai_doc_agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//改注解包含了@ComponentScan可以扫描启动类所在包及其子包下的spring组件，包括Controller、Service、Repository等
@SpringBootApplication

// 开启异步任务
@EnableAsync

// 扫描你mapper目录下的所有Mapper接口
@MapperScan("com.agent.ai_doc_agent.mapper")
public class AiDocAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDocAgentApplication.class, args);
        System.out.println("=====================================");
        System.out.println("✅ AI文档分析助手-Java后端启动成功！");
        System.out.println("📌 服务地址：http://127.0.0.1:8083");
        System.out.println("📌 测试接口：http://127.0.0.1:8083/document/test-link");
        System.out.println("=====================================");
    }
}