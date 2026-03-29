package com.uoh.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uoh.common.BaseResponse;
import com.uoh.common.ErrorCode;
import com.uoh.common.ResultUtils;
import com.uoh.model.entity.Book;
import com.uoh.service.BookService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author L_H
 * @since 2026-03-29 12:04:28
 */
@Slf4j
@RestController
@RequestMapping("/book")
public class RecommendController {

    private static final int DEFAULT_RECOMMEND_SIZE = 4;

    @Resource
    private DashScopeChatModel dashScopeChatModel;

    @Resource
    private BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/recommend")
    public BaseResponse<List<RecommendBookVO>> recommend(@RequestBody RecommendRequest request) {
        if (request == null || isBlank(request.getHobby()) && isBlank(request.getMajor())
                && isBlank(request.getGoal()) && (request.getTags() == null || request.getTags().isEmpty())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请至少填写一个推荐条件");
        }

        List<Book> bookList = bookService.list();
        if (bookList == null || bookList.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }

        int size = request.getSize() == null || request.getSize() <= 0 ? DEFAULT_RECOMMEND_SIZE : Math.min(request.getSize(), 10);
        String prompt = buildPrompt(request, bookList, size);
        try {
            String aiResponse = dashScopeChatModel.call(prompt);
            List<RecommendResultItem> resultItems = parseRecommendResult(aiResponse);
            List<RecommendBookVO> result = buildRecommendBooks(resultItems, bookList, size);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("智能推荐失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "推荐失败，请稍后重试");
        }
    }

    private String buildPrompt(RecommendRequest request, List<Book> bookList, int size) {
        String tagsText = request.getTags() == null || request.getTags().isEmpty()
                ? "[]"
                : request.getTags().stream().filter(StringUtils::hasText).collect(Collectors.joining(", ", "[", "]"));

        String booksJson;
        try {
            List<Map<String, Object>> candidates = bookList.stream().map(book -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", String.valueOf(book.getId()));
                item.put("bookName", defaultString(book.getBookName()));
                item.put("author", defaultString(book.getAuthor()));
                item.put("publisher", defaultString(book.getPublisher()));
                item.put("bookIntroduction", defaultString(book.getBookIntroduction()));
                item.put("language", defaultString(book.getLanguage()));
                item.put("availableStock", book.getAvailableStock() == null ? 0 : book.getAvailableStock());
                item.put("totalBorrowedCount", book.getTotalBorrowedCount() == null ? 0 : book.getTotalBorrowedCount());
                return item;
            }).toList();
            booksJson = objectMapper.writeValueAsString(candidates);
        } catch (Exception e) {
            throw new RuntimeException("构造推荐书单失败", e);
        }

        return "你是一个严谨的图书推荐助手，请严格执行以下规则：" +
                "【核心原则】宁缺毋滥，如果无法确定匹配，必须返回空结果。" +

                "【匹配规则】" +
                "1. 必须同时参考用户的【爱好、专业、目标、标签】，至少满足其中一项“强相关”才可推荐。" +
                "2. “强相关”定义：图书内容、主题、领域与用户输入有明确直接关系，而不是泛泛相关。" +
                "3. 如果只是弱相关、模糊相关，一律不推荐。" +

                "【禁止行为】" +
                "4. 严禁为了凑数量推荐不相关图书。" +
                "5. 严禁主观臆测用户兴趣或扩展理解用户意图。" +
                "6. 严禁推荐候选列表之外的书。" +

                "【结果数量规则】" +
                "7. 最多推荐" + size + "本。" +
                "8. 如果符合条件不足" + size + "本，只返回符合的。" +
                "9. 如果一本都不符合，必须返回空数组。" +

                "【输出格式（必须严格遵守）】" +
                "10. 只允许输出 JSON，不允许任何解释或额外文字。" +
                "11. 格式如下：" +
                "{\"items\":[{\"id\":\"图书id\",\"recommendReason\":\"推荐理由\"}]}" +
                "12. 如果无推荐，返回：{\"items\":[]}。" +

                "【推荐理由要求】" +
                "13. 每条推荐理由30-60字。" +
                "14. 必须明确说明“这本书如何匹配用户输入”，不能泛泛而谈。" +

                "【用户信息】" +
                "爱好=" + defaultString(request.getHobby()) + "；" +
                "专业=" + defaultString(request.getMajor()) + "；" +
                "目标=" + defaultString(request.getGoal()) + "；" +
                "标签=" + tagsText + "。" +

                "【候选图书列表】" + booksJson;
    }

    private List<RecommendResultItem> parseRecommendResult(String aiResponse) {
        if (!StringUtils.hasText(aiResponse)) {
            return Collections.emptyList();
        }
        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode itemsNode = root.get("items");
            if (itemsNode == null || !itemsNode.isArray()) {
                return Collections.emptyList();
            }
            return objectMapper.readValue(itemsNode.toString(), new TypeReference<List<RecommendResultItem>>() {});
        } catch (Exception e) {
            log.error("解析推荐结果失败，AI原始返回: {}", aiResponse, e);
            return Collections.emptyList();
        }
    }

    private List<RecommendBookVO> buildRecommendBooks(List<RecommendResultItem> resultItems, List<Book> bookList, int size) {
        Map<String, Book> bookMap = bookList.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(book -> String.valueOf(book.getId()), book -> book, (a, b) -> a, LinkedHashMap::new));

        List<RecommendBookVO> result = new ArrayList<>();
        LinkedHashSet<String> usedIds = new LinkedHashSet<>();
        for (RecommendResultItem item : resultItems) {
            if (item == null || !StringUtils.hasText(item.getId()) || usedIds.contains(item.getId())) {
                continue;
            }
            Book book = bookMap.get(item.getId());
            if (book == null) {
                continue;
            }
            usedIds.add(item.getId());
            result.add(toRecommendBookVO(book, item.getRecommendReason()));
            if (result.size() >= size) {
                return result;
            }
        }

        return result;
    }

    private RecommendBookVO toRecommendBookVO(Book book, String reason) {
        RecommendBookVO vo = new RecommendBookVO();
        vo.setId(book.getId());
        vo.setBookName(book.getBookName());
        vo.setBookIntroduction(book.getBookIntroduction());
        vo.setCover(book.getCover());
        vo.setAuthor(book.getAuthor());
        vo.setPublisher(book.getPublisher());
        vo.setTotalStock(book.getTotalStock());
        vo.setAvailableStock(book.getAvailableStock());
        vo.setBorrowedCount(book.getBorrowedCount());
        vo.setTotalBorrowedCount(book.getTotalBorrowedCount());
        vo.setPrice(book.getPrice());
        vo.setLanguage(book.getLanguage());
        vo.setRecommendReason(StringUtils.hasText(reason) ? reason : "该图书与您的输入方向较为匹配，适合作为本次推荐候选。");
        vo.setTags(Collections.emptyList());
        return vo;
    }

    private boolean isBlank(String value) {
        return !StringUtils.hasText(value);
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    @Data
    public static class RecommendRequest {
        private String hobby;
        private String major;
        private String goal;
        private List<String> tags;
        private Integer size;
    }

    @Data
    public static class RecommendResultItem {
        private String id;
        private String recommendReason;
    }

    @Data
    public static class RecommendBookVO {
        private Long id;
        private String bookName;
        private String bookIntroduction;
        private String cover;
        private String author;
        private String publisher;
        private Integer totalStock;
        private Integer availableStock;
        private Integer borrowedCount;
        private Integer totalBorrowedCount;
        private Object price;
        private String language;
        private String recommendReason;
        private List<String> tags;
    }
}
