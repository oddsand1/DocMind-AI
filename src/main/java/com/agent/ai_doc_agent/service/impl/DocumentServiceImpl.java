package com.agent.ai_doc_agent.service.impl;

import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.mapper.DocumentMapper;
import com.agent.ai_doc_agent.service.DocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {
}
