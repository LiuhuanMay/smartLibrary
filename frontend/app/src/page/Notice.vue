<template>
  <div id="notice-page" class="full-screen-container fixed-page">
<!--    <van-nav-bar-->
<!--      title="通知公告"-->
<!--      left-arrow-->
<!--      @click-left="goBack"-->
<!--      fixed-->
<!--      placeholder-->
<!--    />-->

      <van-nav-bar
              title="通知公告"
              @click-left="goBack"
              fixed
              placeholder
      />


      <div class="notice-tabs-wrapper">
      <van-tabs v-model:active="activeTab" sticky offset-top="46px">
        <van-tab title="全部" />
        <van-tab title="普通公告" />
        <van-tab title="系统通知" />
        <van-tab title="维护公告" />
      </van-tabs>
    </div>

    <div class="notice-scroll-area">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          :immediate-check="false"
          @load="onLoad"
        >
          <template v-if="showSkeleton">
            <div class="notice-skeleton-list">
              <van-skeleton v-for="index in 5" :key="index" title :row="2" animated />
            </div>
          </template>

          <template v-else-if="displayList.length">
            <div class="notice-list">
              <div
                v-for="item in displayList"
                :key="item.id"
                class="notice-item"
                :class="{ 'notice-item--top': item.isTop }"
                @click="onNoticeClick(item)"
              >
                <div class="notice-item-main">
                  <div class="notice-title-row">
                    <van-tag v-if="item.isTop" type="danger" size="small">置顶</van-tag>
                    <van-tag :type="tagType(item.type)" size="small">{{ formatType(item.type) }}</van-tag>
                    <span class="notice-title-text">{{ item.title }}</span>
                  </div>
                  <div class="notice-meta-row">
                    <span class="meta-item"><van-icon name="clock-o" /> {{ item.createTime }}</span>
                    <span class="meta-item"><van-icon name="eye-o" /> {{ item.viewCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>

          <van-empty v-else-if="!loading" description="暂无通知公告" />
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { listAnnouncementVOByPage, addReading } from '@/api/announcement.js';
import { showDialog } from 'vant';
import 'vant/es/dialog/style';

const router = useRouter();
const sourceList = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const activeTab = ref(0);
const fetching = ref(false);

const goBack = () => {
  router.back();
};

const displayList = computed(() => {
  if (activeTab.value === 0) return sourceList.value;
  const type = activeTab.value - 1;
  return sourceList.value.filter(item => item.type === type);
});

const showSkeleton = computed(() => loading.value && !sourceList.value.length);

const onLoad = async () => {
  if (finished.value || fetching.value) return;
  fetching.value = true;
  loading.value = true;

  try {
    const res = await listAnnouncementVOByPage({
      currentPage: currentPage.value,
      pageSize: pageSize.value,
    });
    if (res.data && Array.isArray(res.data.records)) {
      sourceList.value.push(...res.data.records);
      sourceList.value.sort((a, b) => b.isTop - a.isTop);
      if (res.data.records.length < pageSize.value) {
        finished.value = true;
      } else {
        currentPage.value++;
      }
    } else {
      finished.value = true;
    }
  } catch (error) {
    finished.value = true;
  } finally {
    fetching.value = false;
    loading.value = false;
    refreshing.value = false;
  }
};

const onRefresh = () => {
  currentPage.value = 1;
  sourceList.value = [];
  finished.value = false;
  onLoad();
};

onMounted(() => onLoad());

const formatType = type => {
  const typeMap = { 0: '普通', 1: '系统', 2: '维护' };
  return typeMap[type] || '其他';
};

const tagType = type => {
  const tagMap = { 0: 'primary', 1: 'success', 2: 'warning' };
  return tagMap[type] || 'default';
};

const onNoticeClick = item => {
  showDialog({
    title: item.title,
    message: item.content,
    messageAlign: 'left',
  }).then(() => {
    addReading({ id: item.id });
  });
};
</script>

<style lang="scss" scoped>
/* 核心修复：确保页面在最顶层且背景不透明 */
//.fixed-page {
//  position: fixed; /* 使用 fixed 定位脱离文档流 */
//  top: 0;
//  left: 0;
//  right: 0;
//  bottom: 0;
//  z-index: 999; /* 确保高于全局的 Tabbar (Vant Tabbar 通常是 1) */
//  background-color: #f7f8fa; /* 必须有背景色，否则会透到底下 */
//}

.full-screen-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.notice-tabs-wrapper {
  background: #fff;
  border-bottom: 1px solid #ebedf0;
}

.notice-scroll-area {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
  /* 适配底部安全区 */
  padding-bottom: env(safe-area-inset-bottom);
}

.notice-list {
  padding: 0 12px;

  .notice-item {
    background: #fff;
    margin-bottom: 12px;
    padding: 14px;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.04);
    position: relative;

    &:active {
      background: #f2f3f5;
    }

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 15%;
      height: 70%;
      width: 3px;
      background: #1989fa;
      border-radius: 0 2px 2px 0;
    }

    .notice-title-row {
      display: flex;
      align-items: center;
      gap: 6px;

      .notice-title-text {
        flex: 1;
        font-size: 15px;
        font-weight: 600;
        color: #323233;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .notice-meta-row {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: #969799;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 3px;
      }
    }
  }

  .notice-item--top {
    &::before {
      background: #ee0a24;
    }
  }
}

:deep(.van-nav-bar) {
  z-index: 1000;
}
</style>