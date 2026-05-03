package com.agent.ai_doc_agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_history")
public class ChatHistory {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 用户ID
    private String userId;
    // 关联文档ID
    private String docId;
    // 用户问题
    private String question;
    // AI回答
    private String answer;
    // 创建时间
    private LocalDateTime createTime;
}