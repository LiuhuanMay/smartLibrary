import {http} from "@/utils/http.js";


export const updateUser=(data)=>{
    return http.post("/user/update",data)
}