<template>
    <div class="dashboard-container">
        <!-- 加载动画和打字机遮罩层 -->
        <div v-if="isAiThinking" class="ai-loading-mask">
            <div class="ai-loading-content">
                <el-icon class="is-loading ai-icon"><Platform /></el-icon>
                <div class="typing-text">{{ currentAiText }}<span class="cursor">|</span></div>
            </div>
        </div>

        <div v-show="!isAiThinking" class="stats-row">
            <div class="stat-card" v-for="item in summaryData" :key="item.title">
                <div class="label">{{ item.title }}</div>
                <div class="value">{{ item.value }}</div>
                <div class="unit">{{ item.unit }}</div>
            </div>
        </div>

        <div v-show="!isAiThinking" class="chart-grid">
            <div class="chart-box">
                <div class="chart-title">最近7日借阅趋势</div>
                <div ref="lineChart" class="chart-content"></div>
            </div>

<!--            <div class="chart-box">-->
<!--                <div class="chart-title">图书库存状态分布</div>-->
<!--                <div ref="pieChart" class="chart-content"></div>-->
<!--            </div>-->

            <div class="chart-box">
                <div class="chart-title">热门图书借阅 TOP 5</div>
                <div ref="barChart" class="chart-content"></div>
            </div>

<!--            <div class="chart-box">-->
<!--                <div class="chart-title">借阅正常率</div>-->
<!--                <div ref="gaugeChart" class="chart-content"></div>-->
<!--            </div>-->
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';
import { getSummaryData, getBorrowTrend, getHotBooks } from '@/api/dashboard.js';
import { Platform } from '@element-plus/icons-vue';

// --- AI 模拟生成状态 ---
const isAiThinking = ref(true);
const currentAiText = ref('');
const fullAiTexts = [
    "AI 大模型正在连接数据库...",
    "正在分析最近 7 日的借阅日志...",
    "正在提取图书库存状态与借阅 TOP 5 数据...",
    "正在通过大模型结构化输出 JSON...",
    "正在渲染 ECharts 可视化面板..."
];

// --- 动态数据 (由后端提供) ---
const summaryData = ref([
    { title: '总注册用户', value: '0', unit: '人' },
    { title: '馆藏图书总量', value: '0', unit: '本' },
    { title: '当前借出中', value: '0', unit: '本' },
    { title: '逾期未归还', value: '0', unit: '本' }
]);

const currentTime = ref(new Date().toLocaleString());
let timer = null;

// 图表 DOM 引用
const lineChart = ref(null);
const pieChart = ref(null);
const barChart = ref(null);
const gaugeChart = ref(null);

onMounted(() => {
    startAiSimulation();

    timer = setInterval(() => {
        currentTime.value = new Date().toLocaleString();
    }, 1000);

    window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
    clearInterval(timer);
    window.removeEventListener('resize', handleResize);
});

// --- AI 打字机效果模拟 ---
const startAiSimulation = async () => {
    isAiThinking.value = true;
    for (let i = 0; i < fullAiTexts.length; i++) {
        const text = fullAiTexts[i];
        currentAiText.value = '';
        for (let j = 0; j < text.length; j++) {
            currentAiText.value += text[j];
            await new Promise(resolve => setTimeout(resolve, 50)); // 每个字的打印速度
        }
        await new Promise(resolve => setTimeout(resolve, 600)); // 每句话停留时间
    }
    
    // 模拟结束后，隐藏遮罩层
    isAiThinking.value = false;

    // 必须等待 Vue 完成 DOM 更新，确保图表容器(v-show="!isAiThinking")的尺寸和显隐状态已经生效
    await nextTick();

    // 加载真实数据并渲染图表
    await loadRealData();
};

// --- 数据获取与图表初始化 ---
const loadRealData = async () => {
    await fetchSummaryData();
    await fetchTrendData();
    await fetchHotBooksData();
};

const fetchSummaryData = async () => {
    try {
        const res = await getSummaryData();
        if (res.code === 0) {
            summaryData.value[0].value = res.data.totalUsers;
            summaryData.value[1].value = res.data.totalBooks;
            summaryData.value[2].value = res.data.borrowingCount;
            summaryData.value[3].value = res.data.overdueCount;
        }
    } catch (error) {
        console.error('获取概览数据失败', error);
    }
};

