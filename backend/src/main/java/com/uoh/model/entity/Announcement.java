package com.uoh.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告表
 * @TableName announcement
 */
@TableName(value ="announcement")
@Data
public class Announcement implements Serializable {
    /**
     * 公告ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 阅读量统计
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 发布人ID（管理员）
     */
    private Long publisherId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}