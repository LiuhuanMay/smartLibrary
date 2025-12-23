<template>
  <div id="notice-page">
    <van-sticky>
      <div class="notice-sticky-wrapper">
        <div class="notice-header">
          <div class="notice-header-main">
            <div class="notice-title-row">
              <span class="notice-title-main">通知公告</span>
              <span class="notice-badge">Smart Library</span>
            </div>
            <p class="notice-subtitle">图书馆的重要通知和系统消息会集中展示在这里</p>
          </div>
          <div class="notice-header-extra">
            <span class="notice-tip">下拉刷新，实时获取最新公告</span>
            <div class="notice-summary" v-if="totalCount">
              <van-icon name="volume-o" class="summary-icon" />
              <span class="summary-text">已为你加载 {{ totalCount }} 条公告</span>
            </div>
          </div>
        </div>
        <div class="notice-tabs">
          <van-tabs v-model:active="activeTab">
            <van-tab>
              <template #title>
                <span class="tab-icon-text">
                  <van-icon name="apps-o" />
                  <span>全部</span>
                </span>
              </template>
            </van-tab>
            <van-tab>
              <template #title>
                <span class="tab-icon-text">
                  <van-icon name="volume-o" />
                  <span>普通公告</span>
                </span>
              </template>
            </van-tab>
            <van-tab>
              <template #title>
                <span class="tab-icon-text">
                  <van-icon name="bell" />
                  <span>系统通知</span>
                </span>
              </template>
            </van-tab>
            <van-tab>
              <template #title>
                <span class="tab-icon-text">
                  <van-icon name="setting-o" />
                  <span>维护公告</span>
                </span>
              </template>
            </van-tab>
          </van-tabs>
        </div>
      </div>
    </van-sticky>

    <div class="notice-content">
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
              <van-skeleton
                v-for="index in 4"
                :key="index"
                title
                :row="2"
                animated
              />
            </div>
          </template>
          <template v-else-if="displayList.length">
            <van-cell-group inset class="notice-list">
              <van-cell
                v-for="item in displayList"
                :key="item.id"
                class="notice-item"
                :class="{ 'notice-item--top': item.isTop }"
                @click="onNoticeClick(item)"
              >
                <template #title>
                  <div class="notice-title-wrapper">
                    <van-tag v-if="item.isTop" type="danger" class="top-tag">置顶</van-tag>
                    <van-tag :type="tagType(item.type)" class="category-tag">{{ formatType(item.type) }}</van-tag>
                    <span class="notice-title">{{ item.title }}</span>
                  </div>
                </template>
                <template #label>
                  <div class="notice-meta">
                    <span class="meta-time">
                      <van-icon name="clock-o" /> {{ item.createTime }}
                    </span>
                    <span class="view-count">
                      <van-icon name="eye-o" /> {{ item.viewCount }}
                    </span>
                  </div>
                </template>
              </van-cell>
            </van-cell-group>
          </template>
          <van-empty v-else-if="!loading" image="search" description="暂无通知公告" />
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { listAnnouncementVOByPage, addReading } from '@/api/announcement.js';
import { showDialog } from 'vant';
import 'vant/es/dialog/style';

const sourceList = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const activeTab = ref(0);
const fetching = ref(false);

const totalCount = computed(() => sourceList.value.length);

const displayList = computed(() => {
  if (activeTab.value === 0) {
    return sourceList.value;
  }
  const type = activeTab.value - 1;
  return sourceList.value.filter(item => item.type === type);
});

const showSkeleton = computed(() => loading.value && !sourceList.value.length);

const onLoad = async () => {
  if (finished.value) {
    loading.value = false;
    return;
  }
  if (fetching.value) {
    return;
  }
  fetching.value = true;
  loading.value = true;

  if (refreshing.value) {
    sourceList.value = [];
    refreshing.value = false;
  }

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
  }
};

const onRefresh = () => {
  currentPage.value = 1;
  sourceList.value = [];
  finished.value = false;
  onLoad();
};

onMounted(() => {
  onLoad();
});

const formatType = type => {
  const typeMap = {
    0: '普通公告',
    1: '系统通知',
    2: '维护公告',
  };
  return typeMap[type] || '未知类型';
};

const tagType = type => {
  const tagMap = {
    0: 'primary',
    1: 'success',
    2: 'warning',
  };
  return tagMap[type] || 'default';
};