const fetchTrendData = async () => {
    try {
        const res = await getBorrowTrend();
        if (res.code === 0) {
            initLineChart(res.data.dates, res.data.counts);
        }
    } catch (error) {
        console.error('获取趋势数据失败', error);
    }
};

const fetchHotBooksData = async () => {
    try {
        const res = await getHotBooks();
        if (res.code === 0) {
            initBarChart(res.data.bookNames, res.data.borrowCounts);
        }
    } catch (error) {
        console.error('获取热门图书数据失败', error);
    }
};

const initLineChart = (xData, yData) => {
    const chart = echarts.init(lineChart.value);
    chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: xData, axisLine: { lineStyle: { color: '#ccc' } } },
        yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } }, minInterval: 1 },
        series: [{
            name: '借阅量',
            data: yData,
            type: 'line',
            smooth: true,
            areaStyle: { opacity: 0.3 },
            itemStyle: { color: '#409EFF' }
        }]
    });
};

const initPieChart = () => {
    const chart = echarts.init(pieChart.value);
    chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: '0', left: 'center' },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
            data: [
                { value: 1048, name: '可借阅' },
                { value: 735, name: '已借出' },
                { value: 580, name: '维修/损毁' },
                { value: 484, name: '逾期留置' }
            ]
        }]
    });
};

const initBarChart = (yAxisData, seriesData) => {
    const chart = echarts.init(barChart.value);
    chart.setOption({
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value', minInterval: 1 },
        yAxis: { type: 'category', data: yAxisData, axisLabel: { interval: 0 } },
        series: [{
            type: 'bar',
            data: seriesData,
            itemStyle: {
                color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                    { offset: 0, color: '#83bff6' },
                    { offset: 1, color: '#188df0' }
                ])
            }
        }]
    });
};

const initGaugeChart = () => {
    const chart = echarts.init(gaugeChart.value);
    chart.setOption({
        series: [{
            type: 'gauge',
            progress: { show: true, width: 8 },
            axisLine: { lineStyle: { width: 8 } },
            axisTick: { show: false },
            splitLine: { length: 12, lineStyle: { width: 2, color: '#999' } },
            anchor: { show: true, size: 14, itemStyle: { borderWidth: 2 } },
            title: { show: false },
            detail: { valueAnimation: true, fontSize: 30, offsetCenter: [0, '70%'], formatter: '{value}%' },
            data: [{ value: 96.4 }]
        }]
    });
};

const handleResize = () => {
    [lineChart, pieChart, barChart, gaugeChart].forEach(ref => {
        echarts.getInstanceByDom(ref.value)?.resize();
    });
};
</script>

<style scoped>
.dashboard-container {
    position: relative; /* 为遮罩层提供定位参考 */
    min-height: 80vh;
    background-color: #f0f2f5;
    padding: 20px;
    font-family: 'PingFang SC', sans-serif;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    border-bottom: 2px solid #409EFF;
    padding-bottom: 10px;
}

.header h1 {
    margin: 0;
    font-size: 24px;
    color: #303133;
}

.stats-row {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
}

.stat-card {
    flex: 1;
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    text-align: center;
}

.stat-card .label { color: #909399; font-size: 14px; }
.stat-card .value { font-size: 28px; font-weight: bold; color: #409EFF; margin: 5px 0; }
.stat-card .unit { color: #606266; font-size: 12px; }

.chart-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
}

.chart-box {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    height: 350px;
    display: flex;
    flex-direction: column;
}

.chart-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 15px;
    padding-left: 10px;
    border-left: 4px solid #409EFF;
}

.chart-content {
    flex: 1;
    width: 100%;
}

/* AI 加载遮罩层样式 */
.ai-loading-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.95);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
    border-radius: 8px;
}

.ai-loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
}

.ai-icon {
    font-size: 48px;
    color: #409EFF;
}

.typing-text {
    font-size: 18px;
    color: #303133;
    font-family: 'Courier New', Courier, monospace;
    font-weight: bold;
    min-height: 24px;
}

.cursor {
    display: inline-block;
    width: 2px;
    animation: blink 1s step-end infinite;
    color: #409EFF;
    margin-left: 2px;
}

@keyframes blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0; }
}
</style>