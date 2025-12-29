import { http } from "@/utils/http.js";


export const chat = (data) => {
    return http.post("/ai/chat", data);
}