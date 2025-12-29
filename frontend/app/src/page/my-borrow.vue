<template>
    <div class="borrow-page">
        <van-nav-bar title="我的借阅" left-arrow @click-left="$router.back()" fixed placeholder />

        <van-tabs v-model:active="activeTab" sticky offset-top="46">
            <van-tab title="全部" name="all"></van-tab>
            <van-tab title="借阅中" name="0"></van-tab>
            <van-tab title="已归还" name="1"></van-tab>
            <van-tab title="逾期" name="2"></van-tab>
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

                    <van-row v-if="item.returnTime" class="info-row">
                        <van-col span="8" class="label">归还时间：</van-col>
                        <van-col span="16" class="value">{{ formatDate(item.returnTime) }}</van-col>
                    </van-row>

                    <van-row v-if="item.status === 2" class="info-row">
                        <van-col span="8" class="label">逾期天数：</van-col>
                        <van-col span="16" class="value error-text">{{ item.overdueDays }} 天</van-col>
                    </van-row>
                </div>

                <div class="card-footer">
                    <div class="review-status">
                        审核状态：
                        <span :class="'review-' + item.reviewStatus">
              {{ getReviewText(item.reviewStatus) }}
            </span>
                    </div>
                    <van-button
                        v-if="item.status === 0 && item.reviewStatus === 1"
                        size="small"
                        plain
                        round
                        type="primary"
                    >续借申请</van-button>
                </div>
            </div>

            <van-empty v-if="filteredList.length === 0" description="暂无相关借阅记录" />
        </div>
    </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {myListBookBorrowVOByPage} from "@/api/book.js";
onMounted(async ()=>{
    const res = await myListBookBorrowVOByPage({currentPage:1, pageSize:10})
    borrowList.value = res.data.records
})
const activeTab = ref('all');

// 模拟后端返回的数据 List
const borrowList = ref([]);

// 过滤逻辑
const filteredList = computed(() => {
    if (activeTab.value === 'all') return borrowList.value;
    return borrowList.value.filter(item => item.status.toString() === activeTab.value);
});

// 状态样式映射
const getStatusType = (status) => {
    const map = { 0: 'primary', 1: 'success', 2: 'danger' };
    return map[status] || 'default';
};

const getStatusText = (status) => {
    const map = { 0: '借阅中', 1: '已归还', 2: '已逾期' };
    return map[status];
};

const getReviewText = (status) => {
    const map = { 0: '待审核', 1: '审核通过', 2: '审核拒绝' };
    return map[status];
};

const formatDate = (val) => val ? val.substring(0, 10) : '-';
</script>

<style lang="scss" scoped>
.borrow-page {
    min-height: 100vh;
    background-color: #f7f8fa;

    .list-container {
        padding: 12px;

        .borrow-card {
            background: #fff;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 12px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);

            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 12px;
                .book-name {
                    font-size: 16px;
                    font-weight: bold;
                    color: #323233;
                }
            }

            .card-body {
                .info-row {
                    margin-bottom: 6px;
                    font-size: 13px;
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
                margin-top: 12px;
                padding-top: 12px;
                border-top: 1px solid #f2f3f5;
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 12px;

                .review-status {
                    color: #646566;
                    .review-0 { color: #ff976a; }
                    .review-1 { color: #07c160; }
                    .review-2 { color: #ee0a24; }
                }
            }
        }
    }
}
</style>