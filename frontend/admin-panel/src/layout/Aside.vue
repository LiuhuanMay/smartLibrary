<template>
    <div class="aside-menu">
        <div class="logo-box">
            <img src="https://img.icons8.com/clouds/100/book.png" class="logo-img" />
            <span v-if="!isCollapse" class="logo-text">智慧图书</span>
        </div>

        <el-menu
            :default-active="$route.path"
            :collapse="isCollapse"
            router
            unique-opened
            class="custom-menu"
        >
            <el-menu-item index="/visualization">
                <el-icon><HomeFilled /></el-icon>
                <template #title>首页看板</template>
            </el-menu-item>

            <el-menu-item index="/user">
                <el-icon><User /></el-icon>
                <template #title>用户管理</template>
            </el-menu-item>

            <el-menu-item index="/announcement">
                <el-icon><Notification /></el-icon>
                <template #title>公告管理</template>
            </el-menu-item>

            <el-menu-item index="/book">
                <el-icon><Notebook /></el-icon>
                <template #title>图书管理</template>
            </el-menu-item>

            <el-menu-item index="/borrow">
                <el-icon><Collection /></el-icon>
                <template #title>借阅管理</template>
            </el-menu-item>

            <el-menu-item index="/system">
                <el-icon><Setting /></el-icon>
                <template #title>系统管理</template>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<script setup>
import { HomeFilled, User, Notification, Notebook, Collection, Setting } from '@element-plus/icons-vue'
defineProps(['isCollapse'])
</script>

<style scoped>
.aside-menu {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.logo-box {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    overflow: hidden;
    white-space: nowrap;
}

.logo-img {
    width: 40px;
    height: 40px;
}

.logo-text {
    margin-left: 10px;
    font-size: 18px;
    font-weight: bold;
    color: #409EFF;
}

.custom-menu {
    border-right: none;
    flex: 1;
}

/* 侧边栏折叠与展开的宽度转换 */
.custom-menu:not(.el-menu--collapse) {
    width: 200px;
}
</style>