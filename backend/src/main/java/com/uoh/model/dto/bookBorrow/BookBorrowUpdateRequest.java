package com.uoh.model.dto.bookBorrow;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新借阅请求
 *
 */
@Data
public class BookBorrowUpdateRequest implements Serializable {

    /**
     * 借阅记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 借阅时间
     */
    private Date borrowTime;

    /**
     * 应还时间
     */
    private Date dueTime;

    /**
     * 实际归还时间
     */
    private Date returnTime;

    /**
     * 借阅状态：0借阅中 1已归还 2逾期
     */
    private Integer status;

    /**
     *
     * 管理员审核状态
     */
    private Integer reviewStatus;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}