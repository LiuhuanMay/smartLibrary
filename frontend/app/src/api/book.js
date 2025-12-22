import {http} from "@/utils/http.js";


/**
 * 分页获取图书列表
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const listBookVOByPage= (data)=>{
    return http.post("/book/list/page/vo",data)
}
