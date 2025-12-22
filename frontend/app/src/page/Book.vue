<template>
    <div class="book-page-mobile">
        <!-- 顶部搜索 + 筛选 -->
        <van-sticky>
            <div class="header-bar">
                <van-search
                    v-model="queryParams.bookName"
                    shape="round"
                    placeholder="请输入书名关键词"
                    @search="onSearch"
                    @clear="onSearch"
                />
                <div class="filter-trigger" @click="showFilter = true">
                    <van-icon name="filter-o" size="20" />
                    <span>筛选</span>
                </div>
            </div>
        </van-sticky>

        <!-- 图书列表 -->
        <div class="book-list">
            <van-card
                v-for="book in list"
                :key="book.id"
                :price="book.price ? book.price.toFixed(2) : '0.00'"
                :desc="`出版社: ${book.publisher || '未知'}`"
                :title="book.bookName"
                :thumb="book.cover || defaultCover"
                class="book-item"
            >
                <template #tags>
                    <van-tag :type="book.availableStock > 0 ? 'success' : 'danger'">
                        {{ book.availableStock > 0 ? '可借阅' : '已借完' }}
                    </van-tag>
                    <van-tag plain type="primary" style="margin-left: 5px">
                        剩余: {{ book.availableStock }}
                    </van-tag>
                </template>
                <template #num>
                    <span class="book-author">作者: {{ book.author || '佚名' }}</span>
                </template>
            </van-card>
        </div>

        <!-- 空状态 -->
        <van-empty v-if="!loading && list.length === 0" description="暂无相关图书" />

        <!-- 底部分页（固定在 Tabbar 上方） -->
        <van-pagination
            mode="simple"
            v-model="queryParams.currentPage"
            :total-items="page.total"
            :items-per-page="queryParams.pageSize"
            @change="onPageChange"
            class="pagination-bottom"
        />

        <!-- 筛选弹窗 -->
        <van-popup v-model:show="showFilter" position="bottom" round :style="{ width: '100%' }">
            <div class="filter-popup">
                <van-form @submit="onFilterConfirm">
                    <van-cell-group inset>
                        <van-field
                            v-model="filterParams.author"
                            label="作者"
                            placeholder="请输入作者名"
                            clearable
                        />
                        <van-field
                            v-model="filterParams.publisher"
                            label="出版社"
                            placeholder="请输入出版社"
                            clearable
                        />
                        <van-field
                            v-model="availableStockText"
                            is-link
                            readonly
                            label="是否可借"
                            placeholder="请选择"
                            @click="showAvailableStockPicker = true"
                        />

                        <van-field
                            is-link
                            readonly
                            label="出版日期"
                            :placeholder="filterParams.publishDate || '点击选择日期'"
                            @click="showDatePicker = true"
                        />

                        <van-field v-if="filterParams.publishDate" label="日期范围">
                            <template #input>
                                <van-radio-group v-model="filterParams.publishDateType" direction="horizontal">
                                    <van-radio name="1">之后</van-radio>
                                    <van-radio name="0">之前</van-radio>
                                </van-radio-group>
                            </template>
                        </van-field>

                        <!-- 每页条数 -->
                        <van-field
                            v-model="pageSizeText"
                            is-link
                            readonly
                            label="每页条数"
                            placeholder="请选择"
                            @click="showPageSizePicker = true"
                        />
                    </van-cell-group>

                    <div class="form-buttons">
                        <van-button round block @click="onResetFilter">重置</van-button>
                        <van-button round block type="primary" native-type="submit">确认</van-button>
                    </div>
                </van-form>
            </div>
        </van-popup>

        <!-- 日期选择器 -->
        <van-popup v-model:show="showDatePicker" position="bottom">
            <van-date-picker
                title="选择出版日期"
                @confirm="onDateConfirm"
                @cancel="showDatePicker = false"
            />
        </van-popup>

        <van-popup v-model:show="showAvailableStockPicker" position="bottom">
            <van-picker
                :columns="availableStockOptions"
                @confirm="onAvailableStockConfirm"
                @cancel="showAvailableStockPicker = false"
            />
        </van-popup>

        <van-popup v-model:show="showPageSizePicker" position="bottom">
            <van-picker
                :columns="pageSizeOptions"
                @confirm="onPageSizeConfirm"
                @cancel="showPageSizePicker = false"
            />
        </van-popup>

        <!-- 底部 Tabbar -->
        <van-tabbar fixed>
            <van-tabbar-item name="home" icon="home-o">图书精选</van-tabbar-item>
            <van-tabbar-item name="search" icon="search">通知公告</van-tabbar-item>
            <van-tabbar-item name="friends" icon="friends-o">智能助手</van-tabbar-item>
            <van-tabbar-item name="setting" icon="setting-o">我的</van-tabbar-item>
        </van-tabbar>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { listBookVOByPage } from '@/api/book'
