<template>
    <div class="login-wrapper">
        <div class="fluid-bg"></div>

        <div class="login-card">
            <div class="login-header">
                <div class="logo-circle">
                    <el-icon :size="40" color="#409EFF"><Reading /></el-icon>
                </div>
                <h2 class="title">智慧图书系统</h2>
                <p class="subtitle">请输入您的凭据以访问后台</p>
            </div>

            <el-form :model="user" :rules="rules" ref="LoginRef" @keyup.enter="login">
                <el-form-item prop="username">
                    <el-input
                        v-model="user.username"
                        placeholder="账号 / 手机号"
                        :prefix-icon="User"
                        class="minimal-input"
                    />
                </el-form-item>

                <el-form-item prop="password" style="margin-bottom: 30px;">
                    <el-input
                        v-model="user.password"
                        type="password"
                        placeholder="密码"
                        :prefix-icon="Lock"
                        show-password
                        class="minimal-input"
                    />
                </el-form-item>

                <el-button
                    type="primary"
                    class="login-btn"
                    :loading="loading"
                    @click="login"
                >
                    登 录
                </el-button>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from 'vue-router';
import { User, Lock, Reading } from '@element-plus/icons-vue';
import { userLogin } from "@/api/auth.js";
import { userLoginUserStore } from "@/store/userStore.js";

const userStore = userLoginUserStore();
const router = useRouter();
const LoginRef = ref(null);
const loading = ref(false);

const user = reactive({
    username: '',
    password: ''
});

const rules = {
    username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
};

async function login() {
    if (!LoginRef.value) return;
    try {
        await LoginRef.value.validate();
        loading.value = true;
        const res = await userLogin({ phone: user.username, password: user.password });
        if (res && res.code === 0) {
            userStore.setToken(res.data);
            ElMessage.success("登录成功，正在跳转...");
            setTimeout(() => {
                router.push({ path: "/user", replace: true });
            }, 800);
        } else {
            ElMessage.error(res.message || "账号或密码错误");
        }
    } catch (error) {
        if (error.name !== 'ValidationError') ElMessage.error("网络连接异常");
    } finally {
        loading.value = false;
    }
}
</script>

<style scoped>
/* 极简背景：采用柔和的动态渐变感 */
.login-wrapper {
    height: 100vh;
    width: 100vw;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8faff;
    position: relative;
    overflow: hidden;
}

.fluid-bg {
    position: absolute;
    top: -10%;
    right: -10%;
    width: 50%;
    height: 60%;
    background: radial-gradient(circle, rgba(64, 158, 255, 0.1) 0%, rgba(255, 255, 255, 0) 70%);
    z-index: 0;
}

/* 登录卡片：高透毛玻璃效果 */
.login-card {
    width: 420px;
    padding: 40px 45px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.5);
    z-index: 1;
}

.login-header {
    text-align: center;
    margin-bottom: 35px;
}

.logo-circle {
    width: 70px;
    height: 70px;
    background: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 15px;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 8px;
    font-weight: 600;
}

.subtitle {
    font-size: 14px;
    color: #909399;
}

/* 输入框微调 */
:deep(.minimal-input .el-input__wrapper) {
    background-color: rgba(244, 247, 252, 0.7) !important;
    box-shadow: none !important;
    border: 1px solid transparent;
    border-radius: 12px;
    height: 50px;
    transition: all 0.3s ease;
}

:deep(.minimal-input .el-input__wrapper.is-focus) {
    background-color: #fff !important;
    border-color: #409EFF;
}

/* 按钮微调 */
.login-btn {
    width: 100%;
    height: 50px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 2px;
    background: #409EFF;
    box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
    transition: all 0.3s;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.login-footer {
    margin-top: 25px;
    display: flex;
    justify-content: center;
    font-size: 13px;
    color: #909399;
}

.divider { margin: 0 10px; color: #dcdfe6; }

.register-link {
    color: #409EFF;
    cursor: pointer;
    font-weight: 500;
}
</style>