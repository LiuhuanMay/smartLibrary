<template>
    <div class="borrow-page">
        <van-nav-bar
            title="我的借阅"
            left-arrow
            @click-left="$router.back()"
            fixed
            placeholder
        />

        <van-tabs v-model:active="activeTab" sticky offset-top="46" color="#1989fa">
            <van-tab title="全部" name="all"></van-tab>
            <van-tab title="待审核" name="0"></van-tab>
            <van-tab title="借阅中" name="1"></van-tab>
            <van-tab title="已归还" name="2"></van-tab>
            <van-tab title="逾期" name="3"></van-tab>
        </van-tabs>

        <div class="list-container">
            <div v-for="item in filteredList" :key="item.id" class="borrow-card">
                <div class="card-header">
                    <span class="book-name">{{ item.bookName }}</span>
                    <van-tag :type="getStatusType(item.status)">
                        {{ getStatusText(item.status) }}
                    </van-tag>
                </div>

                <div class="card-body">
                    <van-row class="info-row">
                        <van-col span="8" class="label">借阅时间：</van-col>
                        <van-col span="16" class="value">{{ formatDate(item.borrowTime) }}</van-col>
                    </van-row>
                    <van-row class="info-row">
                        <van-col span="8" class="label">应还时间：</van-col>
                        <van-col span="16" class="value">{{ formatDate(item.dueTime) }}</van-col>
                    </van-row>

                    <van-row v-if="item.status === 2" class="info-row">
                        <van-col span="8" class="label">归还时间：</van-col>
                        <van-col span="16" class="value">{{ formatDate(item.returnTime) }}</van-col>
                    </van-row>

                    <van-row v-if="item.status === 3" class="info-row">
                        <van-col span="8" class="label">逾期天数：</van-col>
                        <van-col span="16" class="value error-text">{{ item.overdueDays || 0 }} 天</van-col>
                    </van-row>
                </div>

                <div class="card-footer" v-if="item.status === 1">
                    <span class="status-hint">图书请按时归还</span>
                    <van-button
                        size="small"
                        plain
                        round
                        type="primary"
                        @click="handleRenew(item)"
                    >
                        归还图书
                    </van-button>
                </div>
            </div>

            <van-empty v-if="filteredList.length === 0" description="暂无相关借阅记录" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { showToast, showConfirmDialog } from 'vant'; // 记得引入确认对话框
import { myListBookBorrowVOByPage, bookReturn } from "@/api/book.js";
import 'vant/es/dialog/style';
import 'vant/es/toast/style';
// --- 状态定义 ---
const activeTab = ref('all');
const borrowList = ref([]);
const loading = ref(false);

// 获取列表数据的方法
const fetchList = async () => {
    loading.value = true;
    try {
        const res = await myListBookBorrowVOByPage({
            currentPage: 1,
            pageSize: 20 // 可以稍微设置大一点
        });
        if (res.code === 0) {
            borrowList.value = res.data.records;
        } else {
            showToast(res.message || '获取数据失败');
        }
    } catch (error) {
        console.error("加载借阅列表失败", error);
        showToast('网络请求异常');
    } finally {
        loading.value = false;
    }
};

// 过滤列表逻辑
const filteredList = computed(() => {
    if (activeTab.value === 'all') return borrowList.value;
    return borrowList.value.filter(item => item.status?.toString() === activeTab.value);
});

// 还书操作处理
const handleRenew = async (item) => {
    // 1. 弹出确认框
    showConfirmDialog({
        title: '归还确认',
        message: `确定要归还《${item.bookName}》吗？`,
    }).then(async () => {
        try {
            // 2. 调用归还接口
            const res = await bookReturn({ bookBorrowId: item.id });
            if (res.code === 0) {
                showToast('归还成功,等待管理员审核');
                // 3. 【关键】归还成功后，重新调用获取列表接口，刷新页面
                await fetchList();
            } else {
                showToast(res.message || '操作失败');
            }
        } catch (error) {
            showToast('请求异常');
        }
    }).catch(() => {
        // 取消归还，不做任何事
    });
};

// 状态映射文字与颜色 (保持不变)
const getStatusType = (status) => {
    const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' };
    return map[status] || 'default';
};
const getStatusText = (status) => {
    const map = { 0: '待审核', 1: '借阅中', 2: '已归还', 3: '已逾期' };
    return map[status] || '未知';
};
const formatDate = (val) => val ? val.substring(0, 10) : '-';

onMounted(() => {
    fetchList();
});
</script>

<style lang="scss" scoped>
.borrow-page {
    min-height: 100vh;
    background-color: #f7f8fa;

    .list-container {
        padding: 12px;
        padding-top: 8px;

        .borrow-card {
            background: #fff;
            border-radius: 12px;
            padding: 16px;
            margin-bottom: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 14px;

                .book-name {
                    font-size: 16px;
                    font-weight: 600;
                    color: #323233;
                    flex: 1;
                    margin-right: 10px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }

            .card-body {
                .info-row {
                    margin-bottom: 8px;
                    font-size: 13px;
                    line-height: 1.5;

                    .label {
                        color: #969799;
                    }

                    .value {
                        color: #323233;
                    }

                    .error-text {
                        color: #ee0a24;
                        font-weight: bold;
                    }
                }
            }

            .card-footer {
                margin-top: 14px;
                padding-top: 12px;
                border-top: 1px solid #f2f3f5;
                display: flex;
                justify-content: space-between;
                align-items: center;

                .status-hint {
                    font-size: 12px;
                    color: #969799;
                }
            }
        }
    }
}
</style>