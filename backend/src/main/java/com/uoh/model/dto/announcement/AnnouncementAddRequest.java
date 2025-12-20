package com.uoh.model.dto.announcement;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建通告管理请求
 *
 */
@Data
public class AnnouncementAddRequest implements Serializable {


    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型：0普通 1系统通知 2维护公告
     */
    private Integer type;

    /**
     * 状态：0下线 1发布
     */
    private Integer status = 1;

    /**
     * 是否置顶：0否 1是
     */
    private Integer isTop;

    /**
     * 阅读量统计
     */
    private Integer viewCount;

    /**
     * 发布人ID
     */
    private Long publisherId;

    private static final long serialVersionUID = 1L;
}