const onNoticeClick = item => {
  showDialog({
    message: item.content,
  }).then(() => {
    addReading({ id: item.id });
  });
};
</script>

<style lang="scss" scoped>
#notice-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
  padding-bottom: 80px;

  .notice-sticky-wrapper {
    background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
  }

  .notice-header {
    padding: 16px 16px 4px 16px;

    .notice-header-main {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .notice-title-row {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .notice-title-main {
          font-size: 18px;
          font-weight: 600;
          color: #222a3a;
        }

        .notice-badge {
          padding: 2px 8px;
          border-radius: 999px;
          font-size: 10px;
          color: #4a90e2;
          background: rgba(74, 144, 226, 0.08);
        }
      }

      .notice-subtitle {
        font-size: 12px;
        color: #8a8ea6;
      }
    }

    .notice-header-extra {
      margin-top: 8px;
      display: flex;
      flex-direction: column;
      gap: 6px;

      .notice-tip {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 999px;
        font-size: 11px;
        color: #3e82f7;
        background: rgba(62, 130, 247, 0.08);
      }

      .notice-summary {
        display: flex;
        align-items: center;
        font-size: 11px;
        color: #6b7a99;

        .summary-icon {
          font-size: 14px;
          margin-right: 4px;
          color: #4a90e2;
        }
      }
    }
  }

  .notice-tabs {
    padding: 0 6px 4px;

    :deep(.van-tabs__wrap) {
      background: transparent;
      padding: 0 12px;
    }

    :deep(.van-tabs__nav) {
      margin: 0 2px;
      border-radius: 999px;
      background: #ffffff;
      box-shadow: 0 2px 8px rgba(74, 144, 226, 0.12);
      padding: 2px;
    }

    :deep(.van-tab) {
      flex: 1;
      border-radius: 999px;
    }

    :deep(.van-tab--active) {
      background: linear-gradient(135deg, #5a9bff 0%, #3e82f7 100%);
      color: #ffffff;
    }

    .tab-icon-text {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      font-size: 13px;

      .van-icon {
        font-size: 14px;
      }
    }
  }

  .notice-content {
    padding: 4px 2px 12px;
  }

  .notice-skeleton-list {
    padding: 8px 12px 16px;

    :deep(.van-skeleton) {
      margin-bottom: 12px;
      border-radius: 12px;
    }
  }

  .notice-list {
    padding: 4px 0 16px;

    .notice-item {
      margin: 8px 12px;
      border-radius: 12px;
      background: #ffffff;
      box-shadow: 0 6px 16px rgba(74, 144, 226, 0.08);
      position: relative;
      overflow: hidden;
      transition: transform 0.16s ease, box-shadow 0.16s ease, background 0.16s ease;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background: linear-gradient(180deg, #5a9bff 0%, #3e82f7 100%);
        opacity: 0.7;
      }

      &:active {
        transform: scale(0.985);
        background: #f7f9ff;
        box-shadow: 0 4px 12px rgba(74, 144, 226, 0.12);
      }

      .notice-title-wrapper {
        display: flex;
        align-items: center;

        .top-tag {
          margin-right: 5px;
        }

        .category-tag {
          margin-right: 5px;
        }

        .notice-title {
          font-weight: bold;
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .notice-meta {
        display: flex;
        justify-content: space-between;
        color: #969799;
        font-size: 12px;
        margin-top: 8px;

        .meta-time {
          max-width: 60%;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          display: flex;
          align-items: center;

          .van-icon {
            margin-right: 4px;
          }
        }

        .view-count {
          display: flex;
          align-items: center;
          position: relative;
          padding-left: 10px;

          &::before {
            content: '';
            position: absolute;
            left: 3px;
            top: 50%;
            transform: translateY(-50%);
            width: 3px;
            height: 3px;
            border-radius: 50%;
            background: #d8d8d8;
          }

          .van-icon {
            margin-right: 2px;
          }
        }
      }
    }

    .notice-item--top {
      box-shadow: 0 8px 18px rgba(255, 96, 96, 0.2);

      &::before {
        background: linear-gradient(180deg, #ff5c7a 0%, #ff9b6b 100%);
      }
    }
  }

  :deep(.van-empty) {
    padding-top: 40px;
  }
}
</style>
