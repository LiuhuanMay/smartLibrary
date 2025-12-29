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
import reactor.core.publisher.Flux;

import java.util.List;


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
                    .topK(3)
                    .similarityThreshold(0)
                    .build();

            List<Document> docs =SimpleVectorStore.similaritySearch(searchRequest);

            if (!docs.isEmpty()) {
                // 获取 ID
                String bookId = String.valueOf(docs.get(0).getMetadata().getOrDefault("bookId", ""));

                // 将书本id存储到对话上下文中
                String augmentedText = String.format(
                        "%s\n(系统检索到书籍ID：%s，请在接下来的对话中使用该 ID 执行工具调用)",
                        userQuery, bookId
                );

                chatClientRequest = chatClientRequest.mutate()
                        .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedText))
                        .build();
            }
        }
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
