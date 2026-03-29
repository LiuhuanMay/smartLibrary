import { http } from "@/utils/http.js";

/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const chat = (data) => {
    return http.post("/ai/chat", data);
}

/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const clearBorrowHistory=(data)=>{
    return http.post("/ai/clearBorrowHistory",data)
}

/**
 *
 * @param data
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const clearPlatformHistory=(data)=>{
    return http.post("/ai/clearPlatformHistory",data)
}

const mockRecommendBooks = [
    {
        id: 9001,
        bookName: 'Vue.js 前端开发实战',
        author: '王明',
        publisher: '电子工业出版社',
        cover: '',
        bookIntroduction: '适合计算机相关专业与前端开发初学者，帮助建立 Vue 项目开发的完整认知。',
        language: '中文',
        price: 69,
        availableStock: 4,
        totalStock: 8,
        borrowedCount: 4,
        totalBorrowedCount: 36,
        recommendReason: '适合计算机专业与前端方向入门学习。',
        tags: ['计算机', '前端', '入门']
    },
    {
        id: 9002,
        bookName: '算法图解',
        author: 'Aditya Bhargava',
        publisher: '人民邮电出版社',
        cover: '',
        bookIntroduction: '用图解方式解释常见算法，非常适合想打基础的学生和工程方向读者。',
        language: '中文',
        price: 59,
        availableStock: 2,
        totalStock: 6,
        borrowedCount: 4,
        totalBorrowedCount: 52,
        recommendReason: '如果你想系统提升编程基础，这本书很适合作为入门读物。',
        tags: ['计算机', '算法', '系统学习']
    },
    {
        id: 9003,
        bookName: '乌合之众',
        author: '古斯塔夫·勒庞',
        publisher: '中央编译出版社',
        cover: '',
        bookIntroduction: '从群体心理角度理解社会行为，适合心理学、社会学与通识阅读。',
        language: '中文',
        price: 39,
        availableStock: 5,
        totalStock: 7,
        borrowedCount: 2,
        totalBorrowedCount: 41,
        recommendReason: '对心理与社会行为感兴趣时，这本书很容易打开视野。',
        tags: ['心理', '社会', '思考']
    },
    {
        id: 9004,
        bookName: '人类群星闪耀时',
        author: '斯蒂芬·茨威格',
        publisher: '上海译文出版社',
        cover: '',
        bookIntroduction: '通过历史瞬间理解人物与时代，适合历史兴趣阅读与写作积累。',
        language: '中文',
        price: 45,
        availableStock: 3,
        totalStock: 5,
        borrowedCount: 2,
        totalBorrowedCount: 28,
        recommendReason: '如果你喜欢历史与人物故事，这本书的阅读体验会很强。',
        tags: ['历史', '人物', '文学']
    },
    {
        id: 9005,
        bookName: '嫌疑人X的献身',
        author: '东野圭吾',
        publisher: '南海出版公司',
        cover: '',
        bookIntroduction: '节奏紧凑的推理小说，适合想放松阅读又喜欢逻辑感的读者。',
        language: '中文',
        price: 35,
        availableStock: 0,
        totalStock: 4,
        borrowedCount: 4,
        totalBorrowedCount: 60,
        recommendReason: '如果你偏好推理题材，这本书通常会是高接受度选择。',
        tags: ['推理', '文学', '休闲阅读']
    },
    {
        id: 9006,
        bookName: '教育心理学',
        author: '陈琦',
        publisher: '高等教育出版社',
        cover: '',
        bookIntroduction: '适合教育学专业、教师资格备考与教学场景认知提升。',
        language: '中文',
        price: 56,
        availableStock: 6,
        totalStock: 9,
        borrowedCount: 3,
        totalBorrowedCount: 24,
        recommendReason: '教育学相关专业和考试准备都比较适合从这本书开始。',
        tags: ['教育学', '心理', '考研']
    }
]

const normalizeText = (value) => (value || '').trim().toLowerCase()

export const getBookRecommendationsByModel = async (data = {}) => {
    const keywords = [data.hobby, data.major, data.goal, ...(data.tags || [])]
        .map(normalizeText)
        .filter(Boolean)

    const matched = keywords.length === 0
        ? mockRecommendBooks.slice(0, 3)
        : mockRecommendBooks.filter((book) => {
            const target = [
                book.bookName,
                book.author,
                book.publisher,
                book.bookIntroduction,
                book.recommendReason,
                ...(book.tags || [])
            ]
                .join(' ')
                .toLowerCase()

            return keywords.some((keyword) => target.includes(keyword))
        }).slice(0, 4)

    // 模拟大模型处理耗时，后续你可以直接替换成真实大模型接口调用。
    await new Promise((resolve) => setTimeout(resolve, 5000))

    return Promise.resolve({
        code: 0,
        data: matched
    })
}
