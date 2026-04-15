package com.agent.ai_doc_agent.mapper;

import com.agent.ai_doc_agent.entity.ChatHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
    //操作数据库的方法，由mybatis-plus提供，BaseMapper是mybatis-plus的基类，包含了CRUD操作
}