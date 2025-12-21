import axios from 'axios'

const service = axios.create({
    baseURL: '/api',
    withCredentials: true,
    timeout: 60 * 1000,
})

//请求拦截器
service.interceptors.request.use((config) => {
    return config
})

service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
)


//响应拦截器
service.interceptors.response.use((response) => {
    return response.data
})


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