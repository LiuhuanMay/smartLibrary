<template>
    <div class="recommend-page">
        <van-nav-bar
            title="智能推荐"
            left-arrow
            @click-left="router.back()"
        />

        <div class="form-panel">
            <van-cell-group inset class="form-card">
                <van-field
                    v-model="recommendForm.hobby"
                    label="爱好"
                    placeholder="例如：历史、推理、编程"
                    clearable
                />
                <van-field
                    v-model="recommendForm.major"
                    label="专业"
                    placeholder="例如：计算机、法学、教育学"
                    clearable
                />
                <van-field
                    v-model="recommendForm.goal"
                    label="目标"
                    placeholder="例如：入门学习、论文参考、放松阅读"
                    clearable
                />
            </van-cell-group>

            <div class="tag-card">
                <div class="tag-header">
                    <span>热门方向</span>
                    <span class="tag-tip">可多选</span>
                </div>
                <div class="tag-list">
                    <van-tag
                        v-for="tag in quickTags"
                        :key="tag"
                        plain
                        size="large"
                        :type="selectedTags.includes(tag) ? 'primary' : 'default'"
                        class="interest-tag"
                        @click="toggleQuickTag(tag)"
                    >
                        {{ tag }}
                    </van-tag>
                </div>
            </div>

            <div class="action-row">
                <van-button round block @click="resetRecommend">重置</van-button>
                <van-button round block type="primary" :loading="recommendLoading" loading-text="推荐中..." @click="handleRecommend">
                    提交
                </van-button>
            </div>
        </div>

        <div v-if="hasRecommended" class="result-card">
            <div class="result-header">
                <div>
                    <div class="result-title">推荐结果</div>
                    <div class="result-subtitle">共 {{ recommendList.length }} 本</div>
                </div>
            </div>

            <van-empty v-if="hasRecommended && recommendList.length === 0" description="暂未匹配到合适图书，试试换个方向" />

            <div v-else class="recommend-list">
                <div
                    v-for="book in recommendList"
                    :key="book.id"
                    class="recommend-card"
                    @click="onBookClick(book)"
                >
                    <img :src="book.cover || defaultCover" :alt="book.bookName" class="recommend-cover" />
                    <div class="recommend-content">
                        <div class="recommend-book-name">{{ book.bookName }}</div>
                        <div class="recommend-book-meta">{{ book.author || '佚名' }} · {{ book.publisher || '未知出版社' }}</div>
                        <div class="recommend-reason">{{ book.recommendReason }}</div>
                        <div class="recommend-footer">
                            <div class="recommend-book-tags">
                                <van-tag
                                    v-for="tag in book.tags"
                                    :key="tag"
                                    plain
                                    type="primary"
                                    size="mini"
                                >
                                    {{ tag }}
                                </van-tag>
                            </div>
                            <van-tag :type="book.availableStock > 0 ? 'success' : 'danger'">
                                {{ book.availableStock > 0 ? '可借阅' : '已借完' }}
                            </van-tag>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getBookRecommendList } from '@/api/book.js'
import { useBookStore } from '@/store/bookStore.js'

const router = useRouter()
const bookStore = useBookStore()

const defaultCover = 'https://img.yzcdn.cn/vant/cat.jpeg'
const quickTags = ['计算机', '文学', '历史', '心理', '考研', '推理']

const recommendLoading = ref(false)
const hasRecommended = ref(false)
const selectedTags = ref([])
const recommendList = ref([])

const recommendForm = reactive({
    hobby: '',
    major: '',
    goal: ''
})

const toggleQuickTag = (tag) => {
    if (selectedTags.value.includes(tag)) {
        selectedTags.value = selectedTags.value.filter((item) => item !== tag)
        return
    }

    selectedTags.value = [...selectedTags.value, tag]
}

const normalizeRecommendBook = (book) => ({
    ...book,
    tags: Array.isArray(book.tags) ? book.tags : [],
    recommendReason: book.recommendReason || '该图书与您的输入方向较为匹配，适合作为本次推荐候选。'
})

const fetchRecommendList = async () => {
    recommendLoading.value = true

    try {
        const res = await getBookRecommendList({
            ...recommendForm,
            tags: selectedTags.value
        })

        if (res.code === 0) {
            recommendList.value = Array.isArray(res.data) ? res.data.map(normalizeRecommendBook) : []
            hasRecommended.value = true
            return
        }

        recommendList.value = []
        hasRecommended.value = true
        showToast(res.message || '推荐失败，请稍后重试')
    } catch (error) {
        recommendList.value = []
        hasRecommended.value = true
        showToast('推荐失败，请稍后重试')
    } finally {
        recommendLoading.value = false
    }
}

const handleRecommend = async () => {
    hasRecommended.value = false
    await fetchRecommendList()

    if (recommendList.value.length === 0) {
        showToast('没有找到完全匹配的图书，试试换个方向')
    }
}

const resetRecommend = () => {
    recommendForm.hobby = ''
    recommendForm.major = ''
    recommendForm.goal = ''
    selectedTags.value = []
    recommendList.value = []
    hasRecommended.value = false
}

const onBookClick = (book) => {
    bookStore.setCurrentBook(book)
    router.push({
        name: '图书详情',
        params: {
            id: book.id
        }
    })
}
</script>

<style scoped lang="scss">
.recommend-page {
    min-height: 100vh;
    padding: 0 0 80px;
    background:
        radial-gradient(circle at top right, rgba(70, 143, 255, 0.16), transparent 28%),
        linear-gradient(180deg, #f4f8ff 0%, #fdfefe 100%);
}

.form-panel {
    padding-top: 16px;
}

.form-card {
    margin-top: 0;
}

.tag-card {
    margin: 16px;
    padding: 16px;
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.92);
    box-shadow: 0 10px 24px rgba(69, 102, 173, 0.08);
}

.tag-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 14px;
    font-weight: 600;
    color: #26344d;
}

.tag-tip {
    font-size: 12px;
    font-weight: 400;
    color: #8a94ab;
}

.tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.interest-tag {
    cursor: pointer;
}

.action-row {
    display: flex;
    gap: 12px;
    padding: 0 16px;
}

.result-card {
    margin: 16px;
    padding: 18px 16px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.94);
    box-shadow: 0 12px 28px rgba(69, 102, 173, 0.08);
}

.result-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 14px;
}

.result-title {
    font-size: 17px;
    font-weight: 700;
    color: #26344d;
}

.result-subtitle {
    margin-top: 4px;
    font-size: 12px;
    color: #8a94ab;
}

.recommend-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.recommend-card {
    display: flex;
    gap: 12px;
    padding: 12px;
    border-radius: 16px;
    background: linear-gradient(180deg, #f9fbff 0%, #ffffff 100%);
    border: 1px solid #e6eefc;
}

.recommend-cover {
    width: 72px;
    height: 96px;
    border-radius: 10px;
    object-fit: cover;
    background: #f3f6fd;
    flex-shrink: 0;
}

.recommend-content {
    flex: 1;
    min-width: 0;
}

.recommend-book-name {
    font-size: 16px;
    font-weight: 600;
    line-height: 1.4;
    color: #1f2940;
}

.recommend-book-meta {
    margin-top: 4px;
    font-size: 12px;
    color: #8a94ab;
}

.recommend-reason {
    margin-top: 8px;
    font-size: 13px;
    line-height: 1.6;
    color: #50617f;
}

.recommend-footer {
    margin-top: 10px;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: 10px;
}

.recommend-book-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
}
</style>
