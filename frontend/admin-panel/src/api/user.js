import {http} from "@/utils/http.js";


/***
 *分页查询用户
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const listUserVOByPage=(data)=>{
    return http.post("/user/list/page/vo",data)
}

/**
 * 更新
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const updateUser=(data)=>{
    return http.post("/user/update",data)
}