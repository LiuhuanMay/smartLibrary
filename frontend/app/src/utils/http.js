import axios from 'axios'
import {userLoginUserStore} from "../store/userStore.js";

const service = axios.create({
    baseURL: '/api',
    withCredentials: true,
    timeout: 60 * 1000,
})

//请求拦截器

service.interceptors.request.use(config => {
    const userStore = userLoginUserStore()
    const token = userStore.token || localStorage.getItem('token')

    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})


//响应拦截器
service.interceptors.response.use(
    res => {
        if (res.data.code === 40100) {
            const loginUserStore = userLoginUserStore();
            loginUserStore.logout(); // 清理本地缓存，防止死循环判断
            // 如果不是在登录页，才跳转到登录页
            if (window.location.pathname !== '/login') {
                router.push('/login');
            }
        }
        return res.data;
    }
)




export const http = {
    get(url, params) {
        const config = {
            method: 'GET',
            url: url,
            params: params ? params : {}
        }
        return service(config)
    },
    post(url, data) {
        const config = {
            method: 'POST',
            url: url,
            data: data
        }
        return service(config)
    }
}