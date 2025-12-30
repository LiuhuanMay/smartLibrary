<template>
    <div id="borrow">
        <!--表格-->
        <main>
            <el-table
                :data="tableData"
                style="width: 100%; margin-top: 10px"
                :cell-style="rowClass"
                :header-cell-style="headClass"
                tooltip-effect="dark"
            >
                <el-table-column prop="userName" label="用户名称" width="120" />
                <el-table-column prop="bookName" label="图书名称" min-width="160" show-overflow-tooltip />
                <el-table-column prop="borrowTime" label="借阅时间" width="160" />
                <el-table-column prop="dueTime" label="应还时间" width="160" />
                <!-- 实际归还时间空值显示暂无 -->
                <el-table-column prop="returnTime" label="实际归还时间" width="160">
                    <template #default="{ row }">
                        {{ row.returnTime ? row.returnTime : '暂无' }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="借阅状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="statusTag(row.status)">{{ statusTxt(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="reviewStatus" label="审核状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="reviewTag(row.reviewStatus)">{{ reviewTxt(row.reviewStatus) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="overdueDays" label="逾期天数" width="100" />
                <!-- 操作列：审核按钮/已审核文本（带颜色） -->
                <el-table-column label="操作" fixed="right" width="120">
                    <template #default="{ row }">
                        <el-button
                            v-if="row.reviewStatus === 0"
                            size="small"
                            type="primary"
                            @click="openReview(row)"
                        >审核</el-button>
                        <span v-else style="color: #67c23a; font-size: 12px; text-align: center; display: inline-block; width: 100%;">已审核</span>
                    </template>
                </el-table-column>
            </el-table>
        </main>

        <!--分页-->
        <div class="pagination">
            <el-pagination
                background
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                :page-sizes="[8, 10, 50]"
                :current-page="page.currentPage"
                :page-size="page.pageSize"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 优化后的审核弹窗（贴合活动模板风格） -->
        <el-dialog v-model="reviewVisible"  width="500px" center>
            <el-form :model="reviewForm" label-width="auto" style="width: 100%;">
                <el-form-item label="用户名称">
                    <span style="font-weight: bold; color: #606266;">{{ reviewRow.userName || '无' }}</span>
                </el-form-item>
                <el-form-item label="图书名称">
                    <span style="font-weight: bold; color: #606266;">{{ reviewRow.bookName || '无' }}</span>
                </el-form-item>
                <el-form-item label="审核结果">
                    <el-select
                        v-model="reviewForm.reviewStatus"
                        placeholder="请选择审核结果"
                        style="width: 100%;"
                    >
                        <el-option label="同意" value="1" />
                        <el-option label="拒绝" value="2" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="danger" plain @click="reviewVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmReview">确认</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
import { listBookBorrowVOByPage, ReviewBookBorrow } from '@/api/bookBorrow.js'

/* -------- 表格样式 -------- */
const rowClass = () => ({ textAlign: 'center' })
const headClass = () => ({ textAlign: 'center', background: '#f8f8f9' })

/* -------- 表格数据 & 分页 -------- */
const tableData = ref([])
const total = ref(0)
const page = reactive({ currentPage: 1, pageSize: 8 })

const handleSizeChange = (val) => {
    page.pageSize = val
}
const handleCurrentChange = (val) => {
    page.currentPage = val
}

/* -------- 枚举文案 & 颜色（修复 ElTag 报错） -------- */
const statusTxt = (s) =>
    ({ 0: '未审核', 1: '借阅中', 2: '已归还', 3: '逾期' }[s] ?? '未知')
const statusTag = (s) =>
    ({ 0: 'info', 1: 'info', 2: 'success', 3: 'danger' }[s] ?? 'info')

const reviewTxt = (s) =>
    ({ 0: '未审核', 1: '同意', 2: '拒绝' }[s] ?? '已审核')
const reviewTag = (s) =>
    ({ 0: 'info', 1: 'success', 2: 'danger' }[s] ?? 'info')

/* -------- 审核弹窗（优化后） -------- */
const reviewVisible = ref(false)
const reviewRow = reactive({})
const reviewForm = reactive({
    bookBorrowId: null,
    reviewStatus: '1' // 默认选中同意
})

const openReview = (row) => {
    Object.assign(reviewRow, row)
    reviewForm.bookBorrowId = row.id
    reviewForm.reviewStatus = '1'
    reviewVisible.value = true
}

const confirmReview = async () => {
    const res = await ReviewBookBorrow(reviewForm)
    if (res.code === 0) {
        ElMessage.success('审核完成')
        reviewVisible.value = false
        loadData()
    } else {
        ElMessage.error(res.message || '审核失败')
    }
}

/* -------- 数据加载 -------- */
const loadData = async () => {
    const params = { currentPage: page.currentPage, pageSize: page.pageSize }
    const res = await listBookBorrowVOByPage(params)
    tableData.value = res.data.records
    total.value = +res.data.total
}

/* 自动请求 */
watchEffect(loadData)
</script>

<style scoped>
.header {
    margin: 20px 0 10px 10px;
    font-size: 16px;
}
.pagination {
    margin: 20px 0 0 10px;
}
/* 复用活动模板的底部按钮样式 */
.dialog-footer {
    text-align: right;
}
</style>