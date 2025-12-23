package com.uoh.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.uoh.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图
 *
 */
@Data
public class UserVO implements Serializable {

    /**
     *
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 手机号（登录账号）
     */
    private String phone;

    /**
     *用户昵称
     */
    private String nickname;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别：0未知 1男 2女
     */
    private Integer gender;

    /**
     * 角色：0普通用户 1管理员
     */
    private Integer role;

    /**
     * 状态：0禁用 1正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 封装类转对象
     *
     * @param userVO
     * @return
     */
    public static User voToObj(UserVO userVO) {
        if (userVO == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        return user;
    }

    /**
     * 对象转封装类
     *
     * @param user
     * @return
     */
    public static UserVO objToVo(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
