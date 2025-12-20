package com.uoh.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uoh.common.BaseResponse;
import com.uoh.common.DeleteRequest;
import com.uoh.common.ErrorCode;
import com.uoh.common.ResultUtils;
import com.uoh.exception.BusinessException;
import com.uoh.exception.ThrowUtils;
import com.uoh.model.dto.book.BookAddRequest;
import com.uoh.model.dto.book.BookQueryRequest;
import com.uoh.model.dto.book.BookUpdateRequest;
import com.uoh.model.entity.Book;
import com.uoh.model.vo.BookVO;
import com.uoh.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 图书管理接口
 *
 */
@RestController
@RequestMapping("/book")
@Slf4j
@Tag(name = "图书管理")
public class BookController {

    @Resource
    private BookService bookService;


    // region 增删改查

    /**
     * 创建图书管理
     *
     * @param bookAddRequest
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增图书")
    public BaseResponse<Long> addBook(@RequestBody BookAddRequest bookAddRequest) {
        ThrowUtils.throwIf(bookAddRequest == null, ErrorCode.PARAMS_ERROR);
        Book book = new Book();
        BeanUtils.copyProperties(bookAddRequest, book);
        // 数据校验
        bookService.validBook(book, true);
        // 初始时候的可用库存等于总库存
        book.setAvailableStock(book.getTotalStock());
        // 写入数据库
        boolean result = bookService.save(book);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 id
        long newBookId = book.getId();
        return ResultUtils.success(newBookId);
    }

    /**
     * 删除图书管理
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除图书")
    public BaseResponse<Boolean> deleteBook(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Book oldBook = bookService.getById(id);
        ThrowUtils.throwIf(oldBook == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = bookService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新图书管理
     *
     * @param bookUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @Operation(summary = "更新图书")
    public BaseResponse<Boolean> updateBook(@RequestBody BookUpdateRequest bookUpdateRequest) {
        if (bookUpdateRequest == null || bookUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Book newbook = new Book();
        BeanUtils.copyProperties(bookUpdateRequest, newbook);
        // 数据校验
        bookService.validBook(newbook, false);
        // 判断是否存在
        long id = bookUpdateRequest.getId();
        Book oldBook = bookService.getById(id);
        ThrowUtils.throwIf(oldBook == null, ErrorCode.NOT_FOUND_ERROR);
        bookService.updateBook(oldBook,bookUpdateRequest);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取图书管理（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据id查找图书")
    public BaseResponse<BookVO> getBookVOById(@RequestParam ("id") long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Book book = bookService.getById(id);
        ThrowUtils.throwIf(book == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(bookService.getBookVO(book));
    }



    /**
     * 分页获取图书管理列表（封装类）
     *
     * @param bookQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取图书")
    public BaseResponse<Page<BookVO>> listBookVOByPage(@RequestBody BookQueryRequest bookQueryRequest) {
        long current = bookQueryRequest.getCurrentPage();
        long size = bookQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Book> bookPage = bookService.page(new Page<>(current, size),
                bookService.getQueryWrapper(bookQueryRequest));
        // 获取封装类
        return ResultUtils.success(bookService.getBookVOPage(bookPage));
    }

    // endregion
}
