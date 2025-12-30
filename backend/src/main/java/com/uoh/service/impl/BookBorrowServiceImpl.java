package com.uoh.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uoh.common.ErrorCode;
import com.uoh.exception.BusinessException;
import com.uoh.exception.ThrowUtils;
import com.uoh.manager.EmailManager;
import com.uoh.mapper.BookBorrowMapper;
import com.uoh.model.dto.bookBorrow.BookBorrowAddRequest;
import com.uoh.model.dto.bookBorrow.BookBorrowQueryRequest;
import com.uoh.model.dto.bookBorrow.BookReturnRequest;
import com.uoh.model.dto.bookBorrow.ReviewBookBorrowRequest;
import com.uoh.model.entity.Book;
import com.uoh.model.entity.BookBorrow;
import com.uoh.model.entity.User;
import com.uoh.model.vo.BookBorrowVO;
import com.uoh.service.BookBorrowService;
import com.uoh.service.BookService;
import com.uoh.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 借阅服务实现
 *
 */
@Service
@Slf4j
public class BookBorrowServiceImpl extends ServiceImpl<BookBorrowMapper, BookBorrow> implements BookBorrowService {

    @Resource
    private BookService bookService;

    @Resource
    private UserService userService;


    @Resource
    private EmailManager emailManager;



    /**
     * 校验数据
     *
     * @param bookBorrow
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBookBorrow(BookBorrow bookBorrow, boolean add) {
        ThrowUtils.throwIf(bookBorrow == null, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 获取查询条件
     *
     * @param bookBorrowQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<BookBorrow> getQueryWrapper(BookBorrowQueryRequest bookBorrowQueryRequest) {
        QueryWrapper<BookBorrow> queryWrapper = new QueryWrapper<>();
        if (bookBorrowQueryRequest == null) {
            return queryWrapper;
        }
        Long userId = bookBorrowQueryRequest.getUserId();
        if(!(bookBorrowQueryRequest.getUserId() ==null)){
            queryWrapper.eq("user_id",userId);
        }
        Integer status = bookBorrowQueryRequest.getStatus();
        if(!(bookBorrowQueryRequest.getStatus()==null)){
            queryWrapper.eq("status",status);
        }
        return queryWrapper;
    }

    /**
     * 获取借阅封装
     *
     * @param bookBorrow
     * @return
     */
    @Override
    public BookBorrowVO getBookBorrowVO(BookBorrow bookBorrow) {
        // 对象转封装类
        BookBorrowVO bookBorrowVO = BookBorrowVO.objToVo(bookBorrow);
        return bookBorrowVO;
    }

    /**
     * 分页获取借阅封装
     *
     * @param bookBorrowPage
     * @return
     */
    @Override
    public Page<BookBorrowVO> getBookBorrowVOPage(Page<BookBorrow> bookBorrowPage) {
        List<BookBorrow> bookBorrowList = bookBorrowPage.getRecords();
        Page<BookBorrowVO> bookBorrowVOPage = new Page<>(bookBorrowPage.getCurrent(), bookBorrowPage.getSize(), bookBorrowPage.getTotal());
        if (CollUtil.isEmpty(bookBorrowList)) {
            return bookBorrowVOPage;
        }

        Set<Long> userIds = bookBorrowList.stream()
                .map(BookBorrow::getUserId)
                .collect(Collectors.toSet());

        Set<Long> bookIds = bookBorrowList.stream()
                .map(BookBorrow::getBookId)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, Book> bookMap = bookService.listByIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        // 对象列表 => 封装对象列表
        List<BookBorrowVO> bookBorrowVOList = bookBorrowList.stream()
                .map(bookBorrow -> {
                    BookBorrowVO vo = BookBorrowVO.objToVo(bookBorrow);

                    User user = userMap.get(bookBorrow.getUserId());
                    Book book = bookMap.get(bookBorrow.getBookId());

                    if (user != null) {
                        vo.setUserName(user.getNickname());
                    }
                    if (book != null) {
                        vo.setBookName(book.getBookName());
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        bookBorrowVOPage.setRecords(bookBorrowVOList);
        return bookBorrowVOPage;
    }

    @Override
    @Transactional
    public Long borrowBook(BookBorrowAddRequest request, Long userId) {
        // 1. 参数校验
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        Long bookId = request.getBookId();
        Date dueTime = request.getDueTime();

        // 2. 查询图书
        Book book = bookService.getById(bookId);
        ThrowUtils.throwIf(book == null,
                ErrorCode.NOT_FOUND_ERROR, "当前图书不存在");

        // 3. 库存校验
        ThrowUtils.throwIf(book.getAvailableStock() <= 0,
                ErrorCode.OPERATION_ERROR, "当前库存不足");

        // 4. 构建借阅记录
        BookBorrow bookBorrow = new BookBorrow();
        bookBorrow.setBookId(bookId);
        bookBorrow.setUserId(userId);
        bookBorrow.setDueTime(dueTime);
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        bookBorrow.setBorrowTime(currentDate);

        // 5. 业务校验
        this.validBookBorrow(bookBorrow, true);

        // 6. 保存借阅记录
        boolean saved = this.save(bookBorrow);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);

        // 7. 更新图书库存
        //可用库存-1
        book.setAvailableStock(book.getAvailableStock() - 1);
        //已经借出去的数量+1
        book.setBorrowedCount(book.getBorrowedCount() + 1);
        //累计借出去的数量+1
        book.setTotalBorrowedCount(book.getTotalBorrowedCount()+1);
        bookService.updateById(book);

        // 8. 返回借阅记录 id
        return bookBorrow.getId();
    }

    @Override
    @Transactional
    public void bookReturn(BookReturnRequest bookReturnRequest) {
        Long bookBorrowId = bookReturnRequest.getBookBorrowId();
        BookBorrow bookBorrow = this.getById(bookBorrowId);
        Long bookId = bookBorrow.getBookId();
        Book book = bookService.getById(bookId);
        if(book==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有该图书");
        }
        bookBorrow.setStatus(2);
        bookBorrow.setReviewStatus(0);
        bookBorrow.setReturnTime(new Date());
        //删除借阅记录
        this.updateById(bookBorrow);
        //更新可用库存
        book.setAvailableStock(book.getAvailableStock()+1);
        book.setBorrowedCount(book.getBorrowedCount()-1);
        bookService.updateById(book);
    }

    @Override
    public void ReviewBookBorrow(ReviewBookBorrowRequest reviewBookBorrowRequest) {
        Long bookBorrowId = reviewBookBorrowRequest.getBookBorrowId();
        Integer reviewStatus = reviewBookBorrowRequest.getReviewStatus();
        BookBorrow bookBorrow = this.getById(bookBorrowId);
        if(bookBorrow==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有该借阅记录");
        }
        Long userId = bookBorrow.getUserId();
        User user = userService.getById(userId);
        if(user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未找到该用户");
        }
        String email = user.getEmail();
        bookBorrow.setReviewStatus(reviewStatus);
        boolean b = this.updateById(bookBorrow);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        emailManager.sendBookBorrowReviewSimpleNotice(email);
    }

}
