package com.uoh.model.dto.book;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 更新图书管理请求
 *
 */
@Data
public class BookUpdateRequest implements Serializable {

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
     * 逾期天数
     */
    private Integer overdueDays;

    private static final long serialVersionUID = 1L;
}