import { showToast } from 'vant'

// 默认封面
const defaultCover = 'https://img.yzcdn.cn/vant/cat.jpeg'

// 列表数据
const list = ref([])
const loading = ref(false)

// 弹窗状态
const showFilter = ref(false)
const showDatePicker = ref(false)
const showAvailableStockPicker = ref(false)
const showPageSizePicker = ref(false)
const availableStockText = ref('')
const pageSizeText = ref('')

// 分页参数
const queryParams = reactive({
    currentPage: 1,
    pageSize: 6,
    bookName: ''
})

// 后端分页信息
const page = reactive({
    total: 0
})

// 筛选参数
const filterParams = reactive({
    author: '',
    publisher: '',
    publishDate: '',
    publishDateType: '',
    availableStock: ''
})

const availableStockOptions = [
    { text: '可借', value: '1' },
    { text: '不可借', value: '0' }
]

const pageSizeOptions = [2, 4, 6, 10].map((value) => ({
    text: `${value} 条/页`,
    value
}))

pageSizeText.value =
    pageSizeOptions.find((item) => item.value === queryParams.pageSize)?.text || ''

// 获取数据
const fetchList = async () => {
    loading.value = true
    const params = { ...queryParams, ...filterParams }
    const res = await listBookVOByPage(params)
    if (res.code === 0) {
        // 确保 key 唯一，避免 Vue 警告
        list.value = res.data.records.map((book, index) => ({ ...book, _uid: book.id + '-' + index }))
        page.total = res.data.total
    } else {
        showToast('数据加载失败')
    }
    loading.value = false
}

// 搜索
const onSearch = () => {
    queryParams.currentPage = 1
    fetchList()
}

// 翻页
const onPageChange = (pageNum) => {
    queryParams.currentPage = pageNum
    fetchList()
}

// 筛选确认
const onFilterConfirm = () => {
    showFilter.value = false
    queryParams.currentPage = 1
    fetchList()
}

// 重置筛选
const onResetFilter = () => {
    Object.assign(filterParams, {
        author: '',
        publisher: '',
        publishDate: '',
        publishDateType: '',
        availableStock: ''
    })
    showFilter.value = false
    queryParams.currentPage = 1
    fetchList()
}

// 日期选择
const onDateConfirm = ({ selectedValues }) => {
    filterParams.publishDate = selectedValues.join('-')
    showDatePicker.value = false
}

const onAvailableStockConfirm = ({ selectedOptions }) => {
    if (selectedOptions && selectedOptions[0]) {
        filterParams.availableStock = selectedOptions[0].value
        availableStockText.value = selectedOptions[0].text
    }
    showAvailableStockPicker.value = false
}

const onPageSizeConfirm = ({ selectedOptions }) => {
    if (selectedOptions && selectedOptions[0]) {
        queryParams.pageSize = selectedOptions[0].value
        pageSizeText.value = selectedOptions[0].text
        queryParams.currentPage = 1
        fetchList()
    }
    showPageSizePicker.value = false
}

// 初始化加载
fetchList()
</script>

<style scoped lang="scss">
.book-page-mobile {
    background: #f7f8fa;
    min-height: 100vh;
    padding-bottom: 110px; /* 50px tabbar + 60px 分页条 */
}

.header-bar {
    display: flex;
    align-items: center;
    background: #fff;
    padding: 5px;

    .van-search {
        flex: 1;
    }

    .filter-trigger {
        padding: 0 10px;
        display: flex;
        flex-direction: column;
        align-items: center;
        font-size: 10px;
        color: #646566;
    }
}

.book-list {
    padding: 10px;
}

.book-item {
    margin-bottom: 10px;

    .book-author {
        font-size: 12px;
        color: #969799;
    }

    :deep(.van-card__title) {
        font-size: 16px;
        font-weight: bold;
        line-height: 1.4;
        max-height: 44px;
    }
}

.pagination-bottom {
    position: fixed;
    left: 0;
    bottom: 50px; /* Tabbar 高度 */
    width: 100%;
    background: #fff;
    padding: 8px 0;
    z-index: 99;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

.filter-popup {
    padding: 10px 0;

    .van-cell-group {
        padding: 0 16px;
    }

    .form-buttons {
        display: flex;
        gap: 12px;
        padding: 16px;
    }
}
</style>
