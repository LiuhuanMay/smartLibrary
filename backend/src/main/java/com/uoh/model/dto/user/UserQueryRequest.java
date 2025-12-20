package com.uoh.model.dto.user;

import com.uoh.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {


    private static final long serialVersionUID = 1L;
}