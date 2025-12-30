package com.uoh.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 图书表
 * @TableName book
 */
@TableName(value ="book")
@Data
public class Book implements Serializable {
    /**
     * 图书ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 图书名称
     */
    private String bookName;


    /**
     *图书介绍
     */
    private String bookIntroduction;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
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
     * 可借库存
     */
    private Integer availableStock;

    /**
     * 已经借出去的数量
     */
    private Integer borrowedCount;

    /**
     *累计借阅次数
     */
    private Integer totalBorrowedCount;

    /**
     * 图书状态：0下架 1上架
     */
    private Integer status;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 语言
     */
    private String language;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}