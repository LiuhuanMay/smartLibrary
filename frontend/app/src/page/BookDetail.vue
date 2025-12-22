<template>
    <div class="book-detail-page">
        <van-nav-bar title="图书详情" left-arrow @click-left="onBack" />

        <div class="detail-header">
            <img :src="book.cover || defaultCover" alt="封面" class="cover" />
            <div class="basic-info">
                <div class="title">{{ book.bookName }}</div>
                <div class="author">作者：{{ book.author || '佚名' }}</div>
                <div class="publisher">出版社：{{ book.publisher || '未知' }}</div>
                <div class="meta-row">
                    <van-tag type="primary" plain v-if="book.language">
                        {{ book.language }}
                    </van-tag>
                    <span class="price" v-if="book.price !== null">
                        ￥{{ priceText }}
                    </span>
                </div>
            </div>
        </div>

        <div class="stats-card">
            <div class="stats-header">
                <van-icon name="bar-chart-o" class="stats-icon" />
                <span class="stats-title">借阅统计</span>
            </div>
            <div class="stats-grid">
                <div class="stat-item stat-total">
                    <div class="stat-value">{{ book.totalStock }}</div>
                    <div class="stat-label">总库存</div>
                </div>
                <div class="stat-item stat-available">
                    <div class="stat-value">{{ book.availableStock }}</div>
                    <div class="stat-label">当前可借</div>
                </div>
                <div class="stat-item stat-borrowed">
                    <div class="stat-value">{{ book.borrowedCount }}</div>
                    <div class="stat-label">当前借出</div>
                </div>
                <div class="stat-item stat-total-borrowed">
                    <div class="stat-value">{{ book.totalBorrowedCount }}</div>
                    <div class="stat-label">累计借阅</div>
                </div>
            </div>
        </div>

        <div class="intro-card">
            <div class="intro-title">图书简介</div>
            <div class="intro-content">
                {{ book.bookIntroduction || '暂无简介' }}
            </div>
        </div>

        <div class="bottom-bar">
            <div class="stock-status">
                <van-tag :type="book.availableStock > 0 ? 'success' : 'danger'">
                    {{ book.availableStock > 0 ? '可借阅' : '当前暂无可借库存' }}
                </van-tag>
            </div>
            <div class="due-row" @click="showDuePicker = true">
                <span class="due-label">归还日期：</span>
                <span class="due-value">
                    {{ dueDate || '请选择归还日期' }}
                </span>
            </div>
            <van-button
                type="primary"
                round
                block
                :disabled="book.availableStock <= 0"
                @click="onBorrow"
            >
                申请借阅
            </van-button>
        </div>

        <van-popup v-model:show="showDuePicker" position="bottom">
            <van-date-picker
                title="选择应还日期"
                @confirm="onDueConfirm"
                @cancel="showDuePicker = false"
            />
        </van-popup>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showNotify } from 'vant'
import { addBookBorrow } from '@/api/book.js'
import { useBookStore } from '@/store/bookStore.js'

import 'vant/es/notify/style'
const router = useRouter()
const defaultCover = 'https://img.yzcdn.cn/vant/cat.jpeg'

const bookStore = useBookStore()

const showDuePicker = ref(false)
const dueDate = ref('')

const book = computed(() => {
    const source = bookStore.currentBook
    const toNumber = (val) => {
        if (val === undefined || val === null || val === '') {
            return 0
        }
        const n = Number(val)
        return Number.isNaN(n) ? 0 : n
    }

    if (!source) {
        return {
            id: '',
            bookName: '图书信息获取中',
            author: '',
            cover: '',
            publisher: '',
            bookIntroduction: '',
            language: '',
            price: null,
            availableStock: 0,
            totalStock: 0,
            borrowedCount: 0,
            totalBorrowedCount: 0
        }
    }

    return {
        id: source.id || '',
        bookName: source.bookName || '',
        author: source.author || '',
        cover: source.cover || '',
        publisher: source.publisher || '',
        bookIntroduction: source.bookIntroduction || '',
        language: source.language || '',
        price: source.price !== undefined ? toNumber(source.price) : null,
        availableStock: toNumber(source.availableStock),
        totalStock: toNumber(source.totalStock),
        borrowedCount: toNumber(source.borrowedCount),
        totalBorrowedCount: toNumber(source.totalBorrowedCount)
    }
})

const priceText = computed(() => {
    if (book.value.price === null) {
        return '--'
    }
    return book.value.price.toFixed(2)
})

