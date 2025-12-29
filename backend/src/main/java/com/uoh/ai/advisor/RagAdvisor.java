package com.uoh.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class RagAdvisor implements CallAdvisor, StreamAdvisor {

    private final VectorStore SimpleVectorStore;

    public RagAdvisor(VectorStore simpleVectorStore) {
        SimpleVectorStore = simpleVectorStore;
    }

    private ChatClientRequest before(ChatClientRequest request) {
        log.info("AI Request: {}", request.prompt());
        return request;
    }

    private void observeAfter(ChatClientResponse chatClientResponse) {
        log.info("AI Response: {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        String userQuery = chatClientRequest.prompt().getUserMessage().getText();

        // 策略：如果是简短的确认（如“确认”、“借这本”、“好的”），跳过检索，直接交给 AI 处理历史
        boolean isConfirmAction = userQuery.matches(".*(确认|可以|确定|借|是|好).*") && userQuery.length() < 10;

        if (!isConfirmAction) {
            // 执行检索
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(userQuery)
                    .topK(1) // 借书场景通常精准匹配 1 本即可
                    .similarityThreshold(0.6)
                    .build();

            List<Document> docs =SimpleVectorStore.similaritySearch(searchRequest);

            if (!docs.isEmpty()) {
                // 获取 ID
                String bookId = String.valueOf(docs.get(0).getMetadata().getOrDefault("bookId", ""));

                // 【关键点】：将 ID 强行塞进当前 UserMessage 的增强文本中
                // 这样 MemoryAdvisor 就会把包含 ID 的这段话存入数据库
                String augmentedText = String.format(
                        "%s\n(系统检索到书籍ID：%s，请在接下来的对话中使用该 ID 执行工具调用)",
                        userQuery, bookId
                );

                chatClientRequest = chatClientRequest.mutate()
                        .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedText))
                        .build();
            }
        }

        // 继续链条：这里会经过 MessageChatMemoryAdvisor，它会把 augmentedText 存起来
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        chatClientRequest = before(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponseFlux, this::observeAfter);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
