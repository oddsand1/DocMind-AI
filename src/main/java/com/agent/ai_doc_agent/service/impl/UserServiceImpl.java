package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.User;
import com.agent.ai_doc_agent.mapper.UserMapper;
import com.agent.ai_doc_agent.service.RedisService;
import com.agent.ai_doc_agent.service.UserService;
import com.agent.ai_doc_agent.util.CaptchaUtil;
import com.agent.ai_doc_agent.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //UserServiceImpl是真正写业务逻辑的地方

    // 密码加密器 / BCrypt加密器，由spring security提供，使用之前要先配置@Bean
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final CaptchaUtil captchaUtil;

    @Override
    public boolean register(User user) {
        // 1. 判断用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        // getOne也是Mybatis-Plus的方法，用于根据查询条件查询单条记录
        User exist = getOne(wrapper);
        if (exist != null) {
            return false;
        }
        // 2. 密码加密
        String encodePwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);
        // 3. 保存
        return save(user);
    }

    //专注登录逻辑
    @Override
    public User login(String username, String password) {
        // 1. 查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        //根据wrapper执行SQL，返回符合条件的第一个对象
        User user = getOne(wrapper);
        if (user == null) {
            return null;
        }
        // 2. 校验密码
        boolean match = passwordEncoder.matches(password, user.getPassword());
        return match ? user : null;
    }

    @Override
    public Map<String, Object> loginWithCaptcha(String username, String password, String captchaKey, String captcha) {
        if (!validateCaptcha(captchaKey, captcha)) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", "验证码错误或已过期");
            return result;
        }
        
        User loginUser = login(username, password);
        if (loginUser == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", "用户名或密码错误");
            return result;
        }
        
        return generateTokens(loginUser);
    }

    private Map<String, Object> generateTokens(User user) {
        String accessToken = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        String refreshTokenKey = "refresh_token:" + user.getId();
        redisService.set(refreshTokenKey, refreshToken, jwtUtil.getExpiration(), java.util.concurrent.TimeUnit.MILLISECONDS);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);

        return data;
    }

    @Override
    public boolean validateCaptcha(String captchaKey, String captcha) {
        if (captchaKey == null || captcha == null) {
            return false;
        }
        // 从Redis中获取验证码
        String storedCaptcha = redisService.get(captchaKey);
        if (storedCaptcha == null) {
            return false;
        }
        // 校验redis里面的验证码是否和前端传递的验证码一致
        boolean isValid = storedCaptcha.equalsIgnoreCase(captcha);
        if (isValid) {
            //一致就及时删除redis里面的验证码，防止复用
            redisService.delete(captchaKey);
        }
        return isValid;
    }

    @Override
    public Map<String, Object> refreshToken(String userId, String refreshToken) {
        String refreshTokenKey = "refresh_token:" + userId;
        String storedRefreshToken = redisService.get(refreshTokenKey);
        
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            return null;
        }
        
        if (jwtUtil.isTokenExpired(refreshToken)) {
            redisService.delete(refreshTokenKey);
            return null;
        }
        
        String newAccessToken = jwtUtil.generateToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        
        redisService.set(refreshTokenKey, newRefreshToken, jwtUtil.getExpiration(), java.util.concurrent.TimeUnit.MILLISECONDS);
        
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", newAccessToken);
        data.put("refreshToken", newRefreshToken);
        
        return data;
    }

    @Override
    public Map<String, Object> getCaptcha() {
        String captcha = captchaUtil.generateCaptcha();
        String captchaKey = captchaUtil.generateCaptchaKey();
        redisService.set(captchaKey, captcha, 5, java.util.concurrent.TimeUnit.MINUTES);
        
        Map<String, Object> data = new HashMap<>();
        data.put("captcha", captcha);
        data.put("captchaKey", captchaKey);
        
        return data;
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword, String confirmPassword) {
        //1.根据用户id查找用户
        User user=getById(userId);
        if(user==null){
            return false;
        }

        //2.验证原密码是否正确
        boolean match=passwordEncoder.matches(oldPassword, user.getPassword());
        if(!match){
            return false;
        }

        //3.验证两次新密码是否一致
        if(!newPassword.equals(confirmPassword)){
            return false;
        }

        //4.更新密码（加密存储）
        String encodePwd=passwordEncoder.encode(newPassword);
        user.setPassword(encodePwd);

        return updateById(user);
    }
}