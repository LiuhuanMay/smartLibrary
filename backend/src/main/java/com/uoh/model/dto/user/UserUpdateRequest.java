package com.uoh.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户请求
 *
 */
@Data
public class UserUpdateRequest implements Serializable {

    private long id;

    private static final long serialVersionUID = 1L;
}