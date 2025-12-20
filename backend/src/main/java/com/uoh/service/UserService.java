package com.uoh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.uoh.model.dto.user.UserQueryRequest;
import com.uoh.model.entity.User;
import com.uoh.model.vo.UserVO;


/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 校验数据
     *
     * @param user
     * @param add 对创建的数据进行校验
     */
    void validUser(User user, boolean add);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
    
    /**
     * 获取用户封装
     *
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 分页获取用户封装
     *
     * @param userPage
     * @return
     */
    Page<UserVO> getUserVOPage(Page<User> userPage);
}
