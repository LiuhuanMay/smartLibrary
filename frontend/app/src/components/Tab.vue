<template>
    <div id="tap">
        <van-tabbar v-model="active" @change="handleTabChange">
            <van-tabbar-item name="home" icon="home-o">图书精选</van-tabbar-item>
            <van-tabbar-item name="search" icon="search">通知公告</van-tabbar-item>
            <van-tabbar-item name="friends" icon="friends-o">智能助手</van-tabbar-item>
            <van-tabbar-item name="setting" icon="setting-o">我的</van-tabbar-item>
        </van-tabbar>
    </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

// 获取路由和路由实例
const route = useRoute();
const router = useRouter();

// 定义tabbar与路由的映射关系（核心：方便匹配）
const tabRouteMap = [
    { tabName: "home", path: "/book" }, // 图书精选
    { tabName: "search", path: "/notice" }, // 通知公告
    { tabName: "friends", path: "/assistant" }, // 智能助手（原绝对路径）
    { tabName: "setting", path: "/my" }, // 我的
];

// 初始化active：根据当前路由匹配对应的tabName
const active = ref(
    tabRouteMap.find(item => item.path === route.path)?.tabName || "setting"
);

// 监听路由变化，更新active
watch(
    () => route.path,
    (newPath) => {
        // 根据新路由路径匹配对应的tabName
        const matchTab = tabRouteMap.find(item => item.path === newPath);
        if (matchTab) {
            active.value = matchTab.tabName;
        }
    },
    { immediate: true } // 立即执行一次（确保初始化正确）
);

// 处理tabbar点击事件：跳转到对应路由
const handleTabChange = (tabName) => {
    // 根据tabName匹配对应的路由路径
    const matchRoute = tabRouteMap.find(item => item.tabName === tabName);
    if (matchRoute) {
        router.push(matchRoute.path);
    }
};
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
</style>