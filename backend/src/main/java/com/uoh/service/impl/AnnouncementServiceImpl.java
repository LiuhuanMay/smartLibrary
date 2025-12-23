package com.uoh.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uoh.common.ErrorCode;
import com.uoh.exception.ThrowUtils;
import com.uoh.mapper.AnnouncementMapper;
import com.uoh.model.dto.announcement.AnnouncementQueryRequest;
import com.uoh.model.entity.Announcement;
import com.uoh.model.vo.AnnouncementVO;
import com.uoh.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通告管理服务实现
 *
 */
@Service
@Slf4j
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {


    /**
     * 校验数据
     *
     * @param announcement
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validAnnouncement(Announcement announcement, boolean add) {
        ThrowUtils.throwIf(announcement == null, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 获取查询条件
     *
     * @param announcementQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);
        if (announcementQueryRequest == null) {
            return queryWrapper;
        }
        return queryWrapper;
    }

    /**
     * 获取通告管理封装
     *
     * @param announcement
     * @return
     */
    @Override
    public AnnouncementVO getAnnouncementVO(Announcement announcement) {
        // 对象转封装类
        AnnouncementVO announcementVO = AnnouncementVO.objToVo(announcement);
        return announcementVO;
    }

    /**
     * 分页获取通告管理封装
     *
     * @param announcementPage
     * @return
     */
    @Override
    public Page<AnnouncementVO> getAnnouncementVOPage(Page<Announcement> announcementPage) {
        List<Announcement> announcementList = announcementPage.getRecords();
        Page<AnnouncementVO> announcementVOPage = new Page<>(announcementPage.getCurrent(), announcementPage.getSize(), announcementPage.getTotal());
        if (CollUtil.isEmpty(announcementList)) {
            return announcementVOPage;
        }
        // 对象列表 => 封装对象列表
        List<AnnouncementVO> announcementVOList = announcementList.stream().map(announcement -> {
            return AnnouncementVO.objToVo(announcement);
        }).collect(Collectors.toList());

        announcementVOPage.setRecords(announcementVOList);
        return announcementVOPage;
    }

}
