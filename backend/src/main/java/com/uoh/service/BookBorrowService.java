package com.uoh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.uoh.model.dto.bookBorrow.BookBorrowAddRequest;
import com.uoh.model.dto.bookBorrow.BookBorrowQueryRequest;
import com.uoh.model.dto.bookBorrow.BookReturnRequest;
import com.uoh.model.dto.bookBorrow.ReviewBookBorrowRequest;
import com.uoh.model.entity.BookBorrow;
import com.uoh.model.vo.BookBorrowVO;


/**
 * 借阅服务
 *
 */
public interface BookBorrowService extends IService<BookBorrow> {

    /**
     * 校验数据
     *
     * @param bookBorrow
     * @param add 对创建的数据进行校验
     */
    void validBookBorrow(BookBorrow bookBorrow, boolean add);

    /**
     * 获取查询条件
     *
     * @param bookBorrowQueryRequest
     * @return
     */
    QueryWrapper<BookBorrow> getQueryWrapper(BookBorrowQueryRequest bookBorrowQueryRequest);
    
    /**
     * 获取借阅封装
     *
     * @return
     */
    BookBorrowVO getBookBorrowVO(BookBorrow bookBorrow);

    /**
     * 分页获取借阅封装
     *
     * @param bookBorrowPage
     * @return
     */
    Page<BookBorrowVO> getBookBorrowVOPage(Page<BookBorrow> bookBorrowPage);


    /**
     *借阅读书
     *
     * @param request
     * @param userId
     * @return
     */
    Long borrowBook(BookBorrowAddRequest request, Long userId);


    /**
     * 归还图书
     *
     * @param bookReturnRequest
     */
    void bookReturn(BookReturnRequest bookReturnRequest);


    /**
     * 管理员审核
     * @param reviewBookBorrowRequest
     */
    void ReviewBookBorrow(ReviewBookBorrowRequest reviewBookBorrowRequest);
}
