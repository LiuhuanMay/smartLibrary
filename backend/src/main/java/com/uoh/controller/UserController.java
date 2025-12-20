package com.uoh.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uoh.common.BaseResponse;
import com.uoh.common.DeleteRequest;
import com.uoh.common.ErrorCode;
import com.uoh.common.ResultUtils;
import com.uoh.exception.BusinessException;
import com.uoh.exception.ThrowUtils;
import com.uoh.model.dto.user.UserAddRequest;
import com.uoh.model.dto.user.UserQueryRequest;
import com.uoh.model.dto.user.UserUpdateRequest;
import com.uoh.model.entity.User;
import com.uoh.model.vo.UserVO;
import com.uoh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "创建用户")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 数据校验
        userService.validUser(user, true);
        // 写入数据库
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newUserId = user.getId();
        return ResultUtils.success(newUserId);
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除用户")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        User oldUser = userService.getById(id);
        ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = userService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 数据校验
        userService.validUser(user, false);
        // 判断是否存在
        long id = userUpdateRequest.getId();
        User oldUser = userService.getById(id);
        ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取用户（封装类）")
    public BaseResponse<UserVO> getUserVOById(@RequestParam ("id") long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(userService.getUserVO(user));
    }



    /**
     * 分页获取用户列表（封装类）
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取用户列表（封装类）")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrentPage();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        // 获取封装类
        return ResultUtils.success(userService.getUserVOPage(userPage));
    }

    // endregion
}
