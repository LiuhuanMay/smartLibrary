import {http} from "@/utils/http.js";

/**
 * 用户登录
 * @param data
 * @returns {*}
 */
export const userLogin = (data) =>{
    return http.post("/auth/login",data)
}

/**
 *用户注册
 * @param data
 * @returns {*}
 */
export const userRegister = (data) =>{
    return http.post("/auth/register",data)
}


/**
 * 获取当前登录用户
 * @param data
 * @returns {*}
 */
export const getCurrentUser = (data) =>{
    return http.post("/auth/currentUser",data)
}

/**
 * 注册发送验证码
 * @param data
 * @returns {*}
 */
export const sendRegisterCode = (data) =>{
    return http.post("/auth/sendCode",data)
}

/**
 * 忘记密码发送验证码
 * @param data
 * @returns {*}
 */
export const sendResetCode = (data) =>{
    return http.post("/auth/sendResetCode",data)
}




/**
 *用户忘记密码
 * @param data
 * @returns {*}
 */
export const forgotPassword = (data) =>{
    return http.post("/auth/forgotPassword",data)
}
