package com.uoh.ai.tools;


import com.uoh.common.BaseResponse;
import com.uoh.controller.BookBorrowController;
import com.uoh.model.dto.bookBorrow.BookBorrowAddRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
@Slf4j
public class BookBorrowTool {

    @Resource
    private BookBorrowController bookBorrowController;

    // description 越详细，AI 越知道何时调用
    @Tool(description = "执行借书操作。必须在已知书籍ID（bookId）的情况下才能调用此工具。")
    public String bookBorrow(@ToolParam(description = "书籍的唯一标识ID，通常是从搜索结果中获取的数字字符串") String bookId) {
        log.info("触发借书工具，BookID: {}", bookId);
        try {
            log.info("触发借书工具，BookID: {}", bookId);
            BookBorrowAddRequest request = new BookBorrowAddRequest();
            request.setBookId(Long.valueOf(bookId));

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            request.setDueTime(calendar.getTime());
            BaseResponse<Long> longBaseResponse = bookBorrowController.addBookBorrow(request);
            if(longBaseResponse.getCode()!=0){
                return "库存不足，借书失败";
            }else{
                return "借书成功，请再7日类按时归还图书";
            }
        } catch (Exception e) {
            return "借书操作发生异常：" + e.getMessage();
        }

    }
}
