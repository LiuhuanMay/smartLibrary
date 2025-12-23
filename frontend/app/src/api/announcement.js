import {http} from "@/utils/http.js";


export const listAnnouncementVOByPage=(data)=>{
    return http.post("/announcement/list/page/vo",data)
}

export const addReading=(data)=>{
    return http.post("/announcement/addReading",data)
}