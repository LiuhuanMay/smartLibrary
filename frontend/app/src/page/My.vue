<template>
    <div class="my-container">
        <div class="user-card">
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
                <van-col span="4">
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
<!--                <van-cell title="我的收藏" is-link icon="star-o" to="/my-favorites" />-->
<!--                <van-cell-->
<!--                    title="账号安全"-->
<!--                    is-link-->
<!--                    icon="shield-check-o"-->
<!--                    :label="userInfo.email || '未绑定邮箱'"-->
<!--                    to="/security"-->
<!--                />-->
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
import { ref } from 'vue';
import { showConfirmDialog, showToast } from 'vant';

// 模拟从后端/Vuex/LocalStorage获取的用户数据
// 对应你数据库的字段
const userInfo = ref({
    id: '20029',
    nickname: '书海行者',
    phone: '13800138000',
    avatar: '', // 留空则显示默认头像
    email: 'library_user@example.com',
    role: 0, // 0-普通用户 1-管理员
    gender: 1, // 1-男
    status: 1
});

const handleEditProfile = () => {
    showToast('跳转个人资料编辑');
};

const handleLogout = () => {
    showConfirmDialog({
        title: '提示',
        message: '确认退出登录吗？',
    }).then(() => {
        showToast('已退出');
    }).catch(() => {});
};
</script>

<style lang="scss" scoped>
.my-container {
    min-height: 100vh;
    background-color: #f7f8fa;
    padding-bottom: 60px; // 留出底部Tabbar的空间

    .user-card {
        background-color: #fff;
        padding: 30px 20px;
        margin-bottom: 12px;

        .user-info {
            padding-left: 15px;
            .nickname {
                font-size: 20px;
                font-weight: bold;
                color: #323233;
                margin-bottom: 4px;
            }
            .phone {
                font-size: 13px;
                color: #969799;
                margin-bottom: 6px;
            }
            .role-tag {
                font-size: 10px;
            }
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
        }
        .stat-label {
            font-size: 12px;
            color: #646566;
            margin-top: 4px;
        }
    }

    .logout-box {
        margin: 30px 16px;
    }
}
</style>