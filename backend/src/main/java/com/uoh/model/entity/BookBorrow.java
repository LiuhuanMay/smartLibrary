package com.uoh.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录表
 * @TableName book_borrow
 */
@TableName(value ="book_borrow")
@Data
public class BookBorrow implements Serializable {
    /**
     * 借阅记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}