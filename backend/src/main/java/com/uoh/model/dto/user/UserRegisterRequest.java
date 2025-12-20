package com.uoh.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {


    /**
     * 手机号（登录账号）
     */
    private String phone;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;


    /**
     *邮箱验证码
     */
    private String code;

    private static final long serialVersionUID = 1L;
}
