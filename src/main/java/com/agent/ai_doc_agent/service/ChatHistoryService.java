package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.ChatHistory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.agent.ai_doc_agent.common.Result;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatHistoryService extends IService<ChatHistory> {
    
    /**
     * 根据文档ID获取聊天历史
     */
    List<ChatHistory> getHistoryByDocumentId(Long documentId);
    
    /**
     * 保存聊天历史
     */
    boolean saveChatHistory(ChatHistory chatHistory);
    
    /**
     * 根据ID获取聊天历史
     */
    ChatHistory getChatHistoryById(Long id);
    
    /**
     * 获取所有聊天历史
     */
    List<ChatHistory> getAllChatHistories();
    
    /**
     * 删除聊天历史
     */
    boolean deleteChatHistoryById(Long id);
}