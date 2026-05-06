package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.util.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.baomidou.mybatisplus.extension.service.IService;

public interface DocumentService extends IService<Document> {

    JSONObject uploadAndSave(MultipartFile file) throws Exception;

    List<Document> getUserDocuments(String userId);

    Document getDocumentById(String docId, String userId);

    CompletableFuture<JSONObject> askWithDocument(JSONObject request, String authHeader, ChatHistoryService chatHistoryService, PythonAIService pythonAIService, JwtUtil jwtUtil);

    boolean deleteDocument(String docId,String userId);

    List<Document> searchDocumentsByName(String userId, String keyword);

}