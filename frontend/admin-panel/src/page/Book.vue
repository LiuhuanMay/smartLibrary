<template>
    <div id="book">
        <div style="margin-top: 20px; display: flex; flex-wrap: wrap;" class="header">
            <div style="display: flex; gap: 10px;">
                <el-input v-model="queryParams.bookName" placeholder="图书名称" style="width: 200px" clearable />
                <el-input v-model="queryParams.author" placeholder="作者" style="width: 200px" clearable />
                <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
                <el-button type="primary" plain :icon="Plus" @click="addVisible=true">新增图书</el-button>
            </div>
        </div>

        <main>
            <el-table :data="tableData" style="width: 100%; margin-top: 10px;" :cell-style="rowClass"
                      :header-cell-style="headClass" tooltip-effect="dark">
                <el-table-column prop="bookName" label="图书名称" show-overflow-tooltip width="180" fixed/>
                <el-table-column prop="cover" label="图书封面" width="120">
                    <template #default="scope">
                        <el-image :src="scope.row.cover" style="height: 20px; border-radius: 4px">
                            <template #error>
                                <div style="font-size: 12px; color: #999">暂无图片</div>
                            </template>
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="author" label="作者" width="150"/>
                <el-table-column prop="publisher" label="出版社" width="150" />
                <el-table-column prop="price" label="价格" width="100" />
                <el-table-column prop="totalStock" label="总库存" width="100" />
                <el-table-column prop="isbn" label="ISBN" width="150" />
                <el-table-column prop="language" label="语言" width="100" />
                <el-table-column prop="status" label="状态" width="100">

                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                            {{ scope.row.status === 1 ? '上架' : '下架' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                    <template #default="scope">
                        <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </main>

        <div style="margin-top: 20px; margin-left: 10px;">
            <el-pagination :page-sizes="[10, 20]" background layout="total, sizes, prev, pager, next, jumper"
                           :total="total" :current-page="page.currentPage" :page-size="page.pageSize"
                           @size-change="handleSizeChange" @current-change="handleCurrentChange"/>
        </div>

        <el-dialog v-model="addVisible" title="新增图书" width="600" center @closed="resetAddForm">
            <el-form :model="addInput" label-width="auto" style="width: 90%; margin: 0 auto;">
                <el-form-item label="图书名称">
                    <el-input v-model="addInput.bookName"/>
                </el-form-item>
                <el-form-item label="封面URL">
                    <el-input v-model="addInput.cover" placeholder="请输入图片链接地址"/>
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="作者">
                            <el-input v-model="addInput.author"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="出版社">
                            <el-input v-model="addInput.publisher"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="出版日期">
                            <el-date-picker v-model="addInput.publishDate" type="date" value-format="YYYY-MM-DD" style="width: 100%"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="ISBN">
                            <el-input v-model="addInput.isbn"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8">
                        <el-form-item label="价格">
                            <el-input-number v-model="addInput.price" :precision="2" :min="0" style="width: 100%"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="库存">
                            <el-input-number v-model="addInput.totalStock" :min="0" style="width: 100%"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="语言">
                            <el-input v-model="addInput.language"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="图书简介">
                    <el-input v-model="addInput.bookIntroduction" type="textarea" :rows="4"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="danger" plain @click="addVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmAdd">确认</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog v-model="editVisible" title="编辑图书" width="600" center>
            <el-form :model="editInput" label-width="auto" style="width: 90%; margin: 0 auto;">
                <el-form-item label="图书名称">
                    <el-input v-model="editInput.bookName"/>
                </el-form-item>
                <el-form-item label="封面URL">
                    <el-input v-model="editInput.cover"/>
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="作者">
                            <el-input v-model="editInput.author"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="价格">
                            <el-input-number v-model="editInput.price" :precision="2" style="width: 100%"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="状态">
                    <el-radio-group v-model="editInput.status">
                        <el-radio :label="1">上架</el-radio>
                        <el-radio :label="0">下架</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="图书简介">
                    <el-input v-model="editInput.bookIntroduction" type="textarea" :rows="4"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="danger" plain @click="editVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmEdit">确认</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, watchEffect } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { addBook, deleteBook, updateBook, listBook } from "@/api/book.js";
import { ElMessage, ElMessageBox } from "element-plus";

// 表格样式
const rowClass = () => ({ 'text-align': 'center' })
const headClass = () => ({ 'text-align': 'center', background: '#f8f8f9' })

// 数据
const tableData = ref([])
const total = ref(0)
const page = reactive({ currentPage: 1, pageSize: 10 })
const queryParams = reactive({ bookName: '', author: '' })

// 初始化/查询数据
const initData = async () => {
    const params = {
        ...page,
        ...queryParams
    }
    const response = await listBook(params)
    // 根据你的后端返回调整，通常是 response.data.records 或 response.data.list
    tableData.value = response.data.records || []
    total.value = parseInt(response.data.total) || 0
}

const handleQuery = () => {
    page.currentPage = 1
    initData()
}

// 新增图书逻辑
const addVisible = ref(false)
const addInput = reactive({
    bookName: '',
    bookIntroduction: '',
    cover: '',
    author: '',
    publisher: '',
    publishDate: '',
    isbn: '',
    totalStock: 0,
    price: 0,
    language: '中文'
})

const confirmAdd = async () => {
    const response = await addBook(addInput)
    if (response.code === 0) {
        ElMessage.success("新增图书成功")
        addVisible.value = false
        initData()
    } else {
        ElMessage.error(response.message)
    }
}

const resetAddForm = () => {
    Object.assign(addInput, {
        bookName: '', bookIntroduction: '', cover: '', author: '',
        publisher: '', publishDate: '', isbn: '', totalStock: 0,
        price: 0, language: '中文'
    })
}

// 删除逻辑
const confirmDelete = async (id) => {
    try {
        await ElMessageBox.confirm('确定要删除该图书?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        const response = await deleteBook({ id: id })
        if (response.code === 0) {
            ElMessage.success("删除成功")
            initData()
        } else {
            ElMessage.error(response.message)
        }
    } catch (e) { /* 取消删除 */ }
}

// 编辑逻辑
const editVisible = ref(false)
const editInput = reactive({
    id: '',
    bookName: '',
    cover: '',
    bookIntroduction: '',
    author: '',
    price: 0,
    status: 1
})

const handleEdit = (row) => {
    Object.assign(editInput, row)
    editVisible.value = true
}

const confirmEdit = async () => {
    const response = await updateBook(editInput)
    if (response.code === 0) {
        ElMessage.success("编辑成功")
        editVisible.value = false
        initData()
    } else {
        ElMessage.error(response.message)
    }
}

// 分页监听
const handleSizeChange = (val) => {
    page.pageSize = val
    initData()
}

const handleCurrentChange = (val) => {
    page.currentPage = val
    initData()
}

// 初次加载
watchEffect(() => {
    initData()
})
</script>

<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    padding: 0 10px;
}
.dialog-footer {
    text-align: right;
}
:deep(.el-table__header) {
    width: 100% !important;
}
</style>