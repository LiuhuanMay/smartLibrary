<template>
    <div id="assistant" class="mobile-chat">
        <van-nav-bar
                title="智能助手"
                left-arrow
                @click-left="goBack"
                fixed
                placeholder
        />

        <div class="message-container" ref="messageContainer">
            <div
                    v-for="(message, index) in messages"
                    :key="index"
                    :class="['message-item', message.type]"
            >
                <template v-if="message.type === 'customer'">
                    <van-image
                            class="avatar"
                            round
                            width="40"
                            height="40"
                            src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
                    />
                    <div class="message-content customer-message">
                        {{ message.content }}
                    </div>
                </template>

                <template v-else>
                    <div class="message-content user-message">
                        {{ message.content }}
                    </div>
                    <van-image
                            class="avatar"
                            round
                            width="40"
                            height="40"
                            :src="userAvatar"
                            fit="cover"
                    />
                </template>
            </div>
        </div>

        <div class="input-area">
            <van-field
                    v-model="userInput"
                    placeholder="请输入消息..."
                    :border="false"
                    @keyup.enter="sendMessage"
            >
                <template #button>
                    <van-button
                            size="small"
                            type="primary"
                            :loading="isSending"
                            @click="sendMessage"
                    >发送</van-button>
                </template>
            </van-field>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed, onBeforeUnmount } from "vue";
import { useRouter, onBeforeRouteLeave } from "vue-router";
import { showToast } from "vant";
import { chat, clearBorrowHistory, clearPlatformHistory } from "@/api/ai.js";
import { userLoginUserStore } from "@/store/userStore.js";

const userStore = userLoginUserStore();
const router = useRouter();

// 1. 头像逻辑
const userAvatar = computed(() => {
    return userStore.loginUser?.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg';
});

// 2. 状态变量
const messageContainer = ref(null);
const userInput = ref("");
const messages = ref([]);
const isSending = ref(false);
const currentChatId = ref("");

// --- 核心逻辑 ---

// 生成唯一会话ID
const generateUUID = () => {
    return 'session-' + Math.random().toString(36).substr(2, 9) + Date.now().toString(36);
};

// 清空后端历史记录的统一方法
const handleClearHistory = async () => {
    if (!currentChatId.value) return;
    try {
        // 同时请求两个清空接口
        await Promise.all([
            clearBorrowHistory({ chatId: currentChatId.value }),
            clearPlatformHistory({ chatId: currentChatId.value })
        ]);
    } catch (error) {
        console.error("清空对话记录失败", error);
    }
};

// 离开页面拦截：路由切换
onBeforeRouteLeave(() => {
    handleClearHistory();
    return true;
});

// 离开页面拦截：组件销毁（保险起见）
onBeforeUnmount(() => {
    handleClearHistory();
});

// 返回
const goBack = () => {
    router.back();
};

// 发送消息
const sendMessage = async () => {
    const text = userInput.value.trim();
    if (!text || isSending.value) return;

    messages.value.push({ type: "user", content: text });
    userInput.value = "";
    isSending.value = true;

    messages.value.push({ type: "customer", content: "正在思考中..." });
    await scrollToBottom();

    try {
        const res = await chat({
            message: text,
            chatId: currentChatId.value
        });

        messages.value.pop(); // 移除“思考中”

        const result = res.data || res;
        if (result.chatId) {
            currentChatId.value = result.chatId;
        }

        messages.value.push({
            type: "customer",
            content: result.content || result.message || (typeof result === 'string' ? result : "抱歉，暂未获取到回复"),
        });
    } catch (error) {
        console.error("对话请求失败:", error);
        showToast("服务连接失败");
        messages.value.pop();
    } finally {
        isSending.value = false;
        await scrollToBottom();
    }
};

const scrollToBottom = async () => {
    await nextTick();
    if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
};

onMounted(() => {
    currentChatId.value = generateUUID();
    messages.value.push({
        type: "customer",
        content: "你好！我是智能助手，有什么可以帮你的吗？",
    });
});
</script>

<style lang="scss" scoped>
.mobile-chat {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f7f8fa;

  .message-container {
    flex: 1;
    padding: 16px;
    padding-bottom: 80px;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
  }

  .message-item {
    display: flex;
    gap: 12px;
    align-items: flex-start;
    margin-bottom: 20px;
    &.user { flex-direction: row-reverse; }
  }

  .message-content {
    max-width: 70%;
    padding: 10px 14px;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-word;
    border-radius: 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }

  .customer-message {
    color: #333;
    background-color: #fff;
    border-top-left-radius: 2px;
  }

  .user-message {
    color: #fff;
    background-color: #1989fa;
    border-top-right-radius: 2px;
  }

  .input-area {
    position: fixed;
    right: 0; bottom: 0; left: 0;
    padding: 10px 16px;
    padding-bottom: calc(10px + env(safe-area-inset-bottom));
    background-color: #fff;
    border-top: 1px solid #ebedf0;
    z-index: 100;
  }
}

:deep(.van-nav-bar__placeholder) { height: 46px !important; }
:deep(.van-field) {
  background-color: #f7f8fa;
  border-radius: 20px;
  padding: 4px 12px;
}
</style>