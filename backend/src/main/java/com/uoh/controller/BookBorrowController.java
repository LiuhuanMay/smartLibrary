package com.uoh.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uoh.common.BaseResponse;
import com.uoh.common.ErrorCode;
import com.uoh.common.ResultUtils;
import com.uoh.exception.BusinessException;
import com.uoh.exception.ThrowUtils;
import com.uoh.model.dto.bookBorrow.BookBorrowAddRequest;
import com.uoh.model.dto.bookBorrow.BookBorrowQueryRequest;
import com.uoh.model.dto.bookBorrow.BookBorrowUpdateRequest;
import com.uoh.model.entity.BookBorrow;
import com.uoh.model.vo.BookBorrowVO;
import com.uoh.service.BookBorrowService;
import com.uoh.utils.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 借阅接口
 *
 */
@RestController
@RequestMapping("/bookBorrow")
@Slf4j
@Tag(name = "借阅管理")
public class BookBorrowController {

    @Resource
    private BookBorrowService bookBorrowService;
    

    // region 增删改查

    /**
     * 创建借阅
     *
     * @param request
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增借阅")
    public BaseResponse<Long> addBookBorrow(@RequestBody BookBorrowAddRequest request) {
        // 获取当前登录用户 id
        Long userId = UserHolder.getUserId();

        // 调用 Service 完成业务
        Long borrowId = bookBorrowService.borrowBook(request, userId);

        // 返回结果
        return ResultUtils.success(borrowId);
    }



    /**
     * 更新借阅
     *
     * @param bookBorrowUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @Operation(summary = "修改借阅")
    public BaseResponse<Boolean> updateBookBorrow(@RequestBody BookBorrowUpdateRequest bookBorrowUpdateRequest) {
        if (bookBorrowUpdateRequest == null || bookBorrowUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BookBorrow bookBorrow = new BookBorrow();
        BeanUtils.copyProperties(bookBorrowUpdateRequest, bookBorrow);
        // 数据校验
        bookBorrowService.validBookBorrow(bookBorrow, false);
        // 判断是否存在
        long id = bookBorrowUpdateRequest.getId();
        BookBorrow oldBookBorrow = bookBorrowService.getById(id);
        ThrowUtils.throwIf(oldBookBorrow == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = bookBorrowService.updateById(bookBorrow);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取当前登录用户借阅列表（封装类）
     *
     * @param bookBorrowQueryRequest
     * @return
     */
    @PostMapping("/myList/page/vo")
    @Operation(summary = "分页获取当前用户的借阅列表")
    public BaseResponse<Page<BookBorrowVO>> myListBookBorrowVOByPage(@RequestBody BookBorrowQueryRequest bookBorrowQueryRequest) {
        Long userId = UserHolder.getUserId();
        bookBorrowQueryRequest.setUserId(userId);
        long current = bookBorrowQueryRequest.getCurrentPage();
        long size = bookBorrowQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BookBorrow> bookBorrowPage = bookBorrowService.page(new Page<>(current, size),
                bookBorrowService.getQueryWrapper(bookBorrowQueryRequest));
        // 获取封装类
        return ResultUtils.success(bookBorrowService.getBookBorrowVOPage(bookBorrowPage));
    }

    /**
     * 分页获取借阅列表（封装类）
     *
     * @param bookBorrowQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取借阅列表")
    public BaseResponse<Page<BookBorrowVO>> listBookBorrowVOByPage(@RequestBody BookBorrowQueryRequest bookBorrowQueryRequest) {
        long current = bookBorrowQueryRequest.getCurrentPage();
        long size = bookBorrowQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BookBorrow> bookBorrowPage = bookBorrowService.page(new Page<>(current, size),
                bookBorrowService.getQueryWrapper(bookBorrowQueryRequest));
        // 获取封装类
        return ResultUtils.success(bookBorrowService.getBookBorrowVOPage(bookBorrowPage));
    }
    // endregion


}
