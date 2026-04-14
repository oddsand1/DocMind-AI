package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.mapper.ChatHistoryMapper;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
}