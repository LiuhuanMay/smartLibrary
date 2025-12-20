package com.uoh.model.vo;

import com.uoh.model.entity.Book;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 图书管理视图
 *
 */
@Data
public class BookVO implements Serializable {

    /**
     * 图书ID
     */
    private Long id;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 图书介绍
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
     * 总库存
     */
    private Integer totalStock;


    /**
     * 可借库存
     */
    private Integer availableStock;

    /**
     * 已借出数量
     */
    private Integer borrowedCount;

    /**
     * 累计借阅次数
     */
    private Integer totalBorrowedCount;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 语言
     */
    private String language;


    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     *
     * @param bookVO
     * @return
     */
    public static Book voToObj(BookVO bookVO) {
        if (bookVO == null) {
            return null;
        }
        Book book = new Book();
        BeanUtils.copyProperties(bookVO, book);
        return book;
    }

    /**
     * 对象转封装类
     *
     * @param book
     * @return
     */
    public static BookVO objToVo(Book book) {
        if (book == null) {
            return null;
        }
        BookVO bookVO = new BookVO();
        BeanUtils.copyProperties(book, bookVO);
        return bookVO;
    }
}
