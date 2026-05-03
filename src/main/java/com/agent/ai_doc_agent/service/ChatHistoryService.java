package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.ChatHistory;


import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatHistoryService extends IService<ChatHistory> {
    
    //根据文档ID获取聊天历史
    List<ChatHistory> getHistoryByDocumentId(String documentId);
    
    //根据用户ID获取聊天历史
    List<ChatHistory> getChatHistoryByUserId(String userId);

    //删除聊天历史
    boolean deleteChatHistoryByUserId(String userId);
    
    //删除指定文档的聊天历史
    boolean deleteChatHistoryByDocumentId(String documentId);
}