<template>
    <div class="my-container">
        <div class="user-card" @click="goToProfile">
            <van-row align="center" justify="center">
                <van-col span="6">
                    <van-image
                        round
                        width="70"
                        height="70"
                        :src="userInfo.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
                        fit="cover"
                    />
                </van-col>
                <van-col span="14" class="user-info">
                    <div class="nickname">{{ userInfo.nickname || '未设置昵称' }}</div>
                    <div class="phone">账号: {{ userInfo.phone }}</div>
                    <van-tag type="primary" size="medium" class="role-tag">
                        {{ userInfo.role === 1 ? '系统管理员' : '普通读者' }}
                    </van-tag>
                </van-col>
                <van-col span="4" class="arrow-col">
                    <van-icon name="arrow" color="#ccc" />
                </van-col>
            </van-row>
        </div>

        <div class="stat-grid">
            <van-grid :column-num="3" :border="false">
                <van-grid-item>
                    <div class="stat-num">12</div>
                    <div class="stat-label">在借中</div>
                </van-grid-item>
                <van-grid-item>
                    <div class="stat-num">58</div>
                    <div class="stat-label">累计借阅</div>
                </van-grid-item>
                <van-grid-item>
                    <div class="stat-num">0</div>
                    <div class="stat-label">待缴违约</div>
                </van-grid-item>
            </van-grid>
        </div>

        <div class="menu-list">
            <van-cell-group inset>
                <van-cell title="个人资料" is-link icon="user-o" to="/profile" />
                <van-cell title="我的借阅" is-link icon="orders-o" to="/my-borrow" />
            </van-cell-group>

            <van-cell-group inset style="margin-top: 12px;">
                <van-cell title="帮助与反馈" is-link icon="question-o" to="/help" />
                <van-cell title="关于图书馆" is-link icon="info-o" to="/about" />
            </van-cell-group>
        </div>

        <div class="logout-box">
            <van-button block round type="danger" @click="handleLogout">退出登录</van-button>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { showConfirmDialog, showToast } from 'vant';
import { userLoginUserStore } from "@/store/userStore.js";
import { useRouter } from 'vue-router';
import 'vant/es/toast/style';
import 'vant/es/dialog/style';

const userStore = userLoginUserStore();
const router = useRouter();

// 使用 computed 实时关联 Store 中的数据
// 当 Profile 页面修改 store 时，这里的 userInfo 会自动更新
const userInfo = computed(() => {
    return userStore.loginUser || {
        nickname: '未获取信息',
        phone: '请重新登录',
        avatar: '',
        role: 0
    };
});

const goToProfile = () => {
    router.push('/profile');
};

const handleLogout = () => {
    showConfirmDialog({
        title: '提示',
        message: '确认退出登录吗？',
    }).then(() => {
        userStore.logout(); // 调用 Pinia 里的退出逻辑
        showToast('已安全退出');
        router.replace('/login');
    }).catch(() => {});
};
</script>

<style lang="scss" scoped>
.my-container {
    min-height: 100vh;
    background-color: #f7f8fa;
    padding-bottom: 60px;

    .user-card {
        background-color: #fff;
        padding: 30px 20px;
        margin-bottom: 12px;
        cursor: pointer;
        transition: background-color 0.1s;

        &:active { background-color: #f2f3f5; }

        .user-info {
            padding-left: 15px;
            .nickname { font-size: 20px; font-weight: bold; color: #323233; margin-bottom: 4px; }
            .phone { font-size: 13px; color: #969799; margin-bottom: 6px; }
            .role-tag { font-size: 10px; }
        }
        .arrow-col { display: flex; align-items: center; justify-content: flex-end; }
    }

    .stat-grid {
        margin: 0 16px 12px;
        background: #fff;
        border-radius: 8px;
        overflow: hidden;
        .stat-num { font-size: 18px; font-weight: bold; color: #1989fa; text-align: center;}
        .stat-label { font-size: 12px; color: #646566; margin-top: 4px; text-align: center;}
    }

    .logout-box { margin: 30px 16px; }
}
</style>