package com.uoh.ai.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.uoh.ai.advisor.MyLoggerAdvisor;
import com.uoh.ai.advisor.RagAdvisor;
import com.uoh.ai.prompt.PromptConstants;
import com.uoh.ai.tools.BookBorrowTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BorrowChatClientConfig {

    @Bean
    public ChatClient borrowChatClient(DashScopeChatModel dashScopeChatModel,VectorStore borrowVectorStore,ChatMemory inMemoryBorrowChatMemory,BookBorrowTool bookBorrowTool) {
       ChatClient chatClient=ChatClient.builder(dashScopeChatModel)
               .defaultSystem(PromptConstants.BORROW_SYSTEM_PROMPT)
               .defaultAdvisors(
                       MessageChatMemoryAdvisor.builder(inMemoryBorrowChatMemory).build(),
                       new MyLoggerAdvisor(),
                       new RagAdvisor(borrowVectorStore)
               )
               .defaultTools(bookBorrowTool)
               .build();
        return chatClient;
    }

}
