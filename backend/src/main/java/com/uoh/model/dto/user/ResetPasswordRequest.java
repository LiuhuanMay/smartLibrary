package com.uoh.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResetPasswordRequest implements Serializable {

    /**
     *邮箱
     */
    private String email;


    /**
     * 密码
     */
    private String password;


    /**
     *
     * 确认密码
     */
    private String repeatPassword;


    /**
     *邮箱验证码
     */
    private String code;

    private static final long serialVersionUID = 1L;
}
