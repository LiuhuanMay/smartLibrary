package com.uoh.ai.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class VectorStoreConfig {



    @Bean
    VectorStore SimpleVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        // 示例：手动构造前三篇文档用于测试
        List<Document> documents = List.of(
                new Document(
                        "书名：人工智能导论。作者：周志华。简介：能系统介绍人工智的基本概念、核心技术及典型应用，适合作为高校人工智能入门教材。出版社：清华大学出版社。",
                        Map.of("bookId", "2002586435816394753", "stock", 47)
                ),
                new Document(
                        "书名：深度学习。作者：Ian Goodfellow。简介：全面讲解深度学习理论基础、模型结构与实际应用，是深度学习领域的经典著作。出版社：人民邮电出版社。",
                        Map.of("bookId", "2002586493794258945", "stock", 29)
                ),
                new Document(
                        "书名：Java 编程思想。作者：Bruce Eckel。简介：深入讲解 Java 语言的设计思想、面向对象编程及最佳实践，适合中高级 Java 开发者。出版社：机械工业出版社。",
                        Map.of("bookId", "2002586528888000514", "stock", 8)
                )
        );

        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
