<template>
    <div id="announcement">
        <div style="margin-top: 20px; display: flex; flex-wrap: wrap;" class="header">
            <div>
                <el-button type="primary" :icon="Plus" @click="handleAdd" plain>新增公告</el-button>
            </div>
            <div>
                <el-input v-model="searchContent.title" style="width: 240px; margin-left: 10px;"
                          placeholder="请输入标题" clearable @change="handleSearch" />
            </div>
            <div>
                <el-form-item style="width: 240px; margin-left: 10px;">
                    <el-select v-model="searchContent.type" placeholder="公告类型" clearable @change="handleSearch">
                        <el-option label="普通" :value="0" />
                        <el-option label="系统通知" :value="1" />
                        <el-option label="维护公告" :value="2" />
                    </el-select>
                </el-form-item>
            </div>
            <div>
                <el-button style="margin-left: 10px;" :icon="Refresh" type="warning" plain @click="reset">重置</el-button>
            </div>
        </div>

        <main>
            <el-table :data="tableData" style="width: 100%; margin-top: 10px;"
                      :cell-style="rowClass" :header-cell-style="headClass" v-loading="loading">
                <el-table-column prop="title" label="公告标题" align="center" show-overflow-tooltip />
                <el-table-column prop="type" label="类型" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.type === 1 ? 'warning' : row.type === 2 ? 'danger' : 'info'">
                            {{ row.type === 1 ? '系统通知' : row.type === 2 ? '维护公告' : '普通' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'info'">
                            {{ row.status === 1 ? '已发布' : '下线' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="isTop" label="置顶" width="80" align="center">
                    <template #default="{ row }">
                        <el-tag v-if="row.isTop === 1" effect="dark" type="success">是</el-tag>
                        <span v-else>否</span>
                    </template>
                </el-table-column>
                <el-table-column prop="viewCount" label="阅读量" width="100" align="center" />
                <el-table-column prop="createTime" label="发布时间" width="180" align="center" />
                <el-table-column label="操作" width="180" align="center" fixed="right">
                    <template #default="scope">
                        <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </main>

        <div style="margin-top: 20px; margin-left: 10px;">
            <el-pagination
                v-model:current-page="page.currentPage"
                v-model:page-size="page.pageSize"
                :page-sizes="[10, 20, 50]"
                background
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="600px" center destroy-on-close>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" placeholder="请输入公告标题" />
                </el-form-item>

                <el-form-item label="类型" prop="type">
                    <el-radio-group v-model="form.type">
                        <el-radio :value="0">普通</el-radio>
                        <el-radio :value="1">系统通知</el-radio>
                        <el-radio :value="2">维护公告</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="设置">
                    <el-checkbox v-model="form.isTop" :true-value="1" :false-value="0">置顶公告</el-checkbox>
                    <el-checkbox v-model="form.status" :true-value="1" :false-value="0" style="margin-left: 20px;">立即发布</el-checkbox>
                </el-form-item>

                <el-form-item label="内容" prop="content">
                    <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="danger" plain @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确认</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    addAnnouncement,
    deleteAnnouncement,
    updateAnnouncement,
    listAnnouncementVOByPage
} from "@/api/announcement.js"

// 表格样式
const rowClass = () => ({'text-align': 'center'})
const headClass = () => ({'text-align': 'center', background: '#f8f8f9'})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()

const page = reactive({ currentPage: 1, pageSize: 10 })
const searchContent = reactive({
    title: '',
    type: null
})

const form = reactive({
    id: null,
    title: '',
    content: '',
    type: 0,
    status: 1,
    isTop: 0
})

const rules = {
    title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
    content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
    type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 数据初始化
const initData = async () => {
    loading.value = true
    try {
        const params = {
            title: searchContent.title || null,
            type: searchContent.type !== null ? searchContent.type : null,
            currentPage: page.currentPage,
            pageSize: page.pageSize
        }
        const res = await listAnnouncementVOByPage(params)
        if (res.code === 0) {
            tableData.value = res.data.records || []
            total.value = parseInt(res.data.total) || 0
        }
    } finally {
        loading.value = false
    }
}

// 监听分页
watch(() => [page.currentPage, page.pageSize], () => {
    initData()
}, { immediate: true })

// 搜索
const handleSearch = () => {
    page.currentPage = 1
    initData()
}

// 重置
const reset = () => {
    searchContent.title = ''
    searchContent.type = null
    page.currentPage = 1
    initData()
}

const handleAdd = () => {
    Object.assign(form, { id: null, title: '', content: '', type: 0, status: 1, isTop: 0 })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    Object.assign(form, JSON.parse(JSON.stringify(row)))
    dialogVisible.value = true
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            const api = form.id ? updateAnnouncement : addAnnouncement
            const res = await api(form)
            if (res.code === 0) {
                ElMessage.success('操作成功')
                dialogVisible.value = false
                initData()
            }
        }
    })
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该公告吗？', '确认删除?', {
        type: 'warning',
        center: true
    }).then(async () => {
        const res = await deleteAnnouncement({ id: row.id })
        if (res.code === 0) {
            ElMessage.success('操作成功')
            if (tableData.value.length === 1 && page.currentPage > 1) {
                page.currentPage--
            } else {
                initData()
            }
        }
    }).catch(() => {})
}

const handleSizeChange = (val) => {
    page.pageSize = val
    page.currentPage = 1
}

const handleCurrentChange = (val) => {
    page.currentPage = val
}
</script>

<style scoped>
.header > div {
    margin-left: 10px;
}
</style>