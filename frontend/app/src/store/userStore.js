import {ref} from "vue";
import {defineStore} from "pinia";
import {getCurrentUser} from "@/api/auth.js";

export const userLoginUserStore = defineStore('user', () => {
    const loginUser = ref(null)

    const token = ref(localStorage.getItem('token') || null)

    function setLoginUser(newLoginUser) {
        loginUser.value = newLoginUser;
    }

    async function fetchLoginUser() {
        const res = await getCurrentUser();
        if (res.code === 0 && res.data) {
            loginUser.value = res.data;
        }
    }

    return {loginUser, token,fetchLoginUser}
})