package com.uoh.ai.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Configuration
class DocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    DocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Bean
    public List<Document> loadPlatformMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/platform/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 提取文档倒数第 3 和第 2 个字作为标签
                String status = filename.substring(filename.length() - 6, filename.length() - 4);
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status", status)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }

    @Bean
    public List<Document> loadBookMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/book/*.md");

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                // 直接读取整个文件的原始字符串，避免被 Reader 切碎
                String rawContent = resource.getContentAsString(StandardCharsets.UTF_8);

                // 1. 提取 bookId
                String bookId = extractId(rawContent);

                // 2. 构造干净的 Metadata（只要 bookId 和 source）
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("bookId", bookId != null ? bookId : "unknown");
                metadata.put("source", fileName);

                // 3. 创建 Document
                // 注意：这里直接用 rawContent，保证 AI 能看到完整的书名、简介和 ID
                Document doc = new Document(rawContent, metadata);

                allDocuments.add(doc);
                log.info("加载成功: {} -> bookId: {}", fileName, bookId);
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }

    private String extractId(String content) {
        // 适配：允许冒号前后有任意空白字符，支持中英文冒号
        // Pattern 说明：匹配 bookId，后面跟冒号，最后抓取数字
        Pattern pattern = Pattern.compile("bookId[:：]\\s*(\\d+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
