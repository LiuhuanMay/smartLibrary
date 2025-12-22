package com.uoh.model.dto.bookBorrow;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建借阅请求
 *
 */
@Data
public class BookBorrowAddRequest implements Serializable {



    /**
     * 图书ID
     */
    private Long bookId;


    /**
     * 应还时间
     */
    private Date dueTime;




    private static final long serialVersionUID = 1L;
}