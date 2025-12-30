<template>
    <div class="content-container">
        <div class="header">
            <div class="header-left">
                <el-button
                    link
                    :icon="isCollapse ? ArrowRight : ArrowLeft"
                    @click="changeMenu"
                />
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>管理系统</el-breadcrumb-item>
                    <el-breadcrumb-item>{{ $route.name || '当前页面' }}</el-breadcrumb-item>
                </el-breadcrumb>
            </div>

            <div class="header-right">
                <el-dropdown trigger="click">
                    <div class="user-profile">
                        <span class="nickname">{{ userStore.loginUser?.nickname || '未命名' }}</span>
                        <el-avatar
                            :size="32"
                            :src="userStore.loginUser?.avatar || defaultAvatar"
                        />
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item>
                                角色: {{ userStore.loginUser?.role === 1 ? '管理员' : '普通用户' }}
                            </el-dropdown-item>
                            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>

        <div class="page-content">
            <router-view></router-view>
        </div>
    </div>
</template>

<script setup>
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { userLoginUserStore } from "@/store/userStore.js"
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const props = defineProps(['isCollapse'])
const emit = defineEmits(['changeCollapse'])
const userStore = userLoginUserStore()
const router = useRouter()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const changeMenu = () => {
    emit('changeCollapse')
}

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出系统吗？', '提示', { type: 'warning' })
        .then(() => {
            userStore.logout()
            router.push('/login')
            ElMessage.success('已安全退出')
        })
}
</script>

<style scoped>
.content-container { display: flex; flex-direction: column; height: 100%; }
.header {
    height: 60px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    border-bottom: 1px solid #f0f0f0;
}
.header-left { display: flex; align-items: center; gap: 20px; }
.user-profile { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.nickname { font-size: 14px; color: #606266; font-weight: 500; }
.page-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto; /* 内容过多时自动滚动 */
}
</style>