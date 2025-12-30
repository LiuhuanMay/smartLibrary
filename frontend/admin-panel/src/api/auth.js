import { http } from "@/utils/http.js";

/**
 * =========================
 * 用户登录
 * =========================
 * @description 使用手机号 + 密码进行登录
 * @param {Object} data
 * @param {string} data.phone    手机号（登录账号）
 * @param {string} data.password 加密后的密码
 * @returns {Promise}
 */
export const userLogin = (data) => {
    return http.post("/auth/login", data);
};



/**
 * =========================
 * 获取当前登录用户信息
 * =========================
 * @description 获取当前登录态下的用户基本信息
 * @returns {Promise}
 */
export const getCurrentUser = () => {
    return http.get("/auth/currentUser");
};