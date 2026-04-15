package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatHistoryController {
    private final ChatHistoryService chatHistoryService;


    @GetMapping("/history")
    public Result<List<ChatHistory>> getHistory(@RequestParam Long documentId) {
        List<ChatHistory> list = chatHistoryService.getHistoryByDocumentId(documentId);
        return Result.success(list);
    }


    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody ChatHistory chatHistory) {
        boolean result = chatHistoryService.saveChatHistory(chatHistory);
        return Result.success(result);
    }


    @GetMapping("/get/{userId}")
    public Result<List<ChatHistory>> getByUserId(@PathVariable Long userId) {
        List<ChatHistory> list = chatHistoryService.getChatHistoryByUserId(userId);
        return Result.success(list);
    }



    @DeleteMapping("/delete/{userId}")
    public Result<Boolean> delete(@PathVariable Long userId) {
        boolean result = chatHistoryService.deleteChatHistoryByUserId(userId);
        return Result.success(result);
    }
}