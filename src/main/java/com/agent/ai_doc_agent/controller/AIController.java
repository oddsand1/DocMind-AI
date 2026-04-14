package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.service.PythonAIService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final PythonAIService pythonAIService;
    private final ChatHistoryService chatHistoryService;

    /**
     * 测试大模型调用接口
     * @param question 用户问题
     * @return 大模型回答
     */
    @PostMapping("/ask")
    public Result<?> askAI(@RequestParam String question) {
        if (question == null || question.isEmpty()) {
            return Result.fail("问题不能为空");
        }
        JSONObject result = pythonAIService.callSparkAI(question);
        if (result.getInteger("code") == 200) {
            // 插入聊天记录到chat_history表
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setQuestion(question);
            chatHistory.setAnswer(result.getJSONObject("data").getString("answer"));
            chatHistoryService.save(chatHistory);
            return Result.success(result);
        } else {
            return Result.fail(result.getString("msg"));
        }
    }
}