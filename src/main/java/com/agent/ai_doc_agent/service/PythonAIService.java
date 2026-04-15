package com.agent.ai_doc_agent.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class PythonAIService {

    //用于调用Python服务，功能有服务间通信，处理文档，AI问答，连接测试


    @Value("${python.service.base-url}")
    private String pythonBaseUrl;

    // 获取Python基础地址（给controller用）
    public String getPythonBaseUrl() {
        return pythonBaseUrl;
    }

    // 1. 测试Python链路
    public JSONObject testPythonLink() {
        try {
            String url = pythonBaseUrl + "/ai/test";
            HttpResponse response = HttpRequest.get(url).timeout(30000).execute();
            return JSONObject.parseObject(response.body());
        } catch (Exception e) {
            log.error("Python服务连通性测试失败", e);
            JSONObject error = new JSONObject();
            error.put("code", 500);
            error.put("msg", "Python服务未启动/链路不通：" + e.getMessage());
            error.put("data", null);
            return error;
        }
    }

    // 2. 调用Python文档解析接口
    public JSONObject callDocumentParse(MultipartFile file) {
        try {
            //url为Python服务的文档解析接口地址
            String url = pythonBaseUrl + "/api/document/parse";
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isEmpty()) {
                filename = "unknown_file";
            }
            log.info("调用Python文档解析接口，文件名：{}", filename);

            HttpResponse response = HttpRequest.post(url)
                    .form("file", filename, file.getInputStream())
                    .timeout(60000)
                    .execute();

            String body = response.body();
            log.info("Python文档解析接口原始响应：{}", body);

            JSONObject result = JSONObject.parseObject(body);
            log.info("Python文档解析接口返回：{}", result);
            return result;
        } catch (Exception e) {
            log.error("调用Python文档解析接口失败", e);
            JSONObject error = new JSONObject();
            error.put("code", 500);
            error.put("msg", "文档解析失败：" + e.getMessage());
            error.put("data", null);
            return error;
        }
    }

    // 3. 调用大模型问答接口（新增）
    public JSONObject callSparkAI(String question) {
        try {
            String url = pythonBaseUrl + "/ai/ask";
            JSONObject payload = new JSONObject();
            payload.put("question", question);
            HttpResponse response = HttpRequest.post(url)
                    .body(payload.toString())
                    .contentType("application/json")
                    .timeout(60000)
                    .execute();

            String body = response.body();
            JSONObject result = JSONObject.parseObject(body);
            log.info("大模型调用结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("调用大模型失败", e);
            JSONObject error = new JSONObject();
            error.put("code", 500);
            error.put("msg", "调用大模型失败：" + e.getMessage());
            error.put("data", null);
            return error;
        }
    }

    // 4. 文档问答接口
    public JSONObject callSparkWithDocument(String question, String content) {
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
            return result;
        } catch (Exception e) {
            log.error("调用文档问答失败", e);
            JSONObject error = new JSONObject();
            error.put("code", 500);
            error.put("msg", "调用文档问答失败：" + e.getMessage());
            error.put("data", null);
            return error;
        }
    }
}