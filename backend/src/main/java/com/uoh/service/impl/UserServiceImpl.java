package com.uoh.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uoh.common.ErrorCode;
import com.uoh.exception.ThrowUtils;
import com.uoh.mapper.UserMapper;
import com.uoh.model.dto.user.UserQueryRequest;
import com.uoh.model.entity.User;
import com.uoh.model.vo.UserVO;
import com.uoh.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    /**
     * 校验数据
     *
     * @param user
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validUser(User user, boolean add) {
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userQueryRequest == null) {
            return queryWrapper;
        }
        return queryWrapper;
    }

    /**
     * 获取用户封装
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        // 对象转封装类
        UserVO userVO = UserVO.objToVo(user);
        return userVO;
    }

    /**
     * 分页获取用户封装
     *
     * @param userPage
     * @return
     */
    @Override
    public Page<UserVO> getUserVOPage(Page<User> userPage) {
        List<User> userList = userPage.getRecords();
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        if (CollUtil.isEmpty(userList)) {
            return userVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserVO> userVOList = userList.stream().map(user -> {
            return UserVO.objToVo(user);
        }).collect(Collectors.toList());

        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

}
