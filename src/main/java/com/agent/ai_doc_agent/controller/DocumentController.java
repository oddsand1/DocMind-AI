package com.agent.ai_doc_agent.controller;

import com.agent.ai_doc_agent.common.Result;
import com.agent.ai_doc_agent.entity.ChatHistory;
import com.agent.ai_doc_agent.entity.Document;
import com.agent.ai_doc_agent.service.ChatHistoryService;
import com.agent.ai_doc_agent.service.DocumentService;
import com.agent.ai_doc_agent.service.PythonAIService;
import com.agent.ai_doc_agent.util.CurrentUser;
import com.agent.ai_doc_agent.util.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


@RestController
@RequestMapping("/document")

//简化依赖注入
@RequiredArgsConstructor
public class DocumentController {

    // 注入Python服务调用工具类
    private final PythonAIService pythonAIService;

    // 声明一个不可变的 DocumentService 类型的成员变量，用于处理与文档相关的业务逻辑
    private final DocumentService documentService;

    // 声明一个不可变的 ChatHistoryService 类型的成员变量，用于处理聊天记录相关的业务逻辑
    private final ChatHistoryService chatHistoryService;

    private final JwtUtil jwtUtil;//注入Jwt（JSON Web Token）工具类，用于处理token等，防止越权访问



    /*@GetMapping("/test-link")
    public Result<?> testLink() {
        // 调用pythonAIService的testPythonLink方法，获取测试连接的结果，返回值为JSONObject类型
        // 这个类是阿里巴巴开发的FastJSON库中的类，用于处理JSON数据，能方便的进行JSON数据的解析、创建、修改和读取等
        JSONObject result = pythonAIService.testPythonLink();

        // code==200通常表示连接测试成功
        if (result.getInteger("code") == 200) {
            return Result.success(result);
        } else {3
            // 若失败，从结果中获取“msg”字段的错误信息，使用Result.fail方法封装错误信息并返回
            return Result.fail(result.getString("msg"));
        }
    }*/



    @PostMapping("/upload-save")
    public Result<?> uploadAndSave(@RequestParam("file") MultipartFile file) throws Exception {
        // @RequestParam用于获取简单类型的请求体参数，并绑定到方法参数上
        // MultipartFile也是spring-web包下的，用于处理文件上传的一个接口，他提供了获取上传文件的相关信息和操作方法，常配合文件上传功能使用


        if (file.isEmpty()) {
            return Result.fail("上传的文件不能为空");
        }

        // 自动获取当前登录用户
        Long userId = CurrentUser.getUserId();

        //将上传文件的字节数据（二进制字节数组）转换成UTF-8编码的字符串，获取文件内容
        String content = new String(file.getBytes(), "UTF-8");


        // 构建Document实体对象，封装文档信息
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());//设置文档原始文件名
        document.setFileType(file.getContentType());//设置文档类型（如txt、pdf等）
        document.setFileSize(file.getSize());//设置文档大小（字节数）
        document.setFilePath("");//设置文件存储路径（此处暂未配置，先赋值为空字符串）
        document.setContent(content);//设置文档完整内容
        document.setSummary(content.length() > 500 ? content.substring(0, 500) : content);//设置文档摘要，长度超过500就截取前500字符
        document.setUserId(userId);//关联文档所属的用户ID

        // 调用文档服务层方法，将文档信息保存到数据库，save是mybatisplus的IService接口下的核心方法之一
        documentService.save(document);

        // 构建返回结果对象，封装需要返回给前端的文档关键信息
        JSONObject result = new JSONObject();
        result.put("documentId", document.getId());
        result.put("fileName", document.getFileName());
        result.put("contentLength", content.length());

        // 返回成功结果，将封装好的信息返回给前端
        return Result.success(result);
    }


    @GetMapping("/list")
    public Result<?> listDocuments() {
        //1.获取当前登录用户的ID（JWT自动获取）
        Long userId = CurrentUser.getUserId();

        //2.构建查询条件，只查询当前用户自己的文档
        // QueryWrapper是Mybatis-Plus中的一个查询条件构造器，用于构建SQL查询条件
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();

        //3.筛选条件：数据库表中的user_id = 当前登录用户id
        queryWrapper.eq("user_id", userId);

        //4.执行查询，获取当前用户的所有文档列表，list是mybatisplus的IService接口下的核心方法之一
        List<Document> documents = documentService.list(queryWrapper);

        //5.返回文档数据
        return Result.success(documents);
    }


    @GetMapping("/{id}")
    public Result<?> getDocumentById(@PathVariable Long id) {
        //1.获取当前登录用户的ID（JWT自动获取）
        Long userId = CurrentUser.getUserId();

        //2.根据文档ID去数据库查文档
        Document document = documentService.getById(id);
        if (document == null) {
            return Result.fail("文档未找到");
        }
        //判断文档属不属于当前登录用户
        if (!document.getUserId().equals(userId)) {
            return Result.fail("无权访问该文档");
        }
        //验证成功-》返回文档详情
        return Result.success(document);
    }


    @PostMapping("/ask-with-document")
    public Result<?> askWithDocument(
            @RequestBody JSONObject request, //@RequestBody 获取请求体中的复杂数据并且绑定到当前方法参数上，这里的复杂数据就是JSONObject对象
            @RequestHeader("Authorization") String authHeader) //@RequestHeader将HTTP请求头中的信息绑定到方法参数上   这里的作用是直接从请求头拿token
            //从前端发来的 HTTP 请求头中，找到名为Authorization的字段，并把这个字段的值绑定到后面的authHeader变量上。
    {

        String question = request.getString("question");
        Long documentId = request.getLong("documentId");
        String content = request.getString("content");

        if (question == null || question.isEmpty()) {
            return Result.fail("问题不能为空");
        }

        try {
            // 直接从 header 解析 token，跳过拦截器！
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token); // 直接解析

            if (userId == null) {
                return Result.fail("用户未登录");
            }

            if (documentId != null) {
                Document document = documentService.getById(documentId);
                if (document == null) {
                    return Result.fail("指定文档不存在");
                }
                if (!document.getUserId().equals(userId)) {
                    return Result.fail("无权访问该文档");
                }
                // 用数据库中真实的文档内容替换前端传入的content
                content = document.getContent();
            }

            if (content == null || content.isEmpty()) {
                return Result.fail("文档内容不能为空");
            }

            //调用Python AI服务，传入问题和文档内容，获取AI回答结果
            JSONObject askResult = pythonAIService.callSparkWithDocument(question, content);
            if (askResult.getInteger("code") != 200) {
                //msg的类型是字符串，从AI回答的JSON数据里找到msg这个key，然后获取对应的值
                return Result.fail(askResult.getString("msg"));
            }

            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setDocId(documentId);
            chatHistory.setUserId(userId);
            chatHistory.setQuestion(question);
            chatHistory.setAnswer(askResult.getJSONObject("data").getString("answer"));
            chatHistoryService.save(chatHistory);

            // data的类型是JSONObject，从AI回答的JSON数据里找到data这个key，再获取对应的值
            return Result.success(askResult.getJSONObject("data"));

        } catch (Exception e) {
            //捕获Token解析失败、服务调用异常等所有异常
            return Result.fail("Token无效或已过期：" + e.getMessage());
        }
    }
}