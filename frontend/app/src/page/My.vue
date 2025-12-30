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
                <van-grid-item to="/my-borrow">
                    <div class="stat-num">{{ stats.borrowingCount }}</div>
                    <div class="stat-label">在借中</div>
                </van-grid-item>
                <van-grid-item to="/my-borrow">
                    <div class="stat-num">{{ stats.totalBorrowed }}</div>
                    <div class="stat-label">累计借阅</div>
                </van-grid-item>
                <van-grid-item to="/my-borrow">
                    <div class="stat-num" :class="{ 'error-text': stats.overdueCount > 0 }">
                        {{ stats.overdueCount }}
                    </div>
                    <div class="stat-label">逾期未还</div>
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
import { computed, ref, onMounted } from 'vue';
import { showConfirmDialog, showToast } from 'vant';
import { userLoginUserStore } from "@/store/userStore.js";
import { myListBookBorrowVOByPage } from "@/api/book.js"; // 引入借阅接口
import { useRouter } from 'vue-router';

const userStore = userLoginUserStore();
const router = useRouter();

// 用户信息
const userInfo = computed(() => userStore.loginUser || {});

// --- 统计逻辑开始 ---
const stats = ref({
    borrowingCount: 0, // 在借中 (status = 1)
    totalBorrowed: 0,  // 累计总数
    overdueCount: 0    // 逾期 (status = 3)
});

const fetchStats = async () => {
    try {
        // 请求接口，设置足够大的 pageSize 以便统计全量数据
        const res = await myListBookBorrowVOByPage({ currentPage: 1, pageSize: 20 });
        if (res.code === 0) {
            const list = res.data.records || [];

            stats.value.totalBorrowed = res.data.total || list.length;

            // 统计 status 为 1 的数量（借阅中）
            stats.value.borrowingCount = list.filter(item => item.status === 1).length;

            // 统计 status 为 3 的数量（逾期）
            stats.value.overdueCount = list.filter(item => item.status === 3).length;
        }
    } catch (error) {
        console.error("统计数据获取失败", error);
    }
};
// --- 统计逻辑结束 ---

onMounted(() => {
    fetchStats();
});

const goToProfile = () => {
    router.push('/profile');
};

const handleLogout = () => {
    showConfirmDialog({
        title: '提示',
        message: '确认退出登录吗？',
    }).then(() => {
        userStore.logout();
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
        .user-info {
            padding-left: 15px;
            .nickname { font-size: 20px; font-weight: bold; color: #323233; margin-bottom: 4px; }
            .phone { font-size: 13px; color: #969799; margin-bottom: 6px; }
        }
    }

    .stat-grid {
        margin: 0 16px 12px;
        background: #fff;
        border-radius: 8px;
        overflow: hidden;
        .stat-num {
            font-size: 18px;
            font-weight: bold;
            color: #1989fa;
            text-align: center;
            // 逾期数字变红
            &.error-text { color: #ee0a24; }
        }
        .stat-label { font-size: 12px; color: #646566; margin-top: 4px; text-align: center;}
    }

    .logout-box { margin: 30px 16px; }
}
</style>