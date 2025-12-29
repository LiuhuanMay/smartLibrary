package com.uoh.ai.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.uoh.ai.PromptConstants;
import com.uoh.ai.advisor.MyLoggerAdvisor;
import com.uoh.ai.advisor.RagAdvisor;
import com.uoh.ai.tools.BookBorrowTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BorrowChatClient {

    private final VectorStore SimpleVectorStore;

    private final ChatMemory inMemoryChatMemory;
    private final ChatClient chatClient;

    private final BookBorrowTool bookBorrowTool;

    public BorrowChatClient(DashScopeChatModel dashScopeChatModel,VectorStore  SimpleVectorStore,ChatMemory inMemoryChatMemory,BookBorrowTool bookBorrowTool) {
       this.bookBorrowTool = bookBorrowTool;
       this.SimpleVectorStore = SimpleVectorStore;
       this.inMemoryChatMemory=inMemoryChatMemory;
       this.chatClient=ChatClient.builder(dashScopeChatModel)
               .defaultSystem(PromptConstants.BORROW_SYSTEM_PROMPT)
               .defaultAdvisors(
                       MessageChatMemoryAdvisor.builder(inMemoryChatMemory).build(),
                       new MyLoggerAdvisor())
               .build();
    }



    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new RagAdvisor(SimpleVectorStore))
                .tools(bookBorrowTool)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

    public List<Message> getHistory(String chatId) {
        // 从记忆库中获取该会话最近的 20 条消息
        return inMemoryChatMemory.get(chatId);
    }


}
