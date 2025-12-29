package com.uoh.controller;

import com.uoh.ai.client.BorrowChatClient;
import com.uoh.common.BaseResponse;
import com.uoh.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI相关")
@Slf4j
public class AIController {

    @Resource
    private BorrowChatClient borrowChatClient;

    @GetMapping("/chat")
    @Operation(summary = "对话式操作")
    public BaseResponse<String> chat(@RequestParam String message, @RequestParam String chatId){
        log.info("用户[ID:{}]输入: {}", chatId, message);
        String response = borrowChatClient.doChat(message, chatId);
        return ResultUtils.success(response);
    }

    @GetMapping("/history")
    @Operation(summary = "对话式操作")
    public BaseResponse<List<Message>> history(@RequestParam String chatId){
        List<Message> history = borrowChatClient.getHistory(chatId);
        return ResultUtils.success(history);
    }




}
