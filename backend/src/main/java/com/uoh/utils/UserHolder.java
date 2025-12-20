package com.uoh.utils;

public class UserHolder {

    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    // 设置 userId
    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    // 获取 userId
    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    // 清理，防止内存泄漏
    public static void remove() {
        userIdThreadLocal.remove();
    }
}

