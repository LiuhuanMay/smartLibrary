import { createWebHistory, createRouter } from 'vue-router'
import {userLoginUserStore} from "@/store/userStore.js"
import {routes} from "./routes.js";

const router = createRouter({
    history: createWebHistory(),
    routes,
})


const whiteList = ['/login'];
router.beforeEach(async (to, from, next) => {
    // 获取用户仓库实例
    const loginUserStore = userLoginUserStore();
    let loginUser = loginUserStore.loginUser;
    // 1. 如果用户信息未获取，尝试从接口获取
    if (!loginUser) {
        try {
            await loginUserStore.fetchLoginUser();
            loginUser = loginUserStore.loginUser;
        } catch (error) {
            // 捕获获取用户信息的异常（如接口报错），避免导航卡住
            console.error('获取用户信息失败：', error);
            loginUser = null;
        }
    }

    // 2. 已登录用户的处理
    if (loginUser) {
        // 若已登录用户访问登录页，重定向到首页（如图书精选）
        if (to.path === '/login') {
            next('/book');
        } else {
            // 正常放行
            next();
        }
    } else {
        // 3. 未登录用户的处理
        if (whiteList.includes(to.path)) {
            // 白名单页面（如登录页）直接放行
            next();
        } else {
            // 非白名单页面，重定向到登录页
            next('/login');
        }
    }
});

export default router
