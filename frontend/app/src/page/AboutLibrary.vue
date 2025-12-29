<template>
    <div class="page">
        <van-nav-bar title="关于图书馆" left-arrow @click-left="$router.back()" />
        <div class="content" style="padding: 20px; text-align: center;">
            <van-image width="100" height="100" src="/static/library-logo.png" />
            <h3>智慧图书馆系统 v1.0.0</h3>
            <p style="color: #666; font-size: 14px; line-height: 1.6;">
                本馆致力于为广大读者提供最便捷的数字化借阅体验，
                涵盖人文、科技、艺术等各类馆藏图书50余万册。
            </p>
        </div>
    </div>
</template>