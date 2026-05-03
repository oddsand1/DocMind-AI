package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User> {

    //接口只写方法名，不写实现
    //写接口方便替换实现、方便测试、规范统一


    //注册
    boolean register(User user);

    //登录
    User login(String username, String password);

    // 带验证码验证的登录
    Map<String, Object> loginWithCaptcha(String username, String password, String captchaKey, String captcha);

    // 验证验证码
    boolean validateCaptcha(String captchaKey, String captcha);

    // 刷新Token
    Map<String, Object> refreshToken(String userId, String refreshToken);

    // 获取验证码
    Map<String, Object> getCaptcha();
}