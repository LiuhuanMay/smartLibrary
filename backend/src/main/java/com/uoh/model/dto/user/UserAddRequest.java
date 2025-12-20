package com.uoh.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建用户请求
 *
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}