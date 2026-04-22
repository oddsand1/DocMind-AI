package com.agent.ai_doc_agent.exception;

public class BusinessException extends RuntimeException {
    private int code; // 错误码   （如404、401、403等）

    public BusinessException(int code, String message) {
        super(message);// 调用父类构造方法，存储异常信息
        this.code = code;// 存储错误码
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500; // 默认错误码  500：后端代码错误
    }

    public int getCode() {
        return code;
    }
}