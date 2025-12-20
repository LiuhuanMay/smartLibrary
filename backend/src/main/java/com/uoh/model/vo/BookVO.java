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
     * 可借库存
     */
    private Integer availableStock;

    /**
     * 累计借阅次数
     */
    private Integer borrowCount;

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
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


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
