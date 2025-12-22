<template>
    <div class="login-container" id="login">
        <!-- 顶部装饰区域 -->
        <div class="decoration-area">
            <div class="circle circle-1"></div>
            <div class="circle circle-2"></div>
            <!-- 图书馆logo -->
            <img class="logo" src="/static/library-logo.png" alt="智能图书馆" />
            <h1 class="slogan">智能图书馆</h1>
        </div>

        <!-- 表单卡片 -->
        <div class="form-card">
            <van-form @submit="onSubmit">
                <van-cell-group inset class="form-content">
                    <!-- 手机号：登录/注册 -->
                    <van-field
                        v-if="mode === 'login' || mode === 'register'"
                        v-model="formData.phone"
                        name="手机号"
                        label=""
                        placeholder="请输入手机号"
                        :rules="[{ required: true, message: '请填写手机号' }]"
                        class="input-item"
                        left-icon="phone-o"
                        autocomplete="tel"
                    />

                    <!-- 邮箱：注册/忘记密码 -->
                    <van-field
                        v-if="mode === 'register' || mode === 'forgotPassword'"
                        v-model="formData.email"
                        name="邮箱"
                        label=""
                        placeholder="请输入邮箱"
                        :rules="[{ required: true, message: '请填写邮箱' }]"
                        class="input-item"
                        left-icon="envelop-o"
                        autocomplete="email"
                    />

                    <!-- 密码：登录/注册 -->
                    <van-field
                        v-if="mode === 'login' || mode === 'register'"
                        v-model="formData.password"
                        :type="showPassword ? 'text' : 'password'"
                        name="密码"
                        left-icon="lock"
                        label=""
                        placeholder="请输入密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                        class="input-item"
                        :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
                        @click-right-icon="showPassword = !showPassword"
                        :autocomplete="mode === 'login' ? 'current-password' : 'new-password'"
                    />

                    <!-- 新密码：忘记密码 -->
                    <van-field
                        v-if="mode === 'forgotPassword'"
                        v-model="formData.password"
                        :type="showNewPassword ? 'text' : 'password'"
                        name="新密码"
                        label=""
                        placeholder="请输入新密码"
                        :rules="[{ required: true, message: '请填写新密码' }]"
                        class="input-item"
                        left-icon="lock"
                        :right-icon="showNewPassword ? 'eye-o' : 'closed-eye'"
                        @click-right-icon="showNewPassword = !showNewPassword"
                        autocomplete="new-password"
                    />

                    <!-- 确认密码：忘记密码 -->
                    <van-field
                        v-if="mode === 'forgotPassword'"
                        v-model="formData.repeatPassword"
                        :type="showRepeatPassword ? 'text' : 'password'"
                        name="确认密码"
                        left-icon="lock"
                        label=""
                        placeholder="请再次输入新密码"
                        :rules="[
                            { required: true, message: '请确认新密码' },
                            { validator: (val) => val === formData.password, message: '两次输入的密码不一致' }
                        ]"
                        class="input-item"
                        :right-icon="showRepeatPassword ? 'eye-o' : 'closed-eye'"
                        @click-right-icon="showRepeatPassword = !showRepeatPassword"
                        autocomplete="new-password"
                    />

                    <!-- 验证码：注册/忘记密码 -->
                    <van-field
                        v-if="mode === 'register' || mode === 'forgotPassword'"
                        v-model="formData.code"
                        name="邮箱验证码"
                        label=""
                        placeholder="请输入验证码"
                        :rules="[{ required: true, message: '请填写验证码' }]"
                        class="input-item"
                        left-icon="shield-o"
                        autocomplete="one-time-code"
                    >
                        <template #button>
                            <van-button size="small" type="primary" @click="sendVerificationCode" :disabled="codeDisabled">
                                {{ codeText }}
                            </van-button>
                        </template>
                    </van-field>
                </van-cell-group>

                <!-- 提交按钮 -->
                <div class="submit-area">
                    <van-button
                        round
                        block
                        type="primary"
                        native-type="submit"
                        class="submit-btn"
                    >
                        {{ submitButtonText }}
                    </van-button>
                </div>
            </van-form>

            <!-- 切换链接 -->
            <div class="switch-links">
                <a href="#" @click.prevent="switchMode('login')" v-if="mode !== 'login'">已有账号？去登录</a>
                <a href="#" @click.prevent="switchMode('register')" v-if="mode !== 'register'">没有账号？去注册</a>
                <a href="#" @click.prevent="switchMode('forgotPassword')" v-if="mode === 'login'">忘记密码？</a>
            </div>

            <!-- 其他登录方式 -->
            <div class="other-login">
                <div class="divider">
                    <span class="line"></span>
                    <span class="text">其他登录方式</span>
                    <span class="line"></span>
                </div>
                <div class="icon-list">
                    <div class="icon-item" @click="handleWechatLogin">
                        <img src="/static/wechat.png" alt="微信登录" class="icon" />
                        <span class="label">微信登录</span>
                    </div>
                    <div class="icon-item" @click="handleWeiboLogin">
                        <img src="/static/weibo.png" alt="微博登录" class="icon" />
                        <span class="label">微博登录</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref, computed } from "vue";
