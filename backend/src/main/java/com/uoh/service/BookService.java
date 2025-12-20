package com.uoh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.uoh.model.dto.book.BookQueryRequest;
import com.uoh.model.dto.book.BookUpdateRequest;
import com.uoh.model.entity.Book;
import com.uoh.model.vo.BookVO;


/**
 * 图书管理服务
 *
 */
public interface BookService extends IService<Book> {

    /**
     * 校验数据
     *
     * @param book
     * @param add 对创建的数据进行校验
     */
    void validBook(Book book, boolean add);

    /**
     * 获取查询条件
     *
     * @param bookQueryRequest
     * @return
     */
    QueryWrapper<Book> getQueryWrapper(BookQueryRequest bookQueryRequest);
    
    /**
     * 获取图书管理封装
     *
     * @return
     */
    BookVO getBookVO(Book book);

    /**
     * 分页获取图书管理封装
     *
     * @param bookPage
     * @return
     */
    Page<BookVO> getBookVOPage(Page<Book> bookPage);

    /**
     * 修改图书信息
     * @param oldBook
     * @param bookUpdateRequest
     */
    void updateBook(Book oldBook, BookUpdateRequest bookUpdateRequest);
}
