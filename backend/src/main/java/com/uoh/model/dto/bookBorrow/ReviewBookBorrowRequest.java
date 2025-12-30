package com.uoh.model.dto.bookBorrow;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewBookBorrowRequest implements Serializable {


    /**
     *
     * 借阅Id
     */
    private Long bookBorrowId;



    /**
     *
     * 审核状态
     */
    private Integer reviewStatus;

    private static final long serialVersionUID = 1L;
}
