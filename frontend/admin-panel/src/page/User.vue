
<template>
    <div id="user">
        <div style="margin-top: 20px; display: flex; flex-wrap: wrap;" class="header">
            <el-input v-model="searchContent.nickname" placeholder="昵称" style="width: 180px; margin-left: 10px;" clearable @change="handleSearch" />
            <el-input v-model="searchContent.phone" placeholder="手机号" style="width: 180px; margin-left: 10px;" clearable @change="handleSearch" />
            <el-select v-model="searchContent.role" placeholder="角色" clearable style="width: 130px; margin-left: 10px;" @change="handleSearch">
                <el-option label="普通用户" :value="0" />
                <el-option label="管理员" :value="1" />
            </el-select>
            <el-button :icon="Refresh" type="warning" plain style="margin-left: 10px;" @click="reset">重置</el-button>
        </div>

        <main>
            <el-table :data="tableData" style="width: 100%; margin-top: 15px;" v-loading="loading">
                <el-table-column prop="avatar" label="头像" width="70" align="center">
                    <template #default="{ row }">
                        <el-avatar :size="35" :src="row.avatar" />
                    </template>
                </el-table-column>
                <el-table-column prop="nickname" label="昵称" align="center" show-overflow-tooltip />
                <el-table-column prop="phone" label="手机号" align="center" width="120" />
                <el-table-column prop="email" label="邮箱" align="center" show-overflow-tooltip />
                <el-table-column prop="age" label="年龄" width="70" align="center" />
                <el-table-column prop="gender" label="性别" width="70" align="center">
                    <template #default="{ row }">
                        {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}
                    </template>
                </el-table-column>
                <el-table-column prop="role" label="角色" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.role === 1 ? 'danger' : 'info'">{{ row.role === 1 ? '管理员' : '用户' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="updateTime" label="最后更新" width="160" align="center" />

                <el-table-column label="操作" width="100" align="center" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </main>

        <div style="margin-top: 20px;">
            <el-pagination v-model:current-page="page.currentPage" v-model:page-size="page.pageSize" :total="total" background layout="total, prev, pager, next" @current-change="handleCurrentChange" />
        </div>

        <el-dialog v-model="dialogVisible" title="编辑用户" width="500px" destroy-on-close>
            <el-form :model="form" label-width="80px" style="padding: 0 20px;">
                <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
                <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
                <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
                <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" :max="150" /></el-form-item>

                <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                        <el-radio :value="0">未知</el-radio>
                        <el-radio :value="1">男</el-radio>
                        <el-radio :value="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="form.role" style="width: 100%;">
                        <el-option label="普通用户" :value="0" /><el-option label="管理员" :value="1" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="form.status">
                        <el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="头像地址"><el-input v-model="form.avatar" /></el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitForm">保存修改</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listUserVOByPage, updateUser } from "@/api/user.js"

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)

const page = reactive({ currentPage: 1, pageSize: 10 })
const searchContent = reactive({ nickname: '', phone: '', role: null })
const form = reactive({ id: null, phone: '', nickname: '', password: '', email: '', avatar: '', age: null, gender: 0, role: 0, status: 1 })

const initData = async () => {
    loading.value = true
    try {
        const res = await listUserVOByPage({ ...searchContent, ...page })
        if (res.code === 0) {
            tableData.value = res.data.records
            total.value = parseInt(res.data.total)
        }
    } finally { loading.value = false }
}

watch(() => [page.currentPage], () => initData(), { immediate: true })

const handleSearch = () => { page.currentPage = 1; initData() }
const reset = () => { searchContent.nickname = ''; searchContent.phone = ''; searchContent.role = null; handleSearch() }

const handleEdit = (row) => {
    Object.assign(form, JSON.parse(JSON.stringify(row)))
    form.password = '' // 密码不回显
    dialogVisible.value = true
}

const submitForm = async () => {
    const data = { ...form }
    if (!data.password) delete data.password
    const res = await updateUser(data)
    if (res.code === 0) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        initData()
    }
}

const handleCurrentChange = (val) => { page.currentPage = val }
</script>