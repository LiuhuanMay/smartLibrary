<template>
    <el-config-provider :locale="zhCn">
        <template v-if="route.path.startsWith('/login')||route.path.startsWith('/noAuth')">
            <router-view />
        </template>
        <template v-else>
            <BasicLayout/>
        </template>
    </el-config-provider>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router";
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import BasicLayout from "./layout/BasicLayout.vue";
const route = useRoute();
</script>

<style>
*{
    margin: 0;
    padding: 0;
}
/*滚动条*/
::-webkit-scrollbar {
    width: 4px;
    height: 4px;
    background-color: rgba(0, 0, 0, .2);
}

::-webkit-scrollbar-thumb {
    border-radius: 10px;
    height: 3px;
    background-color: #339AFD;
}

</style>