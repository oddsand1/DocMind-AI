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
        return chatHistoryService.save(chatHistory);
    }

    @GetMapping("/get/{id}")
    public ChatHistory get(@PathVariable Long id) {
        return chatHistoryService.getById(id);
    }

    @GetMapping("/list")
    public List<ChatHistory> list() {
        return chatHistoryService.list();
    }

    @GetMapping("/history")
    public Result<List<ChatHistory>> getHistory(@RequestParam Long documentId) {
        // 自动获取当前登录用户
        Long userId = CurrentUser.getUserId();

        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doc_id", documentId).eq("user_id", userId).orderByDesc("create_time");
        List<ChatHistory> list = chatHistoryService.list(queryWrapper);

        return Result.success(list);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return chatHistoryService.removeById(id);
    }
}
