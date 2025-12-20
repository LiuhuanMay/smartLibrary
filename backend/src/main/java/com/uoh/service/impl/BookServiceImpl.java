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
    public void updateBook(Book oldBook, BookUpdateRequest bookUpdateRequest) {

        Integer newTotalStock = bookUpdateRequest.getTotalStock();
        Integer oldTotalStock = oldBook.getTotalStock();

        Book book = new Book();
        BeanUtils.copyProperties(bookUpdateRequest, book);

        // 用户没有传库存 or 库存未变化，直接更新其他字段
        if (newTotalStock == null || newTotalStock.equals(oldTotalStock)) {
            // 保留原可借库存
            book.setAvailableStock(oldBook.getAvailableStock());
            this.updateById(book);
            return;
        }

        // 当“库存发生变化”时才执行
        if (newTotalStock < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存总额不能为负数");
        }

        Integer borrowCount = oldBook.getBorrowCount();

        if (newTotalStock < borrowCount) {
            throw new BusinessException(
                    ErrorCode.PARAMS_ERROR,
                    String.format("库存总额不能小于已借出数量（已借出：%d）", borrowCount)
            );
        }

        // 核心公式（安全 & 高性能）
        int newAvailableStock = newTotalStock - borrowCount;
        book.setAvailableStock(newAvailableStock);
        this.updateById(book);
    }



}
