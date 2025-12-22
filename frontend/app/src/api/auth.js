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
 * 用户注册
 * =========================
 * @description 新用户注册账号
 * @param {Object} data
 * @param {string} data.phone    手机号（登录账号）
 * @param {string} data.password 加密后的密码
 * @param {string} data.email    邮箱地址
 * @param {string} data.code     邮箱验证码
 * @returns {Promise}
 */
export const userRegister = (data) => {
    return http.post("/auth/register", data);
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

/**
 * =========================
 * 注册发送邮箱验证码
 * =========================
 * @description 注册时向邮箱发送验证码
 * @param {Object} data
 * @param {string} data.email 邮箱地址
 * @returns {Promise}
 */
export const sendRegisterCode = (data) => {
    return http.get("/auth/sendCode", data);
};

/**
 * =========================
 * 忘记密码发送邮箱验证码
 * =========================
 * @description 找回密码时向邮箱发送验证码
 * @param {Object} data
 * @param {string} data.email 邮箱地址
 * @returns {Promise}
 */
export const sendResetCode = (data) => {
    return http.get("/auth/sendResetCode", data);
};

/**
 * =========================
 * 用户忘记密码 / 重置密码
 * =========================
 * @description 通过邮箱验证码重置用户密码
 * @param {Object} data
 * @param {string} data.email          邮箱地址
 * @param {string} data.password       新密码
 * @param {string} data.repeatPassword 确认新密码
 * @param {string} data.code           邮箱验证码
 * @returns {Promise}
 */
export const forgotPassword = (data) => {
    return http.post("/auth/forgotPassword", data);
};
