package com.uoh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.uoh.model.dto.announcement.AnnouncementQueryRequest;
import com.uoh.model.entity.Announcement;
import com.uoh.model.vo.AnnouncementVO;


/**
 * 通告管理服务
 *
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 校验数据
     *
     * @param announcement
     * @param add 对创建的数据进行校验
     */
    void validAnnouncement(Announcement announcement, boolean add);

    /**
     * 获取查询条件
     *
     * @param announcementQueryRequest
     * @return
     */
    QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest);
    
    /**
     * 获取通告管理封装
     *
     * @return
     */
    AnnouncementVO getAnnouncementVO(Announcement announcement);

    /**
     * 分页获取通告管理封装
     *
     * @param announcementPage
     * @return
     */
    Page<AnnouncementVO> getAnnouncementVOPage(Page<Announcement> announcementPage);
}
