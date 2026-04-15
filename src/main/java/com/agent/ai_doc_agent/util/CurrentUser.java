package com.agent.ai_doc_agent.util;

public class CurrentUser {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    //在拦截器类从token中提取userid之后再传递进来
    public static void set(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get(); // 这里可能返回 null
    }

    public static void clear() {
        USER_ID.remove();
    }
}