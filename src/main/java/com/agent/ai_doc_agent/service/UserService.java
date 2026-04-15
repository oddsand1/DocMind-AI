package com.agent.ai_doc_agent.service;

import com.agent.ai_doc_agent.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    //接口只写方法名，不写实现
    //写接口方便替换实现、方便测试、规范统一


    //注册
    boolean register(User user);

    //登录
    User login(String username, String password);
}