import { userLogin, userRegister, sendRegisterCode, sendResetCode, forgotPassword } from "@/api/auth.js";
import {userLoginUserStore} from "@/store/userStore.js";
import { useRouter} from 'vue-router'
const userStore = userLoginUserStore()
const router = useRouter()

import { showToast } from 'vant';
import 'vant/es/toast/style';
// 模式：login/register/forgotPassword
const mode = ref('login');

// 表单数据
const formData = reactive({
    phone: "",
    password: "",
    email: "",
    code: "",
    repeatPassword: ""
});

// 密码显示状态
const showPassword = ref(false);
const showNewPassword = ref(false);
const showRepeatPassword = ref(false);

// 验证码按钮状态
const codeText = ref("发送验证码");
const codeDisabled = ref(false);
let timer = null;


// 提交按钮文字
const submitButtonText = computed(() => {
    if (mode.value === 'login') return '登录';
    if (mode.value === 'register') return '注册';
    if (mode.value === 'forgotPassword') return '重置密码';
    return '提交';
});

// 验证码倒计时
const startCountdown = () => {
    let time = 60;
    codeDisabled.value = true;
    codeText.value = `${time}s`;

    timer = setInterval(() => {
        time--;
        codeText.value = `${time}s`;
        if (time <= 0) {
            clearInterval(timer);
            codeDisabled.value = false;
            codeText.value = "发送验证码";
        }
    }, 1000);
};

// 发送验证码
const sendVerificationCode = async () => {
    if (!formData.email) {
        showToast("请先填写邮箱");
        return;
    }

    try {
        const apiCall = mode.value === 'register' ? sendRegisterCode : sendResetCode;
        const res = await apiCall({ email: formData.email });

        if (res.code === 0) {
            showToast('验证码发送成功');
            startCountdown();
        } else {
            showToast(res.message || '验证码发送失败');
        }
    } catch (error) {
        showToast('请求失败');
    }
};

// 表单提交
const onSubmit = async () => {
    try {
        let res;
        if (mode.value === 'login') {
            res = await userLogin({ phone: formData.phone, password: formData.password });
            if (res.code === 0) {
                userStore.setToken(res.data)
                showToast('登录成功');
                setTimeout(()=>{
                    router.push("/book")
                },2000)

            } else {
                showToast(res.message || '登录失败');
            }
        } else if (mode.value === 'register') {
            res = await userRegister({
                phone: formData.phone,
                password: formData.password,
                email: formData.email,
                code: formData.code
            });
            if (res.code === 0) {
                showToast('注册成功');
                switchMode('login');
            } else {
                showToast(res.message || '注册失败');
            }
        } else if (mode.value === 'forgotPassword') {
            if (formData.password !== formData.repeatPassword) {
                showToast('两次输入的密码不一致');
                return;
            }
            res = await forgotPassword({
                email: formData.email,
                code: formData.code,
                password: formData.password,
                repeatPassword: formData.repeatPassword
            });
            if (res.code === 0) {
                showToast('密码重置成功');
                switchMode('login');
            } else {
                showToast(res.message || '密码重置失败');
            }
        }
    } catch (error) {
        showToast('操作失败');
    }
};

// 切换模式
const switchMode = (newMode) => {
    mode.value = newMode;
    // 重置表单数据
    Object.keys(formData).forEach(key => formData[key] = "");
    // 重置倒计时
    if (timer) {
        clearInterval(timer);
        timer = null;
        codeDisabled.value = false;
        codeText.value = "发送验证码";
    }
    // 重置密码显示状态
    showPassword.value = false;
    showNewPassword.value = false;
    showRepeatPassword.value = false;
};

// 其他登录方式
const handleWechatLogin = () => {
    showToast('微信登录功能待实现');
};

const handleQQLogin = () => {
    showToast('QQ登录功能待实现');
};

const handleWeiboLogin = () => {
    showToast('微博登录功能待实现');
};
</script>

<style scoped lang="scss">
// 全局样式重置（解决横向滚动的核心：限制视口溢出）
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box; // 所有元素的内边距和边框都包含在宽度内
}

html, body {
    width: 100%;
    overflow-x: hidden; // 禁止页面整体横向滚动
}

