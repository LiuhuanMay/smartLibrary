<template>
    <div class="page">
        <van-nav-bar title="帮助与反馈" left-arrow @click-left="$router.back()" />
        <van-collapse v-model="activeNames">
            <van-collapse-item title="如何借阅图书？" name="1">
                在首页选择心仪图书，点击进入详情页，点击“立即借阅”即可。
            </van-collapse-item>
            <van-collapse-item title="借阅期限是多久？" name="2">
                普通读者借阅期限为30天，可在到期前一周申请续借。
            </van-collapse-item>
        </van-collapse>
        <div style="margin: 20px 16px">
            <van-button
                block
                type="primary"
                plain
                @click="$router.push('/assistant')"
            >
                在线咨询
            </van-button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
const activeNames = ref(['1']);
</script>