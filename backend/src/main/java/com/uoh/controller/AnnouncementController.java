package com.uoh.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uoh.common.BaseResponse;
import com.uoh.common.DeleteRequest;
import com.uoh.common.ErrorCode;
import com.uoh.common.ResultUtils;
import com.uoh.exception.BusinessException;
import com.uoh.exception.ThrowUtils;
import com.uoh.model.dto.announcement.AddReading;
import com.uoh.model.dto.announcement.AnnouncementAddRequest;
import com.uoh.model.dto.announcement.AnnouncementQueryRequest;
import com.uoh.model.dto.announcement.AnnouncementUpdateRequest;
import com.uoh.model.entity.Announcement;
import com.uoh.model.vo.AnnouncementVO;
import com.uoh.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 通告管理接口
 *
 */
@RestController
@RequestMapping("/announcement")
@Tag(name = "通告管理")
@Slf4j
public class AnnouncementController {



    @Resource
    private AnnouncementService announcementService;

    /**
     * 阅读量+1
     * @param addReading
     * @return
     */
    @PostMapping("/addReading")
    @Operation(summary = "阅读量+1")
    public BaseResponse<String> addReading(@RequestBody AddReading addReading) {

        Long id = addReading.getId();
        Announcement announcement = announcementService.getById(id);

        if (announcement == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"公告不存在");
        }

        Integer viewCount = announcement.getViewCount();
        announcement.setViewCount(viewCount == null ? 1 : viewCount + 1);

        boolean success = announcementService.updateById(announcement);

        if (success) {
            return ResultUtils.success("阅读量+1成功");
        } else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"阅读量更新失败");
        }
    }


    // region 增删改查

    /**
     * 创建通告管理
     *
     * @param announcementAddRequest
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "创建通告")
    public BaseResponse<Long> addAnnouncement(@RequestBody AnnouncementAddRequest announcementAddRequest) {
        ThrowUtils.throwIf(announcementAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementAddRequest, announcement);
        // 数据校验
        announcementService.validAnnouncement(announcement, true);
        // 写入数据库
        boolean result = announcementService.save(announcement);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newAnnouncementId = announcement.getId();
        return ResultUtils.success(newAnnouncementId);
    }

    /**
     * 删除通告管理
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除通告")
    public BaseResponse<Boolean> deleteAnnouncement(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = announcementService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新通告管理
     *
     * @param announcementUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @Operation(summary = "更新通告")
    public BaseResponse<Boolean> updateAnnouncement(@RequestBody AnnouncementUpdateRequest announcementUpdateRequest) {
        if (announcementUpdateRequest == null || announcementUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementUpdateRequest, announcement);
        // 数据校验
        announcementService.validAnnouncement(announcement, false);
        // 判断是否存在
        long id = announcementUpdateRequest.getId();
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = announcementService.updateById(announcement);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取通告管理（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取通告管理（封装类）")
    public BaseResponse<AnnouncementVO> getAnnouncementVOById(@RequestParam ("id") long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Announcement announcement = announcementService.getById(id);
        ThrowUtils.throwIf(announcement == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(announcementService.getAnnouncementVO(announcement));
    }



    /**
     * 分页获取通告管理列表（封装类）
     *
     * @param announcementQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取通告管理列表（封装类）")
    public BaseResponse<Page<AnnouncementVO>> listAnnouncementVOByPage(@RequestBody AnnouncementQueryRequest announcementQueryRequest) {
        long current = announcementQueryRequest.getCurrentPage();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        log.info(announcementQueryRequest.toString());
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
        // 获取封装类
        return ResultUtils.success(announcementService.getAnnouncementVOPage(announcementPage));
    }

    // endregion
}
