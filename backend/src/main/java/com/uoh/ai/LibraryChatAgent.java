package com.uoh.ai;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryChatAgent {

    private final DashScopeChatModel model;
    private final ChatClient borrowClient;
    private final ChatClient platformClient;

    public LibraryChatAgent(DashScopeChatModel dashScopeChatModel,
                            ChatClient borrowChatClient,
                            ChatClient platfromChatClient) {
        this.model = dashScopeChatModel;
        this.borrowClient = borrowChatClient;
        this.platformClient = platfromChatClient;
    }

    public String doChat(String message, String chatId) {
        // 1. 路由决策（意图识别）
        String intent = model.call("请判断用户意图：如果涉及平台怎么操作的，比如怎么借书，怎么查看借阅等，返回 'PLATFORM'；" +
                "其它的返回 'BORROW'。只返回单词，不返回其他。用户输入：" + message);

        log.info("用户意图识别结果: {}, 会话ID: {}", intent, chatId);

        // 2. 根据意图分发给不同的 Agent
        if (intent != null && intent.contains("BORROW")) {
            return borrowClient.prompt()
                    .user(message)
                    // 传入 chatId 以维持 MessageChatMemoryAdvisor 的记忆
                    .advisors(spec -> spec.param("chat_memory_conversation_id", chatId))
                    .call()
                    .content();
        } else {
            return platformClient.prompt()
                    .user(message)
                    .advisors(spec -> spec.param("chat_memory_conversation_id", chatId))
                    .call()
                    .content();
        }
    }
}