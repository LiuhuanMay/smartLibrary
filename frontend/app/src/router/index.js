import { createWebHistory, createRouter } from 'vue-router'
import {userLoginUserStore} from "@/store/userStore.js"
import {routes} from "./routes.js";

const router = createRouter({
    history: createWebHistory(),
    routes,
})


const whiteList = ['/login'];
router.beforeEach(async (to, from, next) => {
    const loginUserStore = userLoginUserStore();
    let loginUser = loginUserStore.loginUser;

    // 1. 如果用户信息未获取，尝试同步
    if (!loginUser) {
        try {
            await loginUserStore.fetchLoginUser();
            loginUser = loginUserStore.loginUser;
        } catch (error) {
            console.error('获取用户信息失败：', error);
            loginUser = null;
        }
    }

    // 2. 逻辑判断 (确保每个路径只走一个 next)
    if (loginUser) {
        // 如果已登录，且要去登录页，可以考虑重定向到首页（可选）
        if (to.path === '/login') {
            return next('/');
        }
        return next();
    } else {
        // 未登录状态
        if (whiteList.includes(to.path)) {
            return next();
        } else {
            return next('/login');
        }
    }
});

export default router
