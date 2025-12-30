package com.uoh.model.dto.user;

import com.uoh.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户请求
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色：0普通用户 1管理员
     */
    private Integer role;

    /**
     * 状态：0禁用 1正常
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}