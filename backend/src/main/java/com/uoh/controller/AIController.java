package com.uoh.controller;

import com.uoh.ai.LibraryChatAgent;
import com.uoh.common.BaseResponse;
import com.uoh.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI相关")
@Slf4j
public class AIController {

    @Resource
    private LibraryChatAgent libraryChatAgent;

    @Resource
    private ChatMemory inMemoryBorrowChatMemory;

    @Resource
    private ChatMemory inMemoryPlatformChatMemory;



    public record ChatRequest(
            String message,
            String chatId
    ) {}


    public record ChatIdRequest(String chatId) {}

    @PostMapping("/chat")
    @Operation(summary = "对话")
    public BaseResponse<String> chat(@RequestBody ChatRequest chatRequest) {
        // Record 的访问器直接使用字段名作为方法名
        String message = chatRequest.message();
        String chatId = chatRequest.chatId();

        log.info("用户[ID:{}]输入: {}", chatId, message);

        String response = libraryChatAgent.doChat(message, chatId);
        return ResultUtils.success(response);
    }

    @PostMapping("/getBorrowHistory")
    @Operation(summary = "借阅对话历史")
    public BaseResponse<List<Message>> getBorrowHistory(@RequestParam("chatId") String chatId) {
        return ResultUtils.success(inMemoryBorrowChatMemory.get(chatId));
    }

    @PostMapping("/getPlatformHistory")
    @Operation(summary = "平台对话历史")
    public BaseResponse<List<Message>> getPlatformHistory(@RequestParam("chatId") String chatId) {
        return ResultUtils.success(inMemoryPlatformChatMemory.get(chatId));
    }

    @PostMapping("/clearBorrowHistory")
    @Operation(summary = "清空借阅对话历史")
    public BaseResponse<Boolean> clearBorrowHistory(@RequestBody ChatIdRequest request) {
        // 从 record 中获取 chatId
        inMemoryBorrowChatMemory.clear(request.chatId());
        return ResultUtils.success(true);
    }

    @PostMapping("/clearPlatformHistory")
    @Operation(summary = "清空平台对话历史")
    public BaseResponse<Boolean> clearPlatformHistory(@RequestBody ChatIdRequest request) {
        inMemoryPlatformChatMemory.clear(request.chatId());
        return ResultUtils.success(true);
    }

}
