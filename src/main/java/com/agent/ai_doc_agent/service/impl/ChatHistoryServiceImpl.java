package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.exception.BusinessException;
import com.agent.ai_doc_agent.mapper.ChatHistoryMapper;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.service.DocumentService;
import com.agent.ai_doc_agent.util.CurrentUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {


    private final DocumentService documentService;
    
    @Override
    public List<ChatHistory> getHistoryByDocumentId(String documentId) {
        // 自动获取当前登录用户
        String userId = CurrentUser.getUserId();
        
        // 验证用户是否有权访问指定的文档
        try {
            Document document = documentService.getDocumentById(documentId, userId);
            if (document == null) {
                throw new BusinessException(404, "文档不存在或无权访问");
            }
        } catch (BusinessException e) {
            throw new BusinessException(403, "无权访问该文档的聊天历史: " + e.getMessage());
        }

        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doc_id", documentId).eq("user_id", userId).orderByDesc("create_time");
        return this.list(queryWrapper);
    }


    //根据userId获取该用户的对话历史，包括问题、回答等
    @Override
    public List<ChatHistory> getChatHistoryByUserId(String userId) {
        // 获取当前登录用户ID
        String currentUserId = CurrentUser.getUserId();
        
        // 确保只能获取自己的聊天记录
        if (!currentUserId.equals(userId)) {
            throw new BusinessException(403, "无权查看其他用户的聊天记录");
        }
        
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    

    //删除当前用户的所有聊天记录
    @Override
    public boolean deleteChatHistoryByUserId(String userId) {
        // 获取当前登录用户ID
        String currentUserId = CurrentUser.getUserId();
        
        // 确保只能删除自己的聊天记录
        if (!currentUserId.equals(userId)) {
            throw new BusinessException(403, "无权删除其他用户的聊天记录");
        }
        
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.remove(queryWrapper);
    }

    //删除当前文档的所有聊天记录（文档主键）
    @Override
    public boolean deleteChatHistoryByRecordId(Long recordId) {
        // 获取当前登录用户ID
        String userId = CurrentUser.getUserId();

        // 删除当前用户与指定文档相关的聊天记录
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", recordId).eq("user_id", userId);
        return this.remove(queryWrapper);
    }
}