<template>
    <div id="system" class="system-monitor">
        <el-row :gutter="20">
            <el-col :span="8">
                <el-card shadow="hover" class="monitor-card">
                    <template #header><div class="card-header"><span>CPU 使用率</span></div></template>
                    <div class="card-content">
                        <el-progress type="dashboard" :percentage="monitorData.cpuUsage" :color="colors" />
                        <div class="status-label">核心负载</div>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="8">
                <el-card shadow="hover" class="monitor-card">
                    <template #header><div class="card-header"><span>内存占用</span></div></template>
                    <div class="card-content">
                        <el-progress type="dashboard" :percentage="monitorData.memUsage" />
                        <div class="status-label">{{ monitorData.memUsed }}G / {{ monitorData.memTotal }}G</div>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="8">
                <el-card shadow="hover" class="monitor-card">
                    <template #header><div class="card-header"><span>运行环境</span></div></template>
                    <div class="env-info">
                        <p><strong>操作系统:</strong> {{ monitorData.os }}</p>
                        <p><strong>Java 版本:</strong> <el-tag size="small">{{ monitorData.javaVersion }}</el-tag></p>
                        <p><strong>在线时间:</strong> <span class="uptime">{{ monitorData.upTime }}</span></p>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="hover" class="chart-card">
            <template #header>
                <div class="card-header">
                    <span>系统性能实时趋势</span>
                    <el-badge is-dot type="success"> 实时刷新中 </el-badge>
                </div>
            </template>
            <div ref="chartRef" style="height: 350px;"></div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import { getSystemStatus } from "@/api/system.js";

const monitorData = ref({
    cpuUsage: 0,
    memUsage: 0,
    memTotal: 0,
    memUsed: 0,
    os: 'Loading...',
    javaVersion: '...',
    upTime: '...'
});

const chartRef = ref(null);
let myChart = null;
let timer = null;

// 进度条颜色策略
const colors = [
    { color: '#67c23a', percentage: 40 },
    { color: '#e6a23c', percentage: 70 },
    { color: '#f56c6c', percentage: 100 },
];

// 初始化图表
const initChart = () => {
    if (!chartRef.value) return;
    myChart = echarts.init(chartRef.value);
    const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: [] },
        yAxis: { type: 'value', max: 100 },
        series: [{
            name: 'CPU使用率',
            type: 'line',
            smooth: true,
            showSymbol: false,
            areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
                    { offset: 1, color: 'rgba(64, 158, 255, 0)' }
                ])
            },
            data: []
        }]
    };
    myChart.setOption(option);
};

// 调用接口更新数据
const refreshStatus = async () => {
    try {
        const res = await getSystemStatus();
        if (res.code === 0) {
            monitorData.value = res.data;
            updateChart(res.data.cpuUsage);
        }
    } catch (error) {
        console.error("监控接口调用异常:", error);
    }
};

const updateChart = (val) => {
    if (!myChart) return;
    const now = new Date().toLocaleTimeString('zh-CN', { hour12: false });
    const option = myChart.getOption();

    option.xAxis[0].data.push(now);
    option.series[0].data.push(val);

    if (option.xAxis[0].data.length > 20) {
        option.xAxis[0].data.shift();
        option.series[0].data.shift();
    }
    myChart.setOption(option);
};

onMounted(() => {
    initChart();
    refreshStatus();
    // 每 3 秒拉取一次后端接口
    timer = setInterval(refreshStatus, 3000);
    window.addEventListener('resize', () => myChart?.resize());
});

onUnmounted(() => {
    if (timer) clearInterval(timer);
    myChart?.dispose();
});
</script>

<style scoped>
.system-monitor {
    padding: 20px;
    background-color: #f8f9fa;
    min-height: 100vh;
}

.monitor-card {
    height: 300px;
    border-radius: 8px;
    text-align: center;
}

.card-header {
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-content {
    padding-top: 10px;
}

.status-label {
    margin-top: 15px;
    color: #909399;
    font-size: 14px;
}

.env-info {
    text-align: left;
    padding: 10px;
    line-height: 2.5;
}

.env-info p {
    margin: 0;
    font-size: 14px;
    color: #606266;
}

.uptime {
    color: #67c23a;
    font-weight: bold;
}

.chart-card {
    margin-top: 20px;
    border-radius: 8px;
}
</style>