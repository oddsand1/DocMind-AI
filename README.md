# AI Doc Agent \- 文档智能问答系统

✨**Java \+ Python 双服务联动轻量化RAG文档问答系统**

基于SpringBoot后端管控 \+ FastAPI AI算力拆分架构，实现用户权限管理、多格式文档解析入库、私有化文档知识库问答、问答记录全溯源，适配内网轻量化文档答疑、企业本地知识库场景，开箱即用、权限隔离、低部署成本。

⭐ 开源求Star \| 适配私有化部署、二次开发、课程毕设、企业内网知识库

---

## 📌 项目核心说明

项目采用**前后算力拆分架构**：

1. **Java 后端\(8083端口\)**：负责用户鉴权、文件上传、数据库持久化、接口路由、业务逻辑、Redis缓存管控

2. **Python AI服务\(8080端口\)**：独立承载星火大模型调用、文档语义问答算力，解耦业务与AI算力，便于单独扩容、切换大模型

**端口故障提示**：本地默认端口 8080\(Python\)、8083\(Java\)，若访问 `http://127.0.0.1:8080` / `http://127.0.0.1:8083` / `http://127.0.0.1:8083/document/test-link` 提示URL错误，代表对应服务未启动、端口占用或配置地址不匹配，属于常规联动报错。

---

## 🛠️ 完整技术栈

### 🔹 Java 业务后端

|层级|技术组件|版本|
|---|---|---|
|核心框架|Spring Boot|3\.2\.10|
|运行环境|JDK|21|
|持久层|MyBatis\-Plus|3\.5\.10|
|业务数据库|MySQL|8\.0\+|
|缓存中间件|Redis|通用稳定版|
|安全认证|Spring Security \+ JWT\(jjwt\)|0\.11\.5|
|加密算法|BCrypt|内置|
|工具套件|Hutool|5\.8\.31|
|序列化|FastJSON2|2\.0\.33|
|文件上传|Apache Commons FileUpload|1\.5|
|代码简化|Lombok|1\.18\.44|
|构建工具|Maven|3\.8\+|

### 🔹 Python AI算力服务

- Web服务框架：FastAPI

- 大模型能力：讯飞Spark AI 星火大模型

- 运行环境：Python 3\.9\+

---

## 📂 标准化项目结构

```plaintext
ai-doc-agent/
├── src/
│   ├── main/
│   │   ├── java/com/agent/ai_doc_agent/
│   │   │   ├── common/                  # 公共通用模块
│   │   │   │   └── Result.java          # 全局统一响应封装
│   │   │   ├── config/                  # 全局配置类
│   │   │   │   ├── AsyncConfig.java     # 异步问答线程配置
│   │   │   │   ├── JwtAuthenticationFilter.java  # JWT鉴权过滤器
│   │   │   │   └── SecurityConfig.java  # SpringSecurity权限配置
│   │   │   ├── controller/              # 接口控制层
│   │   │   │   ├── UserController.java          # 用户账号接口
│   │   │   │   ├── DocumentController.java      # 文档管理接口
│   │   │   │   └── ChatHistoryController.java   # 问答历史接口
│   │   │   ├── entity/                  # 数据库实体映射
│   │   │   │   ├── User.java
│   │   │   │   ├── Document.java
│   │   │   │   └── ChatHistory.java
│   │   │   ├── exception/               # 全局异常处理
│   │   │   │   ├── BusinessException.java      # 自定义业务异常
│   │   │   │   └── GlobalExceptionHandler.java # 全局异常兜底
│   │   │   ├── mapper/                  # MyBatis-Plus数据层
│   │   │   ├── service/                 # 业务服务层
│   │   │   │   ├── PythonAIService.java # 对接Python AI远程调用
│   │   │   │   ├── RedisService.java    # Redis缓存工具服务
│   │   │   │   └── impl/                # 业务实现类
│   │   │   ├── util/                    # 全局工具类
│   │   │   └── AiDocAgentApplication.java  # SpringBoot启动入口
│   │   └── resources/
│   │       └── application.yml          # 全局配置文件
│   └── test/                            # 单元测试目录
├── document_table.sql                   # MySQL一键建表脚本
├── pom.xml                              # Maven依赖配置
└── README.md                            # 项目说明文档
```

---

## ✨ 核心功能特性

### 1\. 账号权限体系（安全闭环）

- 账号注册、验证码登录，图形验证码防暴力破解

- 双Token机制：AccessToken短期鉴权 \+ RefreshToken无感续期

- BCrypt不可逆密码加密，数据库无感加密存储

- 用户数据隔离：仅可查看、操作本人上传文档与问答记录

- 接口粒度权限管控，匿名接口放行、业务接口强制鉴权

### 2\. 全品类文档管理

- 支持格式：PDF / Word / TXT 主流文档上传解析

- 能力：文档上传、关键词检索、详情查看、单点删除

- 文档结构化入库：文件名、大小、类型、原文、摘要持久化存储

- 文件大小限制：默认单文件最大100MB，配置可动态修改

### 3\. 双模式AI智能问答

- 通用问答：不依托文档，直接调用星火大模型答疑

- 文档专属问答：绑定指定文档，基于文档上下文精准作答

- 异步调用AI接口，不阻塞Java后端主线程，高并发友好

- 算力解耦：AI服务独立部署，可随时替换本地/第三方大模型

### 4\. 问答记录溯源管理

- 按文档维度：查询单份文档全部问答对话

- 按用户维度：查询当前用户全部历史问答

- 支持单条删除、批量清空问答记录，数据自主可控

---

## 🗄️ 数据库设计

项目共计3张业务核心表，根目录 `document_table.sql` 一键执行建表，无需手动改字段

