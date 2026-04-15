package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/chat")
public class ChatHistoryController {
    @Resource
    private ChatHistoryService chatHistoryService;


    @GetMapping("/history")
    public Result<List<ChatHistory>> getHistory(@RequestParam Long documentId) {
        List<ChatHistory> list = chatHistoryService.getHistoryByDocumentId(documentId);
        return Result.success(list);
    }


    @PostMapping("/add")
    public boolean add(@RequestBody ChatHistory chatHistory) {
        return chatHistoryService.saveChatHistory(chatHistory);
    }

    @GetMapping("/get/{userId}")
    public ChatHistory get(@PathVariable Long userId) {
        return chatHistoryService.getChatHistoryById(userId);
    }


    @GetMapping("/list")
    public List<ChatHistory> list() {
        return chatHistoryService.getAllChatHistories();
    }

    @DeleteMapping("/delete/{userId}")
    public boolean delete(@PathVariable Long userId) {
        return chatHistoryService.deleteChatHistoryById(userId);
    }
}