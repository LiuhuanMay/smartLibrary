<template>
    <div class="dashboard-container">
        <div class="stats-row">
            <div class="stat-card" v-for="item in summaryData" :key="item.title">
                <div class="label">{{ item.title }}</div>
                <div class="value">{{ item.value }}</div>
                <div class="unit">{{ item.unit }}</div>
            </div>
        </div>

        <div class="chart-grid">
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
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';

// --- 模拟数据 (后端分析产出点) ---
const summaryData = [
    { title: '总注册用户', value: '1,284', unit: '人' },
    { title: '馆藏图书总量', value: '5,602', unit: '本' },
    { title: '当前借出中', value: '432', unit: '本' },
    { title: '逾期未归还', value: '18', unit: '本' }
];

const currentTime = ref(new Date().toLocaleString());
let timer = null;

// 图表 DOM 引用
const lineChart = ref(null);
const pieChart = ref(null);
const barChart = ref(null);
const gaugeChart = ref(null);

onMounted(() => {
    initLineChart();
    initBarChart();
    // initGaugeChart();

    timer = setInterval(() => {
        currentTime.value = new Date().toLocaleString();
    }, 1000);

    window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
    clearInterval(timer);
    window.removeEventListener('resize', handleResize);
});

// --- 各图表初始化配置 ---

const initLineChart = () => {
    const chart = echarts.init(lineChart.value);
    chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'], axisLine: { lineStyle: { color: '#ccc' } } },
        yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
        series: [{
            name: '借阅量',
            data: [120, 132, 101, 134, 90, 230, 210],
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

const initBarChart = () => {
    const chart = echarts.init(barChart.value);
    chart.setOption({
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value' },
        yAxis: { type: 'category', data: ['三体', '活着', '心理罪', '白夜行', '明朝那些事'] },
        series: [{
            type: 'bar',
            data: [182, 234, 290, 330, 410],
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
    margin-bottom: 15px;
    color: #303133;
    border-left: 4px solid #409EFF;
    padding-left: 10px;
}

.chart-content {
    flex: 1;
}
</style>