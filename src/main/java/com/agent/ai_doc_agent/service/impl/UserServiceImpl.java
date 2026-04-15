package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.User;
import com.agent.ai_doc_agent.mapper.UserMapper;
import com.agent.ai_doc_agent.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //UserServiceImpl是真正写业务逻辑的地方


    // 密码加密器 / BCrypt加密器，由spring security提供，使用之前要先配置@Bean
    private final BCryptPasswordEncoder passwordEncoder;

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