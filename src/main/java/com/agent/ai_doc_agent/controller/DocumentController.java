package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.service.DocumentService;
import com.agent.ai_doc_agent.service.PythonAIService;
import com.agent.ai_doc_agent.util.CurrentUser;
import com.agent.ai_doc_agent.util.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/document")

//简化依赖注入
@RequiredArgsConstructor
public class DocumentController {

    // 注入Python服务调用工具类
    private final PythonAIService pythonAIService;

    // 声明一个不可变的 DocumentService 类型的成员变量，用于处理与文档相关的业务逻辑
    private final DocumentService documentService;

    // 声明一个不可变的 ChatHistoryService 类型的成员变量，用于处理聊天记录相关的业务逻辑
    private final ChatHistoryService chatHistoryService;

    private final JwtUtil jwtUtil;//注入Jwt（JSON Web Token）工具类，用于处理token等，防止越权访问

    @PostMapping("/upload-save")
    public Result<?> uploadAndSave(@RequestParam("file") MultipartFile file) throws Exception {
        JSONObject result = documentService.uploadAndSave(file);
        return Result.success(result);
    }

    @GetMapping("/list")//获取用户所有文档
    public Result<?> listDocuments() {
        //获取当前登录用户的ID（JWT自动获取）
        Long userId = CurrentUser.getUserId();
        List<Document> documents = documentService.getUserDocuments(userId);
        return Result.success(documents);
    }

    @GetMapping("/{id}")//根据文档ID获取文档
    public Result<?> getDocumentById(@PathVariable Long id) {
        //获取当前登录用户的ID（JWT自动获取）
        Long userId = CurrentUser.getUserId();
        Document document = documentService.getDocumentById(id, userId);
        return Result.success(document);
    }

    @PostMapping("/ask-with-document")
    public Result<?> askWithDocument(
            @RequestBody JSONObject request, //@RequestBody 获取请求体中的复杂数据并且绑定到当前方法参数上，这里的复杂数据就是JSONObject对象
            @RequestHeader("Authorization") String authHeader) {
        JSONObject result = documentService.askWithDocument(request, authHeader, chatHistoryService, pythonAIService, jwtUtil);
        return Result.success(result);
    }
}