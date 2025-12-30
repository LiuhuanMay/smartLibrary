<template>
    <div class="LoginContainer">
        <div class="glass-effect">
            <div class="form-container">
                <el-form :model="user" :rules="rules" ref="LoginRef">
                    <h2 class="form-title">欢迎智慧图书管理系统</h2>
                    <el-form-item prop="username">
                        <el-input size="large" placeholder="请输入账号" v-model="user.username" class="input-field">
                            <template #prefix>
                                <el-icon>
                                    <User/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input size="large" placeholder="请输入密码" v-model="user.password" show-password
                                  class="input-field">
                            <template #prefix>
                                <el-icon>
                                    <Lock/>
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" class="login-button" @click="login">登录</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {useRouter} from 'vue-router';
import {userLoginUserStore} from "@/store/userStore.js";
const LoginUser = userLoginUserStore();
const router = useRouter();
const LoginRef = ref(null);
const user = reactive({
    username: '',
    password: ''
});
const rules = reactive({
    username: [
        {required: true, message: '请输入账户名', trigger: 'blur'},
    ],
    password: [
        {required: true, message: '请输入密码', trigger: 'blur'},
    ],
});

async function login() {
    const valid = await LoginRef.value.validate();
    if (valid) {
        ElMessage.success("登录成功");
        await router.push({
            path: "/home",
            replace: true,
        });
    }
}
</script>

<style scoped>
.LoginContainer {
    height: 100vh;
    background: linear-gradient(to bottom right, #6a82fb, #fc5c7d);
    display: flex;
    align-items: center;
    justify-content: center;
}

.glass-effect {
    display: flex;
    background-color: rgba(255, 255, 255, 0.15);
    width: 30%;
    height: 60%;
    border-radius: 10px;
    overflow: hidden;
    backdrop-filter: blur(15px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.5);
}

.form-container {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 90%;
}

.form-title {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 30px;
    color: #ffffff;
}

.input-field {
    margin-bottom: 20px;
}

.input-field .el-input__inner {
    border-radius: 5px;
    border: 1px solid #ffffff;
    transition: border-color 0.3s;
}

.input-field .el-input__inner:focus {
    border-color: #6a82fb;
    box-shadow: 0 0 5px rgba(106, 130, 251, 0.5);
}

.login-button {
    width: 100%;
    border-radius: 5px;
    transition: background-color 0.3s;
}

.login-button:hover {
    background-color: #5a92e0;
}

.footer-links {
    display: flex;
    justify-content: space-between;
    width: 100%;
    color: #ffffff;
}

.register-link {
    color: #6a82fb;
    cursor: pointer;
    transition: color 0.3s;
}

.register-link:hover {
    color: #fc5c7d;
}
</style>