const onBack = () => {
    router.back()
}

const onBorrow = async () => {
    if (!dueDate.value) {
        showToast('请选择应还日期')
        return
    }
    const due = new Date(dueDate.value + 'T23:59:59')

    if (!book.value.id) {
        showToast('图书信息异常，请返回重新进入')
        return
    }
    if (book.value.availableStock <= 0) {
        showToast('当前暂无可借库存')
        return
    }
    const data = {
        bookId: book.value.id,
        dueTime: due.toISOString(),
    }
    try {
        const res = await addBookBorrow(data)
        if (res.code === 0) {
            showNotify({
                type: 'primary',
                message: '已提交借阅申请，请等待邮箱通知'
            })
        } else {
            showToast(res.message || '借阅申请失败')
        }
    } catch (e) {
        showToast('借阅申请失败，请稍后重试')
    }
}

const onDueConfirm = ({ selectedValues }) => {
    dueDate.value = selectedValues.join('-')
    showDuePicker.value = false
}

</script>

<style scoped lang="scss">
.book-detail-page {
    min-height: 100vh;
    background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
    padding-bottom: 80px;
}

:deep(.van-nav-bar) {
    background: transparent;
}

::v-deep(.van-nav-bar__title) {
    font-weight: 600;
}

.detail-header {
    display: flex;
    padding: 16px 16px 12px;
    gap: 14px;
}

.cover {
    width: 90px;
    height: 120px;
    border-radius: 8px;
    object-fit: cover;
    box-shadow: 0 6px 16px rgba(74, 144, 226, 0.25);
    background: #f5f7ff;
}

.basic-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.title {
    font-size: 18px;
    font-weight: 600;
    color: #222a3a;
    line-height: 1.4;
}

.author,
.publisher {
    font-size: 13px;
    color: #8a8ea6;
    margin-top: 4px;
}

.meta-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 6px;
}

.price {
    font-size: 18px;
    font-weight: 600;
    color: #ff9f43;
}

.stats-card {
    margin: 4px 16px 12px;
    border-radius: 16px;
    box-shadow: 0 8px 18px rgba(74, 144, 226, 0.08);
    background: #ffffff;
    padding: 12px 14px 10px;
}

.stats-header {
    display: flex;
    align-items: center;
    padding-bottom: 8px;
    border-bottom: 1px solid #f3f4fb;
    margin-bottom: 6px;
}

.stats-icon {
    font-size: 16px;
    color: #8c88ff;
    margin-right: 6px;
}

.stats-title {
    font-size: 14px;
    color: #555c73;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    padding-top: 4px;
}

.stat-item {
    padding: 10px 4px;
    text-align: center;
}

.stat-value {
    font-size: 20px;
    font-weight: 600;
    line-height: 1.2;
}

.stat-total .stat-value {
    color: #4a6bff;
}

.stat-available .stat-value {
    color: #19be6b;
}

.stat-borrowed .stat-value {
    color: #ff9f43;
}

.stat-total-borrowed .stat-value {
    color: #8c88ff;
}

.stat-label {
    margin-top: 4px;
    font-size: 12px;
    color: #9aa4c0;
}

.intro-card {
    margin: 8px 16px 0;
    padding: 14px 14px 18px;
    border-radius: 16px;
    background: #ffffff;
    box-shadow: 0 8px 18px rgba(74, 144, 226, 0.08);
}

.intro-title {
    font-size: 15px;
    font-weight: 600;
    color: #222a3a;
    margin-bottom: 8px;
}

.intro-content {
    font-size: 14px;
    line-height: 1.6;
    color: #525a6b;
    white-space: pre-wrap;
}

.bottom-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    padding: 10px 16px 16px;
    background: rgba(255, 255, 255, 0.98);
    box-shadow: 0 -4px 18px rgba(74, 144, 226, 0.12);
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.stock-status {
    display: flex;
    justify-content: flex-start;
}

.due-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 10px;
    border-radius: 999px;
    background: #f5f7ff;
    cursor: pointer;
}

.due-label {
    font-size: 13px;
    color: #7a86a8;
}

.due-value {
    font-size: 13px;
    color: #3e82f7;
}

.bottom-bar :deep(.van-button) {
    background: linear-gradient(135deg, #5a9bff 0%, #3e82f7 100%);
    border: none;
    box-shadow: 0 8px 15px rgba(74, 144, 226, 0.3);
}
</style>
