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


    }
}