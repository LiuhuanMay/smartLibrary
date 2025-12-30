import {http} from "@/utils/http.js";


/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const getSystemStatus=()=>{
    return http.get("/api/monitor/status")
}