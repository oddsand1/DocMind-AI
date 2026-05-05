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
    public Result<List<ChatHistory>> getHistory(@RequestParam String documentId) {
        List<ChatHistory> list = chatHistoryService.getHistoryByDocumentId(documentId);
        return Result.success(list);
    }


    @GetMapping("/get/{userId}")
    public Result<List<ChatHistory>> getByUserId(@PathVariable String userId) {
        List<ChatHistory> list = chatHistoryService.getChatHistoryByUserId(userId);
        return Result.success(list);
    }


    //删除当前用户的所有聊天记录
    @DeleteMapping("/delete/user/{userId}")
    public Result<Boolean> deleteByUserId(@PathVariable String userId) {
        boolean result = chatHistoryService.deleteChatHistoryByUserId(userId);
        return Result.success(result);
    }


    //删除指定文档的聊天历史记录
    @DeleteMapping("/delete/{recordId}")
    public Result<Boolean> deleteByRecordId(@PathVariable Long recordId) {
        boolean result = chatHistoryService.deleteChatHistoryByRecordId(recordId);
        return Result.success(result);
    }
}