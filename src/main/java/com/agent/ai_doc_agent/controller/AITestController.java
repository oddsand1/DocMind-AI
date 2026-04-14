package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.service.PythonAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AITestController {

    @Autowired
    private PythonAIService pythonAIService;

    @GetMapping("/test/python")
    public String testPython() {
        return null;
    }
}