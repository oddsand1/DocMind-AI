package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.service.PythonAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AITestController {

    private final PythonAIService pythonAIService;

    @GetMapping("/test/python")
    public String testPython() {
        return null;
    }
}