import {ref} from "vue";
import {defineStore} from "pinia";
import {getCurrentUser} from "@/api/auth.js";
import Access_Enum from "@/access/access_Enum.js";

export const userLoginUserStore = defineStore('LoginUser', () => {

    // 1. 状态定义
    const loginUser = ref(JSON.parse(localStorage.getItem('user_info')) || null)
    const token = ref(localStorage.getItem('token') || null)

    // 2. 设置用户信息 (增加持久化)
    function setLoginUser(newLoginUser) {
        loginUser.value = newLoginUser;
        if (newLoginUser) {
            localStorage.setItem('user_info', JSON.stringify(newLoginUser));
        } else {
            localStorage.removeItem('user_info');
        }
    }
    // 3. 设置 Token
    function setToken(newToken) {
        token.value = newToken;
        if (newToken) {
            localStorage.setItem('token', newToken);
        } else {
            localStorage.removeItem('token');
        }
    }


    // 4. 获取当前登录用户
    async function fetchLoginUser() {
        const res = await getCurrentUser();
        if (res.code === 0 && res.data) {
            setLoginUser(res.data);
        }else{
            loginUser.value = {userRole: Access_Enum.NOT_LOGIN};
        }

    }

    // 5. 退出登录 (核心新增)
    function logout() {
        // 清空响应式数据
        token.value = null;
        loginUser.value = null;
        // 清理本地存储
        localStorage.removeItem('token');
        localStorage.removeItem('user_info');
    }

    return {
        loginUser,
        token,
        fetchLoginUser,
        setLoginUser,
        setToken,
        logout
    }
})