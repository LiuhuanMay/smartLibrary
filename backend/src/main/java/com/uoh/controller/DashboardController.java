package com.uoh.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uoh.common.BaseResponse;
import com.uoh.common.ResultUtils;
import com.uoh.model.entity.Book;
import com.uoh.model.entity.BookBorrow;
import com.uoh.model.entity.User;
import com.uoh.service.BookBorrowService;
import com.uoh.service.BookService;
import com.uoh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "数据可视化面板")
public class DashboardController {

    @Resource
    private UserService userService;

    @Resource
    private BookService bookService;

    @Resource
    private BookBorrowService bookBorrowService;

    @GetMapping("/summary")
    @Operation(summary = "获取面板基础统计数据")
    public BaseResponse<Map<String, Object>> getSummaryData() {
        Map<String, Object> result = new HashMap<>();

        // 1. 总注册用户
        long totalUsers = userService.count();
        result.put("totalUsers", totalUsers);

        // 2. 馆藏图书总量 (汇总 book 表的 total_stock)
        QueryWrapper<Book> bookQw = new QueryWrapper<>();
        bookQw.select("IFNULL(SUM(total_stock), 0) as totalStock");
        Map<String, Object> bookMap = bookService.getMap(bookQw);
        long totalBooks = 0;
        if (bookMap != null && bookMap.get("totalStock") != null) {
            totalBooks = Long.parseLong(bookMap.get("totalStock").toString());
        }
        result.put("totalBooks", totalBooks);

        // 3. 当前借出中 (借阅状态：1 借阅中)
        QueryWrapper<BookBorrow> borrowQw = new QueryWrapper<>();
        borrowQw.eq("status", 1);
        long borrowingCount = bookBorrowService.count(borrowQw);
        result.put("borrowingCount", borrowingCount);

        // 4. 逾期未归还 (借阅状态：3 逾期)
        QueryWrapper<BookBorrow> overdueQw = new QueryWrapper<>();
        overdueQw.eq("status", 3);
        long overdueCount = bookBorrowService.count(overdueQw);
        result.put("overdueCount", overdueCount);

        return ResultUtils.success(result);
    }

    @GetMapping("/trend")
    @Operation(summary = "获取最近7日借阅趋势")
    public BaseResponse<Map<String, Object>> getBorrowTrend() {
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            String dateStr = DateUtil.offsetDay(new Date(), -i).toString("yyyy-MM-dd");
            dates.add(DateUtil.offsetDay(new Date(), -i).toString("MM-dd"));
            
            QueryWrapper<BookBorrow> qw = new QueryWrapper<>();
            qw.apply("DATE(create_time) = {0}", dateStr);
            long count = bookBorrowService.count(qw);
            
            counts.add(count);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);

        return ResultUtils.success(result);
    }

    @GetMapping("/hot-books")
    @Operation(summary = "获取热门图书借阅 TOP 5")
    public BaseResponse<Map<String, Object>> getHotBooks() {
        QueryWrapper<Book> qw = new QueryWrapper<>();
        qw.select("book_name", "total_borrowed_count")
          .orderByDesc("total_borrowed_count")
          .last("LIMIT 5");
        
        List<Book> hotBooks = bookService.list(qw);
        
        List<String> bookNames = new ArrayList<>();
        List<Integer> borrowCounts = new ArrayList<>();
        
        // 由于是倒序排列（最多的在最前面），为了柱状图从下往上显示，我们可以反转一下或者直接使用，前端 ECharts 会从下往上画
        Collections.reverse(hotBooks);
        
        for (Book book : hotBooks) {
            bookNames.add(book.getBookName());
            borrowCounts.add(book.getTotalBorrowedCount() != null ? book.getTotalBorrowedCount() : 0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookNames", bookNames);
        result.put("borrowCounts", borrowCounts);

        return ResultUtils.success(result);
    }
}
