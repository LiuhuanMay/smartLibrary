package com.uoh.ai.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class VectorStoreConfig {

    @Resource
    private List<Document> loadBookMarkdowns;

    @Resource
    private List<Document> loadPlatformMarkdowns;


    @Bean
    VectorStore borrowVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        simpleVectorStore.add(loadBookMarkdowns);
        return simpleVectorStore;
    }

    @Bean
    VectorStore platformVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        simpleVectorStore.add(loadPlatformMarkdowns);
        return simpleVectorStore;
    }
}
