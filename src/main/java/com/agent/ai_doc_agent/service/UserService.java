package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    //注册
    boolean register(User user);

    User login(String username, String password);
}