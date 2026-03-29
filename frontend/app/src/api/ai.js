import { http } from "@/utils/http.js";

/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const chat = (data) => {
    return http.post("/ai/chat", data);
}

/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const clearBorrowHistory=(data)=>{
    return http.post("/ai/clearBorrowHistory",data)
}

/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const clearPlatformHistory=(data)=>{
    return http.post("/ai/clearPlatformHistory",data)
}
