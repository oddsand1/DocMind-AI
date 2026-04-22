package com.agent.ai_doc_agent.common;

import lombok.Data;
//所有接口统一 用这个返回
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // 成功
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }
}