package com.agent.ai_doc_agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("document")
public class Document {
    @TableId(type = IdType.AUTO)
    private String id;
    // 用户ID
    private String userId;
    // 文件名
    private String fileName;
    // 文件存储路径
    private String filePath;
    // 文件类型
    private String fileType;
    // 文件大小
    private Long fileSize;
    // 文件内容
    private String content;
    // AI总结
    private String summary;
    // 创建时间
    private LocalDateTime createTime;
}