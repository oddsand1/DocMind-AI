package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.User;
import com.agent.ai_doc_agent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        boolean ok = userService.register(user);
        return ok ? Result.success("注册成功") : Result.fail("用户名已存在");
    }

    @GetMapping("/captcha")
    public Result getCaptcha() {
        Map<String, Object> data = userService.getCaptcha();
        return Result.success(data);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String captcha = loginData.get("captcha");
        String captchaKey = loginData.get("captchaKey");
        
        Map<String, Object> data = userService.loginWithCaptcha(username, password, captchaKey, captcha);
        if (data == null || data.containsKey("error")) {
            String errorMsg = data != null ? (String) data.get("error") : "登录失败";
            return Result.fail(errorMsg);
        }
        return Result.success(data);
    }

    @PostMapping("/refresh-token")
    public Result refreshToken(@RequestBody Map<String, String> requestData) {
        Long userId = Long.parseLong(requestData.get("userId"));
        String refreshToken = requestData.get("refreshToken");
        
        Map<String, Object> data = userService.refreshToken(userId, refreshToken);
        return data != null ? Result.success(data) : Result.fail("刷新Token失败，请重新登录");
    }

}