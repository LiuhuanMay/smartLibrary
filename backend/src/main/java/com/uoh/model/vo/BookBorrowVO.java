package com.uoh.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.uoh.model.entity.BookBorrow;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅视图
 *
 */
@Data
public class BookBorrowVO implements Serializable {

    /**
     * 借阅记录ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 用户名称
     */
    private String userName;

    /**
     * 图书名称
     */
    private String bookName;


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
     *审核状态
     */
    private Integer reviewStatus;

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
     * 封装类转对象
     *
     * @param bookBorrowVO
     * @return
     */
    public static BookBorrow voToObj(BookBorrowVO bookBorrowVO) {
        if (bookBorrowVO == null) {
            return null;
        }
        BookBorrow bookBorrow = new BookBorrow();
        BeanUtils.copyProperties(bookBorrowVO, bookBorrow);
        return bookBorrow;
    }

    /**
     * 对象转封装类
     *
     * @param bookBorrow
     * @return
     */
    public static BookBorrowVO objToVo(BookBorrow bookBorrow) {
        if (bookBorrow == null) {
            return null;
        }
        BookBorrowVO bookBorrowVO = new BookBorrowVO();
        BeanUtils.copyProperties(bookBorrow, bookBorrowVO);
        return bookBorrowVO;
    }
}
