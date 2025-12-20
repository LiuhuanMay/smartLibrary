package com.uoh.model.dto.book;

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
     * 主键id
     */
    private long id;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 图书封面URL
     */
    private String cover;

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
     * ISBN（可为空，但唯一）
     */
    private String isbn;

    /**
     * 总库存
     */
    private Integer totalStock;


    /**
     * 图书状态：0下架 1上架
     */
    private Integer status = 0;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 语言
     */
    private String language;



    private static final long serialVersionUID = 1L;
}