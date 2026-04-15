package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.mapper.ChatHistoryMapper;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.util.CurrentUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
    
    @Override
    public List<ChatHistory> getHistoryByDocumentId(Long documentId) {
        // 自动获取当前登录用户
        Long userId = CurrentUser.getUserId();

        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doc_id", documentId).eq("user_id", userId).orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public boolean saveChatHistory(ChatHistory chatHistory) {
        return this.save(chatHistory);
    }

    @Override
    public ChatHistory getChatHistoryById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<ChatHistory> getAllChatHistories() {
        return this.list();
    }

    @Override
    public boolean deleteChatHistoryById(Long id) {
        return this.removeById(id);
    }
}