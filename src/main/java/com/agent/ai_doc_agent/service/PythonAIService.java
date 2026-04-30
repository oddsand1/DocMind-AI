package com.agent.ai_doc_agent.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PythonAIService {

    //这个类用于调用Python服务，功能有服务间通信，处理文档，AI问答，连接测试


    @Value("${python.service.base-url}")
    private String pythonBaseUrl;

    // 获取Python基础地址（给controller用）
    public String getPythonBaseUrl() {
        return pythonBaseUrl;
    }


    // 文档问答接口
    @Async
    public CompletableFuture<JSONObject> callSparkWithDocument(String question, String content) {
        try {
            String url = pythonBaseUrl + "/ai/ask-with-document";
            JSONObject payload = new JSONObject();
            payload.put("question", question);
            payload.put("content", content);
            HttpResponse response = HttpRequest.post(url)
                    .body(payload.toString())
                    .contentType("application/json")
                    .timeout(60000)
                    .execute();

            String body = response.body();
            JSONObject result = JSONObject.parseObject(body);
            log.info("文档问答调用结果：{}", result);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("调用文档问答失败", e);
            JSONObject error = new JSONObject();
            error.put("code", 500);
            error.put("msg", "调用文档问答失败：" + e.getMessage());
            error.put("data", null);
            return CompletableFuture.completedFuture(error);
        }
    }
}