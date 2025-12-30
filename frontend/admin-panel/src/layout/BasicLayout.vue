<template>
    <div class="layout">
        <div>
            <Aside class="menu" :isCollapse='isCollapse'></Aside>
        </div>
        <div>
            <Content class="content" :class="{ isActive: isCollapse }" @changeCollapse='changeCollapse'
                     :isCollapse='isCollapse'>
            </Content>
        </div>
    </div>


</template>

<script setup>
import Aside from '@/layout/Aside.vue';
import Content from '@/layout/Content.vue';
import { ref } from 'vue';

let isCollapse = ref(false)
const changeCollapse = (() => {
    isCollapse.value = !isCollapse.value

})
</script>

<style scoped lang='less'>
.layout {
  .menu {
    position: fixed;
    top: 0;
    bottom: 0;
  }

  .content {
    margin-left: 200px;
    transition: all 0.3s;
  }

  .isActive {
    margin-left: 64px;
  }
}
</style>