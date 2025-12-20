package com.uoh.model.dto.bookBorrow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.uoh.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询借阅请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BookBorrowQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 借阅状态：0借阅中 1已归还 2逾期
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}