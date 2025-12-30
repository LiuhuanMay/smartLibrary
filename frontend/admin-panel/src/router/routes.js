export const routes = [
    {
        path: "/",
        redirect: "/user"
    },
    {
        path: "/login",
        name: "登录",
        component: () => import('@/page/Login.vue'),
    },
    {
        path: "/user",
        name: "用户管理",
        component: () => import('@/page/User.vue'),
    }, {
        path: "/announcement",
        name: "公告管理",
        component: () => import('@/page/Announcement.vue'),
    },
    {
        path: "/book",
        name: "图书管理",
        component: () => import('@/page/Book.vue'),
    },{
        path: "/borrow",
        name: "借阅管理",
        component: () => import('@/page/Borrow.vue'),
    },{
        path: "/system",
        name: "系统管理",
        component: () => import('@/page/System.vue'),
    },{
        path: "/visualization",
        name: "数据可视化",
        component: () => import('@/page/Visualization.vue'),
    }

]