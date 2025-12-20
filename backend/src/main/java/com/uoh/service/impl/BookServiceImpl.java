package com.uoh.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uoh.common.ErrorCode;
import com.uoh.exception.BusinessException;
import com.uoh.exception.ThrowUtils;
import com.uoh.mapper.BookMapper;
import com.uoh.model.dto.book.BookQueryRequest;
import com.uoh.model.dto.book.BookUpdateRequest;
import com.uoh.model.entity.Book;
import com.uoh.model.vo.BookVO;
import com.uoh.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书管理服务实现
 *
 */
@Service
@Slf4j
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {




    /**
     * 校验数据
     *
     * @param book
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBook(Book book, boolean add) {
        ThrowUtils.throwIf(book == null, ErrorCode.PARAMS_ERROR);


    }

    /**
     * 获取查询条件
     *
     * @param bookQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Book> getQueryWrapper(BookQueryRequest bookQueryRequest) {

        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();

        if (bookQueryRequest == null) {
            return queryWrapper;
        }

        // 图书名称
        if (bookQueryRequest.getBookName() != null
                && !bookQueryRequest.getBookName().trim().isEmpty()) {
            queryWrapper.like("book_name", bookQueryRequest.getBookName());
        }

        // 作者
        if (bookQueryRequest.getAuthor() != null
                && !bookQueryRequest.getAuthor().trim().isEmpty()) {
            queryWrapper.like("author", bookQueryRequest.getAuthor());
        }

        // 出版社
        if (bookQueryRequest.getPublisher() != null
                && !bookQueryRequest.getPublisher().trim().isEmpty()) {
            queryWrapper.like("publisher", bookQueryRequest.getPublisher());
        }

        // 是否可借
        if (bookQueryRequest.getAvailableStock() != null) {
            if (bookQueryRequest.getAvailableStock() == 1) {
                queryWrapper.gt("available_stock", 0);
            } else {
                queryWrapper.eq("available_stock", 0);
            }
        }

        // 出版日期：之前 / 之后
        if (bookQueryRequest.getPublishDate() != null
                && bookQueryRequest.getPublishDateType() != null) {

            if (bookQueryRequest.getPublishDateType() == 0) {
                // 之前
                queryWrapper.lt("publish_date", bookQueryRequest.getPublishDate());
            } else {
                // 之后
                queryWrapper.gt("publish_date", bookQueryRequest.getPublishDate());
            }
        }

        return queryWrapper;
    }




    /**
     * 获取图书管理封装
     *
     * @param book
     * @return
     */
    @Override
    public BookVO getBookVO(Book book) {
        // 对象转封装类
        BookVO bookVO = BookVO.objToVo(book);
        return bookVO;
    }

    /**
     * 分页获取图书管理封装
     *
     * @param bookPage
     * @return
     */
    @Override
    public Page<BookVO> getBookVOPage(Page<Book> bookPage) {
        List<Book> bookList = bookPage.getRecords();
        Page<BookVO> bookVOPage = new Page<>(bookPage.getCurrent(), bookPage.getSize(), bookPage.getTotal());
        if (CollUtil.isEmpty(bookList)) {
            return bookVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BookVO> bookVOList = bookList.stream().map(book -> {
            return BookVO.objToVo(book);
        }).collect(Collectors.toList());

        bookVOPage.setRecords(bookVOList);
        return bookVOPage;
    }

    @Override
    @Transactional
    public void updateBook(Book oldBook, BookUpdateRequest request) {

        Integer newTotalStock = request.getTotalStock();
        Integer oldTotalStock = oldBook.getTotalStock();

        Book book = new Book();
        BeanUtils.copyProperties(request, book);
        book.setId(oldBook.getId());

        // 库存未传 或 库存未变化
        if (newTotalStock == null || newTotalStock.equals(oldTotalStock)) {
            book.setUpdateTime(new Date());
            this.updateById(book);
            return;
        }

        // 库存校验
        if (newTotalStock < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存总额不能为负数");
        }

        Integer borrowedCount = oldBook.getBorrowedCount();
        if (newTotalStock < borrowedCount) {
            throw new BusinessException(
                    ErrorCode.PARAMS_ERROR,
                    String.format("库存总额不能小于已借出数量（已借出：%d）", borrowedCount)
            );
        }

        // 重新计算可用库存
        book.setTotalStock(newTotalStock);
        book.setAvailableStock(newTotalStock - borrowedCount);
        book.setUpdateTime(new Date());

        this.updateById(book);
    }




}
