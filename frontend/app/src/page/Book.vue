<template>
    <div class="book-page-mobile">
        <!-- 粘性布局的头部，包含搜索和筛选 -->
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
                    <van-icon name="filter-o" size="20"/>
                    <span>筛选</span>
                </div>
            </div>
        </van-sticky>

        <!-- 图书列表 -->
        <van-list
            v-model:loading="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="onLoad"
            class="book-list"
        >
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
                    <van-tag plain type="primary" style="margin-left: 5px;">
                        剩余: {{ book.availableStock }}
                    </van-tag>
                </template>
                <template #num>
                    <span class="book-author">作者: {{ book.author || '佚名' }}</span>
                </template>
            </van-card>
        </van-list>

        <!-- 空状态 -->
        <van-empty v-if="!loading && list.length === 0" description="暂无相关图书"/>

        <!-- 筛选弹出层 -->
        <van-popup v-model:show="showFilter" position="right" :style="{ width: '85%', height: '100%' }">
            <div class="filter-popup">
                <h3 class="filter-title">高级筛选</h3>
                <van-form @submit="onFilterConfirm">
                    <van-cell-group inset>
                        <van-field
                            v-model="filterParams.author"
                            name="author"
                            label="作者"
                            placeholder="请输入作者名"
                            clearable
                        />
                        <van-field
                            v-model="filterParams.publisher"
                            name="publisher"
                            label="出版社"
                            placeholder="请输入出版社"
                            clearable
                        />
                        <van-field
                            v-model="filterParams.availableStock"
                            name="availableStock"
                            label="是否可借"
                        >
                            <template #input>
                                <van-radio-group v-model="filterParams.availableStock" direction="horizontal">
                                    <van-radio name="1">可借</van-radio>
                                    <van-radio name="0">不可借</van-radio>
                                </van-radio-group>
                            </template>
                        </van-field>
                        <van-field
                            is-link
                            readonly
                            name="date"
                            label="出版日期"
                            :placeholder="filterParams.publishDate || '点击选择日期'"
                            @click="showDatePicker = true"
                        />
                        <van-field name="radio" label="日期范围" v-if="filterParams.publishDate">
                            <template #input>
                                <van-radio-group v-model="filterParams.publishDateType" direction="horizontal">
                                    <van-radio name="1">之后</van-radio>
                                    <van-radio name="0">之前</van-radio>
                                </van-radio-group>
                            </template>
                        </van-field>
                    </van-cell-group>
                    <div class="form-buttons">
                        <van-button round block type="default" @click="onResetFilter">重置</van-button>
                        <van-button round block type="primary" native-type="submit">确认</van-button>
                    </div>
                </van-form>
            </div>
        </van-popup>

        <!-- 日期选择器 -->
        <van-popup v-model:show="showDatePicker" position="bottom">
            <van-date-picker
                @confirm="onDateConfirm"
                @cancel="showDatePicker = false"
                title="选择出版日期"
            />
        </van-popup>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { listBookVOByPage } from "@/api/book.js";
import { showToast } from 'vant';

// 默认封面
const defaultCover = 'https://img.yzcdn.cn/vant/cat.jpeg';

// --- 响应式状态定义 ---
const list = ref([]);
const loading = ref(false);
const finished = ref(false);


const showFilter = ref(false);
const showDatePicker = ref(false);



// 基础查询参数（搜索框和分页）
const queryParams = reactive({
    currentPage: 1,
    pageSize: 10,
    bookName: "",
});

// 筛选抽屉里的参数
const filterParams = reactive({
    author: "",
    publisher: "",
    publishDate: "",
    publishDateType: "", // 0：之前  1：之后
    availableStock: "", // 1:可借 0不可借
});

const onLoad = async () => {
    loading.value = true;
    const params = { ...queryParams, ...filterParams };
    const res = await listBookVOByPage(params);
    console.log(res.data)
    if(res.code==0) {
        list.value.push(...res.data.records)
        finished.value = true
    }else{
        showToast('数据加载失败');
        finished.value = true
    }
};


// 搜索触发（包括清空）
const onSearch = () => {
    // 重置列表状态
    queryParams.currentPage = 1;
    list.value = [];
    finished.value = false;
    // 重新加载数据
    onLoad();
};

// 确认筛选
const onFilterConfirm = () => {
    showFilter.value = false;
    onSearch();
};

// 重置筛选条件
const onResetFilter = () => {
    filterParams.author = "";
    filterParams.publisher = "";
    filterParams.publishDate = "";
    filterParams.publishDateType = "";
    filterParams.availableStock = "";
    showFilter.value = false;
    onSearch(); // 重置后立即重新搜索
};

// 日期选择确认
const onDateConfirm = ({ selectedValues }) => {
    filterParams.publishDate = selectedValues.join('-');
    showDatePicker.value = false;
};

</script>

<style lang="scss" scoped>
.book-page-mobile {
    background-color: #f7f8fa;
    min-height: 100vh;
}

.header-bar {
    display: flex;
    align-items: center;
    background-color: #fff;
    padding: 5px;

    .van-search {
        flex-grow: 1;
    }

    .filter-trigger {
        padding: 0 10px;
        display: flex;
        flex-direction: column;
        align-items: center;
        color: #646566;
        font-size: 10px;
    }
}

.book-list {
    padding: 10px;
}

.book-item {
    margin-bottom: 10px;
    background-color: #fff;

    // 自定义作者样式
    .book-author {
        font-size: 12px;
        color: #969799;
    }

    // 覆盖 Vant Card 的标题样式
    :deep(.van-card__title) {
        font-size: 16px;
        font-weight: bold;
        line-height: 1.4;
        max-height: 44px; // 最多显示两行
    }
}

.filter-popup {
    padding: 20px 0;
    height: 100%;
    display: flex;
    flex-direction: column;

    .filter-title {
        font-size: 18px;
        text-align: center;
        margin: 0 0 20px 0;
    }

    .van-cell-group {
        flex-grow: 1;
    }

    .form-buttons {
        display: flex;
        padding: 20px 16px 0;
        gap: 10px;
    }
}
</style>
