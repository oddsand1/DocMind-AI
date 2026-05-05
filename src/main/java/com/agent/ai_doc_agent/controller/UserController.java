package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.User;
import com.agent.ai_doc_agent.service.UserService;
import com.agent.ai_doc_agent.util.CurrentUser;
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
        String userId = requestData.get("userId");
        String refreshToken = requestData.get("refreshToken");
        
        Map<String, Object> data = userService.refreshToken(userId, refreshToken);
        return data != null ? Result.success(data) : Result.fail("刷新Token失败，请重新登录");
    }

    @PostMapping("/change-password")
    public Result changePassword(@RequestBody Map<String, String> requestData) {
        String oldPassword = requestData.get("oldPassword");
        String newPassword = requestData.get("newPassword");
        String confirmPassword = requestData.get("confirmPassword");

        //获取当前用户id
        String userId= CurrentUser.getUserId();

        // 检查用户是否登录
        if (userId == null) {
            return Result.fail("用户未登录，请先登录");
        }

        boolean success= userService.changePassword(userId, oldPassword, newPassword, confirmPassword);
        return success?Result.success("密码修改成功"):Result.fail("原密码错误或两次输入的密码不一致");
    }
}