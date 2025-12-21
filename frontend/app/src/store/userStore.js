import {ref} from "vue";
import {defineStore} from "pinia";
import {getCurrentUser} from "@/api/auth.js";

export const useLoginUserStore = defineStore('user', () => {
    const loginUser = ref({
        userName: "未登录",
    })

    function setLoginUser(newLoginUser) {
        loginUser.value = newLoginUser;
    }

    async function fetchLoginUser() {
        const res = await getCurrentUser();
        if (res.code === 0 && res.data) {
            loginUser.value = res.data;
        }
    }

    return {loginUser, setLoginUser, fetchLoginUser}
})