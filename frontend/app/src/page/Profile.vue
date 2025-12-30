<template>
    <div class="profile-page">
        <van-nav-bar
            title="个人资料"
            left-arrow
            right-text="保存"
            @click-left="$router.back()"
            @click-right="onSave"
        />

        <div class="avatar-section">
            <van-uploader :after-read="afterRead">
                <van-image
                    round
                    width="80"
                    height="80"
                    :src="userForm.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
                    fit="cover"
                />
                <div class="upload-tip">点击更换头像</div>
            </van-uploader>
        </div>

        <van-cell-group inset>
            <van-field v-model="userForm.nickname" label="昵称" placeholder="请输入昵称" input-align="right" />
            <van-field v-model="userForm.phone" label="手机号" readonly disabled input-align="right" />
            <van-field v-model="userForm.email" label="邮箱" placeholder="请输入邮箱" input-align="right" />
            <van-cell title="性别" is-link :value="genderText" @click="showGenderSheet = true" />
            <van-field v-model.number="userForm.age" type="digit" label="年龄" placeholder="请输入年龄" input-align="right" />
            <van-cell title="账号角色" :value="userForm.role === 1 ? '管理员' : '普通用户'" />
            <van-cell title="注册时间" :value="formatDate(userForm.createTime)" />
        </van-cell-group>

        <van-action-sheet
            v-model:show="showGenderSheet"
            :actions="genderActions"
            cancel-text="取消"
            close-on-click-action
            @select="onGenderSelect"
        />
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { showToast, showLoadingToast } from 'vant';
import { userLoginUserStore } from "@/store/userStore.js";
import { useRouter } from 'vue-router';
import { updateUser } from "@/api/user.js";
import 'vant/es/toast/style';
const userStore = userLoginUserStore();
const router = useRouter();

const userForm = ref({
    id: null,
    phone: '',
    nickname: '',
    email: '',
    avatar: null,
    age: 0,
    gender: 0,
    role: 0,
    createTime: '',
});

onMounted(() => {
    if (userStore.loginUser) {
        // 深拷贝一份数据给表单使用
        userForm.value = JSON.parse(JSON.stringify(userStore.loginUser));
    }
});

const showGenderSheet = ref(false);
const genderActions = [{ name: '保密', value: 0 }, { name: '男', value: 1 }, { name: '女', value: 2 }];
const genderText = computed(() => {
    const item = genderActions.find(a => a.value === userForm.value.gender);
    return item ? item.name : '保密';
});
const onGenderSelect = (item) => { userForm.value.gender = item.value; };

// 保存逻辑
const onSave = async () => {
    if (!userForm.value.nickname) return showToast('昵称不能为空');

    showLoadingToast({ message: '保存中...', forbidClick: true });

    try {
        const res = await updateUser(userForm.value);
        if (res.code === 0) {
            // ✅ 关键：更新 Pinia Store 里的数据
            // 这里不加 .value，直接把表单的新值传进去
            userStore.setLoginUser({ ...userForm.value });
            // 返回上级页面（我的）
            setTimeout(() => {
                showToast('资料更新成功');
            }, 800);
        } else {
            showToast(res.message || '更新失败');
        }
    } catch (error) {
        showToast('网络请求失败');
    }
};

const formatDate = (dateStr) => {
    if (!dateStr) return '无';
    const d = new Date(dateStr);
    return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
};

// 头像上传示例（简单演示预览效果）
const afterRead = (file) => {
    userForm.value.avatar = file.content;
};
</script>

<style lang="scss" scoped>
.profile-page {
    min-height: 100vh;
    background-color: #f7f8fa;
    .avatar-section {
        padding: 30px 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: #fff;
        margin-bottom: 12px;
        .upload-tip { margin-top: 8px; font-size: 12px; color: #969799; }
    }
}
</style>