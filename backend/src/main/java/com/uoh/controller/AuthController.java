package com.uoh.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.uoh.common.BaseResponse;
import com.uoh.common.ErrorCode;
import com.uoh.common.ResultUtils;
import com.uoh.manager.EmailManager;
import com.uoh.model.dto.user.ResetPasswordRequest;
import com.uoh.model.dto.user.UserLoginRequest;
import com.uoh.model.dto.user.UserRegisterRequest;
import com.uoh.model.entity.User;
import com.uoh.service.UserService;
import com.uoh.utils.JWTUtils;
import com.uoh.utils.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "权限管理")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private JWTUtils jwtUtils;

    @Resource
    private EmailManager emailManager;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest request){
        User user = userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getPhone, request.getPhone())
                .eq(User::getPassword, request.getPassword()));
        if(user == null){
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR,"用户名或密码错误");
        }
        String token = jwtUtils.generateToken(user.getId());
        return ResultUtils.success(token);
    }

    @GetMapping("/currentUser")
    @Operation(summary = "获取当前登录用户")
    public BaseResponse<User> getCurrentUser() {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        User user = userService.getById(userId);
        return ResultUtils.success(user);
    }

    @GetMapping("/sendCode")
    @Operation(summary = "注册发送验证码")
    public BaseResponse<String> sendRegisterCode(@RequestParam ("email") String email) {
        emailManager.sendCode(email, EmailManager.CodeType.REGISTER);
        return ResultUtils.success("验证码已发送");
    }

    @GetMapping("/sendResetCode")
    @Operation(summary = "忘记密码发送验证码")
    public BaseResponse<String> sendResetCode(@RequestParam ("email")String email) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR ,"邮箱未注册");
        }
        emailManager.sendCode(email, EmailManager.CodeType.RESET);
        return ResultUtils.success("验证码已发送");
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public BaseResponse<String> userRegister(@RequestBody UserRegisterRequest request){
        if (request.getPhone() == null || request.getPassword() == null
                || request.getEmail() == null || request.getCode() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "手机号、密码、邮箱或验证码不能为空");
        }
        if (!emailManager.checkCode(request.getEmail(), request.getCode(), EmailManager.CodeType.REGISTER)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }

        User user = new User();
        BeanUtils.copyProperties(request,user);
        userService.save(user);
        emailManager.deleteCode(request.getEmail(), EmailManager.CodeType.REGISTER);
        return ResultUtils.success("注册成功");
    }

    @PostMapping("/forgotPassword")
    @Operation(summary = "重置密码")
    public BaseResponse<String> forgotPassword(@RequestBody ResetPasswordRequest request){
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, request.getEmail()));
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if (!emailManager.checkCode(request.getEmail(), request.getCode(), EmailManager.CodeType.RESET)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        if(!request.getPassword().equals(request.getRepeatPassword())){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        user.setPassword(request.getPassword());
        userService.updateById(user);
        emailManager.deleteCode(request.getEmail(), EmailManager.CodeType.RESET);

        return ResultUtils.success("密码重置成功");
    }
}
