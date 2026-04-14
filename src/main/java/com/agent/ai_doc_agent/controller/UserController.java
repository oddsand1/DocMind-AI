package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.User;
import com.agent.ai_doc_agent.service.UserService;
import com.agent.ai_doc_agent.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        boolean ok = userService.register(user);
        if (ok) {
            return Result.success("注册成功");
        } else {
            return Result.fail("用户名已存在");
        }
    }

    // 用户登录 —— 只加 token，其他完全不动！
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            // 生成 token
            String token = jwtUtil.generateToken(loginUser.getId());

            // 组装返回：保留原来的用户信息 + 新增token
            Map<String, Object> data = new HashMap<>();
            data.put("user", loginUser);
            data.put("token", token);

            return Result.success(data);
        } else {
            return Result.fail("用户名或密码错误");
        }
    }

    // 列表
    @PostMapping("/list")
    public Result list() {
        return Result.success(userService.list());
    }
}