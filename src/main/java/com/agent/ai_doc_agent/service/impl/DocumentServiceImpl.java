package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.mapper.DocumentMapper;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.service.DocumentService;
import com.agent.ai_doc_agent.service.PythonAIService;
import com.agent.ai_doc_agent.util.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public JSONObject uploadAndSave(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 自动获取当前登录用户
        Long userId = com.agent.ai_doc_agent.util.CurrentUser.getUserId();

        //将上传文件的字节数据（二进制字节数组）转换成UTF-8编码的字符串，获取文件内容
        String content = new String(file.getBytes(), "UTF-8");


        // 构建Document实体对象，封装文档信息
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());//设置文档原始文件名
        document.setFileType(file.getContentType());//设置文档类型（如txt、pdf等）
        document.setFileSize(file.getSize());//设置文档大小（字节数）
        document.setFilePath("");//设置文件存储路径（此处暂未配置，先赋值为空字符串）
        document.setContent(content);//设置文档完整内容
        document.setSummary(content.length() > 500 ? content.substring(0, 500) : content);//设置文档摘要，长度超过500就截取前500字符
        document.setUserId(userId);//关联文档所属的用户ID

        // 调用文档服务层方法，将文档信息保存到数据库，save是mybatisplus的IService接口下的核心方法之一
        this.save(document);

        // 构建返回结果对象，封装需要返回给前端的文档关键信息
        JSONObject result = new JSONObject();
        result.put("documentId", document.getId());
        result.put("fileName", document.getFileName());
        result.put("contentLength", content.length());

        return result;
    }

    @Override
    public List<Document> getUserDocuments(Long userId) {
        //构建查询条件，只查询当前用户自己的文档
        // QueryWrapper是Mybatis-Plus中的一个查询条件构造器，用于构建SQL查询条件
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();

        //筛选条件：数据库表中的user_id = 当前登录用户id
        queryWrapper.eq("user_id", userId);

        //执行查询，获取当前用户的所有文档列表，list是mybatisplus的IService接口下的核心方法之一
        return this.list(queryWrapper);
    }

    @Override
    public Document getDocumentById(Long docId, Long userId) {
        //根据文档ID去数据库查文档
        Document document = this.getById(docId);
        if (document == null) {
            throw new RuntimeException("文档未找到");
        }
        //判断文档属不属于当前登录用户
        if (!document.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该文档");
        }
        //验证成功-》返回文档详情
        return document;
    }

    @Override
    public JSONObject askWithDocument(JSONObject request, String authHeader, ChatHistoryService chatHistoryService, PythonAIService pythonAIService, JwtUtil jwtUtil) {
        String question = request.getString("question");
        Long documentId = request.getLong("documentId");
        String content = request.getString("content");

        if (question == null || question.isEmpty()) {
            throw new IllegalArgumentException("问题不能为空");
        }

        // 直接从 header 解析 token，跳过拦截器！
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token); // 直接解析

        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        if (documentId != null) {
            Document document = this.getById(documentId);
            if (document == null) {
                throw new RuntimeException("指定文档不存在");
            }
            if (!document.getUserId().equals(userId)) {
                throw new RuntimeException("无权访问该文档");
            }
            // 用数据库中真实的文档内容替换前端传入的content
            content = document.getContent();
        }

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("文档内容不能为空");
        }

        //调用Python AI服务，传入问题和文档内容，获取AI回答结果
        JSONObject askResult = pythonAIService.callSparkWithDocument(question, content);
        if (askResult.getInteger("code") != 200) {
            //msg的类型是字符串，从AI回答的JSON数据里找到msg这个key，然后获取对应的值
            throw new RuntimeException(askResult.getString("msg"));
        }

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setDocId(documentId);
        chatHistory.setUserId(userId);
        chatHistory.setQuestion(question);
        chatHistory.setAnswer(askResult.getJSONObject("data").getString("answer"));
        chatHistoryService.save(chatHistory);

        // data的类型是JSONObject，从AI回答的JSON数据里找到data这个key，再获取对应的值
        return askResult.getJSONObject("data");
    }
}