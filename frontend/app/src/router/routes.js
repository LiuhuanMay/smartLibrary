export const routes = [
    {
        name:"测试页",
        path:"/test",
        component:()=>import('@/components/test.vue')
    },{
        name: "404页",
        path: "/notfound",
        component:()=>import('@/page/NotFound.vue')
    }
]