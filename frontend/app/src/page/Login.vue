<template>
    <div>
        <van-form @submit="login">
            <van-cell-group inset>
                <van-field
                        v-model="loginUser.phone"
                        name="手机号"
                        label="手机号"
                        placeholder="账号"
                        :rules="[{ required: true, message: '请填写账号' }]"
                />
                <van-field
                        v-model="loginUser.password"
                        type="password"
                        name="密码"
                        label="密码"
                        placeholder="密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    提交
                </van-button>
            </div>
        </van-form>

        <van-form @submit="register">
            <van-cell-group inset>
                <van-field
                        v-model="registerUser.phone"
                        name="手机号"
                        label="手机号"
                        placeholder="账号"
                        :rules="[{ required: true, message: '请填写账号' }]"
                />
                <van-field
                        v-model="registerUser.password"
                        type="password"
                        name="密码"
                        label="密码"
                        placeholder="密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                />
                <van-field
                        v-model="registerUser.email"
                        name="邮箱"
                        label="邮箱"
                        placeholder="邮箱"
                        :rules="[{ required: true, message: '请填写邮箱' }]"
                />
                <van-field
                        v-model="registerUser.code"
                        name="邮箱验证码"
                        label="邮箱验证码"
                        placeholder="邮箱验证码"
                        :rules="[{ required: true, message: '请填写验证码' }]"
                >
                    <template #button>
                        <van-button
                                size="small"
                                type="primary"
                                :disabled="codeDisabled"
                                @click="sendCode"
                        >
                            {{ codeText }}
                        </van-button>
                    </template>
                </van-field>

            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    提交
                </van-button>
            </div>
        </van-form>
        <van-form @submit="forgetPasswordT">
            <van-cell-group inset>
                <van-field
                        v-model="forgetPassword.email"
                        name="邮箱"
                        label="邮箱"
                        placeholder="邮箱"
                        :rules="[{ required: true, message: '请填写邮箱' }]"
                />
                <van-field
                        v-model="forgetPassword.code"
                        name="邮箱验证码"
                        label="邮箱验证码"
                        placeholder="邮箱验证码"
                        :rules="[{ required: true, message: '请填写验证码' }]"
                >

                    <template #button>
                        <van-button
                                size="small"
                                type="primary"
                                :disabled="codeDisabled"
                                @click="sendRCode"
                        >
                            {{ codeText }}
                        </van-button>
                    </template>
                </van-field>
                <van-field
                        v-model="forgetPassword.password"
                        type="password"
                        name="密码"
                        label="密码"
                        placeholder="密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                />
                <van-field
                        v-model="forgetPassword.repeatPassword"
                        type="password"
                        name="确认密码"
                        label="确认密码"
                        placeholder="密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                />

            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    提交
                </van-button>
            </div>
        </van-form>

    </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {userLogin,userRegister,sendRegisterCode,sendResetCode,forgotPassword} from "@/api/auth.js";


const loginUser=reactive({
    phone:"",
    password:""
})
const registerUser=reactive({
    phone:"",
    password:"",
    email:"",
    code:""
})
const forgetPassword=reactive({
    email:"",
    code:"",
    password:"",
    repeatPassword:""
})

// 验证码按钮状态
const codeText = ref("发送验证码");
const codeDisabled = ref(false);
let timer = null;


//注册验证码
const sendCode = async () => {
    if (!registerUser.email) {
        console.log("请先填写邮箱")
    }

    const res = await sendRegisterCode({
        email: registerUser.email
    });

    if (res.code === 0) {
        startCountdown();
    } else {
        //发送失败
    }
};
//忘记密码
const sendRCode = async () => {
    if (!forgetPassword.email) {
        console.log("请先填写邮箱")
    }

    const res = await sendResetCode({
        email: forgetPassword.email
    });

    if (res.code === 0) {
        startCountdown();
    } else {
        //发送失败
    }
};
const forgetPasswordT=async  ()=>{
    const res = await forgotPassword(forgetPassword)
    if(res.code){

    }else{

    }
}

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

const login = async () => {
    const res = await userLogin(loginUser)
    if(res.code==0){
        //登录成功

    }else {
        //登录失败
        console.log(res.message)
    }
};
const register = async () => {
    const res = await userRegister(registerUser)
    if(res.code==0){
        //注册成功
    }else {
        //注册失败
        console.log(res.message)
    }
};
</script>

<style  scoped>

</style>