package com.uoh.model.dto.announcement;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新通告管理请求
 *
 */
@Data
public class AnnouncementUpdateRequest implements Serializable {

    /**
     * 公告ID
     */
    private Long id;

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
    private Integer status;

    /**
     * 是否置顶：0否 1是
     */
    private Integer isTop;


    private static final long serialVersionUID = 1L;
}