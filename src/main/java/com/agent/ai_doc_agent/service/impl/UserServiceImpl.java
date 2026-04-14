package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.User;
import com.agent.ai_doc_agent.mapper.UserMapper;
import com.agent.ai_doc_agent.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean register(User user) {
        // 1. 判断用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
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

    @Override
    public User login(String username, String password) {
        // 1. 查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = getOne(wrapper);
        if (user == null) {
            return null;
        }
        // 2. 校验密码
        boolean match = passwordEncoder.matches(password, user.getPassword());
        return match ? user : null;
    }
}