import {http} from "@/utils/http.js";


/**
 *增加公告
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const addAnnouncement=(data)=>{
    return http.post("/announcement/add",data)
}

/**
 *删除通告
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const deleteAnnouncement=(data)=>{
    return http.post("/announcement/delete",data)
}

/**
 * 更新公告
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const updateAnnouncement=(data)=>{
    return http.post("/announcement/update",data)
}


/**
 *分页获取公告
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const listAnnouncementVOByPage=(data)=>{
    return http.post("/announcement/list/page/vo",data)
}
