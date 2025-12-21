package com.uoh.model.dto.bookBorrow;

import lombok.Data;

import java.io.Serializable;


@Data
public class BookReturnRequest implements Serializable {

    /**
     * 借阅Id
     */
    private Long bookBorrowId;

    private static final long serialVersionUID = 1L;
}
