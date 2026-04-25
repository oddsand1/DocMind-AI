package com.agent.ai_doc_agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync //启用异步处理
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);//核心线程数
        executor.setMaxPoolSize(10);//最大线程数
        executor.setQueueCapacity(25);//队列容量
        executor.setThreadNamePrefix("Async-");//线程名称前缀
        executor.initialize();//初始化线程池，必须调用此方法才能使线程池生效
        return executor;
    }


    //处理异步异常，记录异常信息
    //异步任务中的异常默认不会被主线程捕获，通过自定义异常处理器可以确保异常被记录，便于问题排查
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex,method,params)->{
            log.error("异步任务执行失败",method.getName(),ex);
        };
    }
}
