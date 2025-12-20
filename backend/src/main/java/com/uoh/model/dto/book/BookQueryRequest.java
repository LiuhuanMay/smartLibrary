package com.uoh.model.dto.book;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.uoh.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询图书管理请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BookQueryRequest extends PageRequest implements Serializable {

    /**
     * 图书名称
     */
    private String bookName;


    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 出版日期
     */
    private Date publishDate;

    /**
     * 0：之前  1：之后
     */
    private Integer publishDateType;

    /**
     * 是否可借
     */
    private Integer availableStock;


    private static final long serialVersionUID = 1L;
}