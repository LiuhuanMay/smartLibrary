package com.uoh.ai.client;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class BorrowChatClientTest {

    @Resource
    private BorrowChatClient borrowChatClient;


    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String userMessage = "我想借书，请帮我推荐一下";

        String response = borrowChatClient.doChat(userMessage, chatId);

    }
}