|数据表名|业务作用|核心字段|
|---|---|---|
|`user`|系统用户账号表|id, username, password, create\_time, update\_time|
|`document`|用户上传文档信息表|id, user\_id, file\_name, file\_path, file\_type, file\_size, content, summary, create\_time|
|`chat_history`|AI问答对话记录表|id, user\_id, doc\_id, question, answer, create\_time|

---

## 💻 环境前置要求

- ✅ JDK 21 \+ Maven 3\.8\+

- ✅ MySQL 8\.0\+

- ✅ Redis 服务本地启动

- ✅ Python 3\.9\+

- ✅ 有效讯飞Spark AI 密钥（配置至Python服务内）

---

## 🚀 一键部署启动流程

### 步骤1：项目克隆

```bash
git clone https://github.com/xxx/ai-doc-agent.git
cd ai-doc-agent
```

### 步骤2：数据库初始化

```bash
# 登录MySQL执行建表脚本
mysql -u root -p < document_table.sql
```

### 步骤3：修改全局配置 application\.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_document_agent?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root      # 你的MySQL账号
    password: 123456     # 你的MySQL密码
  redis:
    host: localhost
    port: 6379

# 对接Python AI服务地址(固定8080，不可随意修改)
python:
  service:
    base-url: http://127.0.0.1:8080

# JWT自定义密钥自行修改
jwt:
  secret: 自定义加密密钥
  expiration: 1800000
  refresh-expiration: 604800000

# Java后端固定端口8083
server:
  port: 8083
```

### 步骤4：启动双服务（顺序不可颠倒）

#### ① 优先启动 Python FastAPI AI服务\(8080\)

```bash
cd python服务目录
python main.py
```

#### ② 启动 Java SpringBoot 业务后端\(8083\)

```bash
# Maven启动
mvn spring-boot:run

# 或者使用包装脚本启动
# Windows
mvnw.cmd spring-boot:run
# Mac/Linux
./mvnw spring-boot:run
```

### 后端启动成功标识

```plaintext
=====================================
✅ AI文档分析助手-Java后端启动成功！
📌 服务地址：http://127.0.0.1:8083
📌 测试接口：http://127.0.0.1:8083/document/test-link
=====================================
```

**访问报错排查**：以上地址提示URL错误，仅为服务连通性测试接口异常，原因：Python服务未启动、端口被占用、yml内python服务地址填写错误，修正后即可恢复联动。

---

## 🔌 全局API接口文档

### 统一响应格式（所有接口通用）

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### Java后端接口 : 127\.0\.0\.1:8083

#### 用户模块 /user

|请求方式|接口路径|功能说明|是否鉴权|
|---|---|---|---|
|POST|/user/register|账号注册|否|
|GET|/user/captcha|获取登录验证码|否|
|POST|/user/login|账号登录，返回双Token|否|
|POST|/user/refresh\-token|刷新访问令牌|否|
|POST|/user/change\-password|修改登录密码|是|

#### 文档模块 /document

|请求方式|接口路径|功能说明|是否鉴权|
|---|---|---|---|
|POST|/document/upload\-save|上传解析文档入库|是|
|GET|/document/list|获取个人文档列表|是|
|GET|/document/\{id\}|查询文档详情|是|
|POST|/document/ask\-with\-document|绑定文档AI问答|是|
|DELETE|/document/\{id\}|删除个人文档|是|
|GET|/document/search|文件名检索文档|是|

#### 问答记录模块 /chat

|请求方式|接口路径|功能说明|是否鉴权|
|---|---|---|---|
|GET|/chat/history?documentId=xxx|查询单文档问答记录|是|
|GET|/chat/get/\{userId\}|查询用户全部问答|是|
|DELETE|/chat/delete/\{recordId\}|删除单条问答记录|是|
|DELETE|/chat/delete/user/\{userId\}|清空用户全部问答|是|

### Python AI接口 : 127\.0\.0\.1:8080

```http
# 通用AI问答
POST /ai/ask
Content-Type: application/json
{
  "question": "你的提问问题"
}

# 绑定文档AI问答
POST /ai/ask-with-document
Content-Type: application/json
{
  "question": "文档相关问题",
  "content": "文档完整解析文本内容"
}
```

---

## 🔐 安全机制详解

- 无状态JWT鉴权：业务请求统一携带 `Authorization: Bearer Token` 请求头

- 登录验证码拦截，杜绝账号暴力猜解

- 密码BCrypt加盐加密，不可逆存储

- 接口黑白名单：登录/注册/验证码放行，其余接口强制鉴权

- 数据行级隔离：用户仅可操作自身文档、问答数据

---

## 📦 项目打包部署

```bash
# Maven打包，跳过单元测试
mvn clean package -DskipTests

# 运行jar包
java -jar target/ai-doc-agent-0.0.1-SNAPSHOT.jar
```

---

## ❌ 常见问题\&故障排查

1. **问题**：访问8080/8083接口提示URL拼写错误

2. **原因**：对应端口服务未启动、端口占用、yml内python服务地址配置错误

3. **解决方案**：先启Python\(8080\)、后启Java\(8083\)，核对配置地址，关闭占用端口程序

4. **问题**：AI问答无返回

5. **解决方案**：检查星火AI密钥有效性、Python服务跨域配置、网络连通性

6. **问题**：文件上传失败

7. **解决方案**：核对yml文件上传大小配置、服务器磁盘读写权限

---

## 📄 开源许可

本项目基于 **MIT License**开源，支持学习毕设、内网私有化二次开发，商用请保留项目开源署名。

如有适配本地大模型、新增前端页面、接口改造需求，欢迎提交Issues联动开发❤️

> （注：部分内容可能由 AI 生成）
