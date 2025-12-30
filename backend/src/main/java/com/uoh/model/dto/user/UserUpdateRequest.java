package com.uoh.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新用户请求
 *
 */
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long id;


    /**
     * 手机号（登录账号）
     */
    private String phone;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别：0未知 1男 2女
     */
    private Integer gender;

    /**
     * 角色：0普通用户 1管理员
     */
    private Integer role;

    /**
     * 状态：0禁用 1正常
     */
    private Integer status;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}