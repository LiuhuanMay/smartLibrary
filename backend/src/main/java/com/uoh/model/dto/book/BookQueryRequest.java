package com.uoh.model.dto.book;

import com.uoh.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询图书管理请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BookQueryRequest extends PageRequest implements Serializable {


    private static final long serialVersionUID = 1L;
}