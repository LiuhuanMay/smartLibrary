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
       return res.data
    } ,
    err => {
        const code = err.response?.data?.code;
        if (code === 40100) {
            // token 过期 / 无效 / 篡改 → 统一未登录
            location.href = '/login';
        }
        return Promise.reject(err);
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