// 全局容器
.login-container {
    width: 100%; // 强制宽度为100%
    height: 100vh; // 固定高度为视口高度
    background: linear-gradient(180deg, #F0F7FF 0%, #FFFFFF 100%);
    position: relative;
    overflow-y: auto; // 仅纵向可滚动
    overflow-x: hidden; // 禁止横向滚动
    padding-bottom: 40px;
    box-sizing: border-box; // 将 padding 包含在高度内
}

// 顶部装饰区域
.decoration-area {
    width: 100%; // 强制宽度为100%
    height: 250px; // 增加高度
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: linear-gradient(180deg, #6BA8FF 0%, #A1C4FD 100%); // 调整渐变色
    overflow: hidden; // 隐藏装饰圆的溢出部分

    .circle {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1); // 调整透明度

        &.circle-1 {
            width: 220px;
            height: 220px;
            top: -110px;
            left: -60px;
        }

        &.circle-2 {
            width: 160px;
            height: 160px;
            top: -60px;
            right: -80px;
            opacity: 0.15;
        }
    }

    .logo {
        width: 88px; // 调整尺寸
        height: 88px;
        margin-bottom: 15px;
        border-radius: 50%;
        border: 2px solid rgba(255, 255, 255, 0.8); // 添加边框
        box-shadow: 0 6px 15px rgba(74, 144, 226, 0.3); // 调整阴影
    }

    .slogan {
        font-size: 22px; // 调整字号
        color: #FFFFFF; // 改为白色
        font-weight: 600; // 加粗
        margin: 0;
        letter-spacing: 1.5px; // 调整字间距
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1); // 添加文字阴影
    }
}

// 表单卡片
.form-card {
    width: calc(100% - 40px); // 用calc计算宽度，避免边距导致溢出
    max-width: 500px; // 限制最大宽度
    margin: -50px auto 0; // 改为auto居中，避免左右边距溢出
    background: #FFFFFF;
    border-radius: 24px; // 增加圆角
    padding: 25px;
    box-shadow: 0 12px 25px rgba(0, 0, 0, 0.08); // 调整阴影
    position: relative;
    z-index: 10;
    box-sizing: border-box; // 内边距包含在宽度内
}

// 表单内容
.form-content {
    --van-cell-group-inset-padding: 0;
    --van-cell-border: none;

    .input-item {
        margin-bottom: 20px;
        background: transparent; // 移除背景
        border-bottom: 1px solid #E0E0E0; // 添加下边框
        border-radius: 0; // 移除圆角
        padding: 8px 0; // 调整内边距
        transition: border-color 0.3s, box-shadow 0.3s; // 添加过渡效果

        --van-field-label-width: 0;
        --van-field-input-padding: 0;
        --van-field-content-padding: 0;

        &:focus-within {
            border-color: #4A90E2;
            box-shadow: 0 1px 0 0 #4A90E2;
        }

        :deep(.van-field__left-icon) {
            margin-right: 12px;
            color: #888;
            font-size: 18px;
        }

        :deep(.van-field__control) {
            font-size: 15px;
        }

        :deep(.van-button) {
            height: 32px;
            line-height: 32px;
            padding: 0 15px;
            border-radius: 16px;
            background: #4A90E2;
            border: none;
            color: #fff;
            font-size: 13px;

            &.van-button--disabled {
                background: #DCDFE6;
                color: #A8ABB2;
            }
        }
    }
}

// 提交按钮区域
.submit-area {
    margin: 20px 0;

    .submit-btn {
        height: 48px; // 增加高度
        line-height: 48px;
        border-radius: 24px; // 增加圆角
        background: linear-gradient(135deg, #5A9BFF 0%, #3E82F7 100%); // 调整渐变色
        box-shadow: 0 8px 15px rgba(74, 144, 226, 0.3); // 调整阴影
        font-size: 17px; // 调整字号
        font-weight: 600; // 加粗
        border: none; // 移除边框

        &:active {
            transform: translateY(1px); // 添加点击效果
            box-shadow: 0 6px 12px rgba(74, 144, 226, 0.25); // 调整点击阴影
        }
    }
}

// 切换链接
.switch-links {
    margin: 25px 0; // 增加垂直间距
    text-align: center;

    a {
        margin: 0 15px; // 增加水平间距
        color: #4A90E2;
        text-decoration: none;
        font-size: 14px;
        transition: color 0.2s; // 添加过渡效果

        &:hover {
            text-decoration: underline;
            color: #357ABD; // 调整悬停颜色
        }
    }
}

// 其他登录方式
.other-login {
    margin-top: 35px; // 调整上边距

    .divider {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 25px; // 调整下边距

        .line {
            width: 60px; // 调整长度
            height: 1px;
            background: #EAEAEA; // 调整颜色
        }

        .text {
            color: #B0B0B0; // 调整颜色
            font-size: 13px; // 调整字号
            margin: 0 15px; // 调整间距
        }
    }

    .icon-list {
        display: flex;
        justify-content: center;
        gap: 40px; // 调整间距

        .icon-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            cursor: pointer;
            transition: transform 0.2s ease-in-out; // 添加过渡效果

            &:hover {
                transform: scale(1.1); // 添加悬停放大效果
            }

            .icon {
                width: 44px; // 调整尺寸
                height: 44px;
                margin-bottom: 8px; // 调整下边距
                border-radius: 50%;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08); // 添加阴影
            }

            .label {
                font-size: 13px; // 调整字号
                color: #666; // 调整颜色
            }
        }
    }
}

// 适配移动端
@media (max-width: 375px) {
    .form-card {
        width: calc(100% - 30px); // 适配小屏的宽度计算
        margin: 0 auto;
        padding: 15px;
    }

    .decoration-area {
        height: 180px;
    }
}
</style>