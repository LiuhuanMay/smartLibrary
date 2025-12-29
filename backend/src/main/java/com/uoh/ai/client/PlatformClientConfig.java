package com.uoh.ai.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.uoh.ai.advisor.MyLoggerAdvisor;
import com.uoh.ai.prompt.PromptConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PlatformClientConfig {

    @Bean
    public ChatClient platfromChatClient(DashScopeChatModel dashScopeChatModel, ChatMemory inMemoryPlatformChatMemory, VectorStore platformVectorStore) {
       ChatClient chatClient=ChatClient.builder(dashScopeChatModel)
               .defaultSystem(PromptConstants.PLATFORM_SYSTEM_PROMPT)
               .defaultAdvisors(
                       MessageChatMemoryAdvisor.builder(inMemoryPlatformChatMemory).build(),
                       new QuestionAnswerAdvisor(platformVectorStore),
                       new MyLoggerAdvisor()
               )
               .build();
        return chatClient;
    }

}
