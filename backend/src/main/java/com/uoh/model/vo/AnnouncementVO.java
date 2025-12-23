package com.uoh.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.uoh.model.entity.Announcement;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 通告管理视图
 *
 */
@Data
public class AnnouncementVO implements Serializable {

    /**
     * 公告ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 是否置顶：0否 1是
     */
    private Integer isTop;

    /**
     * 阅读量统计
     */
    private Integer viewCount;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date updateTime;

    private static final long serialVersionUID = 1L;



    /**
     * 封装类转对象
     *
     * @param announcementVO
     * @return
     */
    public static Announcement voToObj(AnnouncementVO announcementVO) {
        if (announcementVO == null) {
            return null;
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementVO, announcement);
        return announcement;
    }

    /**
     * 对象转封装类
     *
     * @param announcement
     * @return
     */
    public static AnnouncementVO objToVo(Announcement announcement) {


        AnnouncementVO announcementVO = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, announcementVO);
        return announcementVO;
    }
}
