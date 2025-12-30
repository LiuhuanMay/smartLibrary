import { createWebHistory, createRouter } from 'vue-router'
import { userLoginUserStore } from "@/store/userStore.js"
import { routes } from "./routes.js";

const router = createRouter({
    history: createWebHistory(),
    routes,
})

// 白名单，不需要登录即可访问的页面
const whiteList = ['/login'];

router.beforeEach(async (to, from, next) => {
    const loginUserStore = userLoginUserStore();

    // --- 逻辑优化点 1：主动进入登录页的处理 ---
    // 如果目标路径是登录页，通常意味着用户想要切换账号或重新登录
    if (to.path === '/login') {
        // 可选：如果不希望自动登出，注释掉下面这行，逻辑会保持“已登录不准看登录页”
        // loginUserStore.logout();
        return next();
    }

    let loginUser = loginUserStore.loginUser;

    // --- 逻辑点 2：同步用户信息 ---
    // 如果本地有 token 但没有用户信息（比如刷新了页面），尝试从后端获取
    if (loginUserStore.token && !loginUser) {
        try {
            await loginUserStore.fetchLoginUser();
            loginUser = loginUserStore.loginUser;
        } catch (error) {
            console.error('全局守卫：获取用户信息失败', error);
            loginUserStore.logout(); // 获取失败说明 token 可能过期，清理掉
            loginUser = null;
        }
    }

    // --- 逻辑点 3：权限判定 ---
    if (loginUser) {
        // 场景 A：已登录状态，允许通行
        return next();
    } else {
        // 场景 B：未登录状态
        if (whiteList.includes(to.path)) {
            // 在白名单中，放行
            return next();
        } else {
            // 不在白名单，强制跳转登录，并记录重定向地址（可选）
            return next(`/login?redirect=${to.fullPath}`);
        }
    }
});

export default router;