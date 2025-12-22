export const routes = [
    {
        path: "",
        redirect: "/book"
    },
    {
        name: "登录页",
        path: "/login",
        component: () => import('@/page/Login.vue')
    },
    {
                path: "/book/detail/:id",
                name: "图书详情",
                component: () => import('@/page/BookDetail.vue')
    },
    {
        name: "首页",
        path: "/home",
        component: () => import('@/page/Home.vue'),
        children: [
            {
                path: "/book",
                name: "图书精选",
                component: () => import('@/page/Book.vue')
            },
            {
                path: "/my",
                name: "我的",
                component: () => import('@/page/My.vue')
            },
            {
                path: "/notice",
                name: "通知通告",
                component: () => import('@/page/Notice.vue')
            },
            {
                path: "/assistant",
                name: "智能助手",
                component: () => import('@/page/Assistant.vue')
            }
        ]
    }
]
