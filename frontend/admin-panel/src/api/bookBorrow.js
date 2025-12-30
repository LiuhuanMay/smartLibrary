import {http} from "@/utils/http.js";


/**
 * 分页获取借阅列表
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const listBookBorrowVOByPage=(data)=>{
    return http.post("/bookBorrow/list/page/vo",data)
}

export const ReviewBookBorrow=(data)=>{
    return http.post("/bookBorrow/reviewBookBorrow",data)
}