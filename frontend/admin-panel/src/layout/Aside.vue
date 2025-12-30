<template>
    <div>
        <el-menu :default-active="$route.path" class="el-menu-vertical-demo" :collapse="isCollapse"
                 :unique-opened="false" :router="true">
            <el-menu-item index="/visualization">
                <el-icon><HomeFilled /></el-icon>
                <span>首页</span>
            </el-menu-item>

            <el-menu-item index="/user">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
            </el-menu-item>

            <el-menu-item index="/announcement">
                <el-icon><Notification /></el-icon>
                <span>公告管理</span>
            </el-menu-item>

            <el-menu-item index="/book">
                <el-icon><Notebook /></el-icon>
                <span>图书管理</span>
            </el-menu-item>

            <el-menu-item index="/borrow">
                <el-icon><Collection /></el-icon>
                <span>借阅管理</span>
            </el-menu-item>

            <el-menu-item index="/system">
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<script setup>
// 记得在你的项目中按需引入这些图标
import {
    HomeFilled,
    User,
    Notification,
    Notebook,
    Collection,
    Setting
} from '@element-plus/icons-vue'

defineProps(['isCollapse'])
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 100vh;
}
/* 建议加上这个，防止折叠时文字闪烁 */
.el-menu {
    border-right: none;
}
</style>