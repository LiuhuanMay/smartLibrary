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
            <van-field
                    v-model="userForm.nickname"
                    label="昵称"
                    placeholder="请输入昵称"
                    input-align="right"
            />
            <van-field
                    v-model="userForm.phone"
                    label="手机号"
                    readonly
                    disabled
                    placeholder="手机号不可修改"
                    input-align="right"
            />
            <van-field
                    v-model="userForm.email"
                    label="邮箱"
                    placeholder="请输入邮箱"
                    input-align="right"
            />
            <van-cell
                    title="性别"
                    is-link
                    :value="genderText"
                    @click="showGenderSheet = true"
            />
            <van-field
                    v-model.number="userForm.age"
                    type="digit"
                    label="年龄"
                    placeholder="请输入年龄"
                    input-align="right"
            />
            <van-cell
                    title="账号角色"
                    :value="userForm.role === 1 ? '管理员' : '普通用户'"
            />
            <van-cell
                    title="注册时间"
                    :value="formatDate(userForm.create_time)"
            />
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
import { ref, computed } from 'vue';
import { showToast, showLoadingToast } from 'vant';

// 1. 初始化表单数据（对应数据库字段）
const userForm = ref({
    id: 20029,
    nickname: '书海行者',
    phone: '13800138000',
    email: 'library_user@example.com',
    avatar: '',
    age: 25,
    gender: 1, // 0未知 1男 2女
    role: 0,
    create_time: '2025-12-01T08:00:00'
});

// 2. 性别相关逻辑
const showGenderSheet = ref(false);
const genderActions = [
    { name: '保密', value: 0 },
    { name: '男', value: 1 },
    { name: '女', value: 2 },
];

const genderText = computed(() => {
    const item = genderActions.find(a => a.value === userForm.value.gender);
    return item ? item.name : '未知';
});

const onGenderSelect = (item) => {
    userForm.value.gender = item.value;
};

// 3. 头像上传模拟
const afterRead = (file) => {
    showLoadingToast({ message: '上传中...', forbidClick: true });
    // 模拟接口上传
    setTimeout(() => {
        userForm.value.avatar = file.content; // 实际开发中这里应该是后端返回的URL
        showToast('头像更新成功');
    }, 1000);
};

// 4. 保存提交
const onSave = () => {
    // 简单校验
    if (!userForm.value.nickname) return showToast('昵称不能为空');
    if (userForm.value.email && !/^\w+@\w+\.\w+$/.test(userForm.value.email)) {
        return showToast('邮箱格式不正确');
    }

    console.log('提交给后端的数据:', userForm.value);
    showLoadingToast({ message: '保存中...', forbidClick: true });

    setTimeout(() => {
        showToast('资料更新成功');
        // router.back(); // 保存成功后返回
    }, 800);
};

// 工具：日期格式化
const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
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

    .upload-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #969799;
    }
  }

  :deep(.van-cell-group--inset) {
    margin: 0 12px;
  }
}
</style>