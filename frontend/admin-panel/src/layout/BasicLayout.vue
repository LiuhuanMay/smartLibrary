<template>
    <div class="layout-container">
        <Aside class="aside-wrapper" :isCollapse="isCollapse" />

        <div class="main-wrapper">
            <Content
                @changeCollapse="changeCollapse"
                :isCollapse="isCollapse"
            />
        </div>
    </div>
</template>

<script setup>
import Aside from '@/layout/Aside.vue';
import Content from '@/layout/Content.vue';
import { ref } from 'vue';


const isCollapse = ref(false)
const changeCollapse = () => {
    isCollapse.value = !isCollapse.value
}
</script>

<style scoped lang="less">
.layout-container {
    display: flex;
    height: 100vh;
    width: 100vw;
    overflow: hidden;

    .aside-wrapper {
        background-color: #fff;
        border-right: 1px solid #f0f0f0;
        transition: width 0.3s;
        height: 100%;
    }

    .main-wrapper {
        flex: 1; // 自动填充剩余宽度
        display: flex;
        flex-direction: column;
        overflow-x: hidden;
        overflow-y: auto;
        background-color: #f5f7fa;
    }
}
</style>