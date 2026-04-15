package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.util.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

public interface DocumentService extends IService<Document> {
    
    /**
     * 上传并保存文档
     */
    JSONObject uploadAndSave(MultipartFile file) throws Exception;
    
    /**
     * 获取用户文档列表
     */
    List<Document> getUserDocuments(Long userId);
    
    /**
     * 根据ID获取文档（带权限检查）
     */
    Document getDocumentById(Long docId, Long userId);
    
    /**
     * 与文档相关的问题咨询
     */
    JSONObject askWithDocument(JSONObject request, String authHeader, ChatHistoryService chatHistoryService, PythonAIService pythonAIService, JwtUtil jwtUtil);
}