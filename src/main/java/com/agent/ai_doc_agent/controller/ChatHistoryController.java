package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.util.CurrentUser;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/chat")
public class ChatHistoryController {
    @Resource
    private ChatHistoryService chatHistoryService;

    @PostMapping("/add")
    public boolean add(@RequestBody ChatHistory chatHistory) {
        return chatHistoryService.saveChatHistory(chatHistory);
    }

    @GetMapping("/get/{id}")
    public ChatHistory get(@PathVariable Long id) {
        return chatHistoryService.getChatHistoryById(id);
    }

    @GetMapping("/list")
    public List<ChatHistory> list() {
        return chatHistoryService.getAllChatHistories();
    }

    @GetMapping("/history")
    public Result<List<ChatHistory>> getHistory(@RequestParam Long documentId) {
        List<ChatHistory> list = chatHistoryService.getHistoryByDocumentId(documentId);
        return Result.success(list);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return chatHistoryService.deleteChatHistoryById(id);
    }
}