<template>
  <div class="dashboard-container">
    <!-- 头部区域 -->
    <div class="dashboard-header a-card">
      <div class="header-content">
        <div class="welcome-section">
          <h1 class="welcome-title">
            <i class="el-icon-s-home"></i>
            管理控制台
          </h1>
          <p class="welcome-subtitle">今日好心情，管理更高效</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" icon="el-icon-refresh" @click="refreshData" class="refresh-btn">
            刷新数据
          </el-button>
          <div class="current-time">
            <i class="el-icon-time"></i>
            {{ currentTime }}
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card" v-for="(stat, index) in statsData" :key="index">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <i :class="stat.icon"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-change" :class="{ 'positive': stat.change > 0, 'negative': stat.change < 0 }">
              <i :class="stat.change > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              {{ Math.abs(stat.change) }}%
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-grid">
        <div class="chart-card">
          <div class="chart-header">
            <h3><i class="el-icon-s-data"></i> 会员增长趋势</h3>
            <div class="chart-controls">
              <el-select v-model="timeRange" size="mini" @change="updateChart">
                <el-option label="最近7天" value="7d"></el-option>
                <el-option label="最近30天" value="30d"></el-option>
                <el-option label="最近90天" value="90d"></el-option>
              </el-select>
            </div>
          </div>
          <div class="chart-container">
            <div id="memberChart" class="chart"></div>
          </div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3><i class="el-icon-money"></i> 收入统计</h3>
            <div class="chart-controls">
              <el-select v-model="revenueType" size="mini" @change="updateRevenueChart">
                <el-option label="月度收入" value="monthly"></el-option>
                <el-option label="季度收入" value="quarterly"></el-option>
                <el-option label="年度收入" value="yearly"></el-option>
              </el-select>
            </div>
          </div>
          <div class="chart-container">
            <div id="revenueChart" class="chart"></div>
          </div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3><i class="el-icon-cpu"></i> 设备使用率</h3>
          </div>
          <div class="chart-container">
            <div id="equipmentChart" class="chart"></div>
          </div>
        </div>

        <div class="quick-actions-card chart-card">
          <div class="chart-header">
            <h3><i class="el-icon-setting"></i> 快速操作</h3>
          </div>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/admin/add-members')">
              <div class="action-icon"><i class="el-icon-user-add"></i></div>
              <span>添加会员</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/employee-manage')">
              <div class="action-icon"><i class="el-icon-s-custom"></i></div>
              <span>员工管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/course-manage')">
              <div class="action-icon"><i class="el-icon-reading"></i></div>
              <span>课程管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/equipment-manage')">
              <div class="action-icon"><i class="el-icon-cpu"></i></div>
              <span>设备管理</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新动态 -->
    <div class="news-section">
      <div class="news-card chart-card">
        <div class="news-header">
          <h3><i class="el-icon-bell"></i> 最新动态</h3>
          <el-button type="text" size="mini">查看全部</el-button>
        </div>
        <div class="news-list">
          <div class="news-item" v-for="(news, index) in recentNews" :key="index">
            <div class="news-dot" :style="{ backgroundColor: news.color }"></div>
            <div class="news-content">
              <div class="news-title">{{ news.title }}</div>
              <div class="news-time">{{ news.time }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      currentTime: '',
      timeRange: '30d',
      revenueType: 'monthly',
      statsData: [
        {
          icon: 'el-icon-user',
          label: '总会员数',
          value: '1,234',
          change: 12.5,
          gradient: 'linear-gradient(135deg, #2563eb 0%, #0ea5e9 100%)'
        },
        {
          icon: 'el-icon-money',
          label: '月收入',
          value: '¥45,678',
          change: 8.2,
          gradient: 'linear-gradient(135deg, #16a34a 0%, #22c55e 100%)'
        },
        {
          icon: 'el-icon-document-checked',
          label: '活跃课程',
          value: '28',
          change: -2.1,
          gradient: 'linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%)'
        },
        {
          icon: 'el-icon-cpu',
          label: '设备使用率',
          value: '87%',
          change: 5.7,
          gradient: 'linear-gradient(135deg, #ef4444 0%, #f97316 100%)'
        }
      ],
      recentNews: [
        {
          title: '新会员张三办理了年卡',
          time: '2分钟前',
          color: '#22c55e'
        },
        {
          title: '瑜伽课程预约人数已满',
          time: '15分钟前',
          color: '#f59e0b'
        },
        {
          title: '新增健身教练李四',
          time: '1小时前',
          color: '#0ea5e9'
        },
        {
          title: '设备维护提醒：跑步机需要保养',
          time: '2小时前',
          color: '#ef4444'
        }
      ]
    }
  },
  mounted() {
    this.updateTime();
    this.initCharts();
    setInterval(this.updateTime, 1000);
  },
  methods: {
    updateTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    refreshData() {
      this.$message.success('数据已刷新');
      this.initCharts();
    },

    initCharts() {
      this.$nextTick(() => {
        this.initMemberChart();
        this.initRevenueChart();
        this.initEquipmentChart();
      });
    },

    initMemberChart() {
      const chartDom = document.getElementById('memberChart');
      if (!chartDom) return;

      const myChart = this.$echarts.init(chartDom);
      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(17, 24, 39, 0.9)',
          borderColor: 'rgba(17, 24, 39, 0.9)',
          textStyle: { color: '#fff' }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'],
          axisLine: { lineStyle: { color: '#e5e7eb' } },
          axisLabel: { color: '#4b5563' }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#e5e7eb' } },
          axisLabel: { color: '#4b5563' },
          splitLine: { lineStyle: { color: 'rgba(0,0,0,0.04)' } }
        },
        series: [{
          name: '会员数量',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { color: 'var(--admin-primary)', width: 3 },
          itemStyle: { color: 'var(--admin-primary)' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(37, 99, 235, 0.25)' },
                { offset: 1, color: 'rgba(37, 99, 235, 0.05)' }
              ]
            }
          },
          data: [120, 132, 101, 134, 90, 230, 210]
        }]
      };
      myChart.setOption(option);
    },

    initRevenueChart() {
      const chartDom = document.getElementById('revenueChart');
      if (!chartDom) return;

      const myChart = this.$echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月', '6月'],
          axisLine: { lineStyle: { color: '#e5e7eb' } },
          axisLabel: { color: '#4b5563' }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#e5e7eb' } },
          axisLabel: { color: '#4b5563' },
          splitLine: { lineStyle: { color: 'rgba(0,0,0,0.04)' } }
        },
        series: [{
          name: '收入',
          type: 'bar',
          data: [20000, 25000, 30000, 35000, 28000, 32000],
          itemStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: '#2563eb' },
                { offset: 1, color: '#0ea5e9' }
              ]
            },
            borderRadius: [6, 6, 0, 0]
          }
        }]
      };
      myChart.setOption(option);
    },

    initEquipmentChart() {
      const chartDom = document.getElementById('equipmentChart');
      if (!chartDom) return;

      const myChart = this.$echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c}% ({d}%)' },
        legend: {
          orient: 'vertical',
          left: 'left',
          textStyle: { color: '#4b5563' }
        },
        series: [{
          name: '设备使用率',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: false,
          label: { show: false },
          labelLine: { show: false },
          data: [
            { value: 85, name: '跑步机', itemStyle: { color: '#2563eb' } },
            { value: 72, name: '举重架', itemStyle: { color: '#0ea5e9' } },
            { value: 68, name: '椭圆机', itemStyle: { color: '#f59e0b' } },
            { value: 90, name: '动感单车', itemStyle: { color: '#22c55e' } },
            { value: 45, name: '其他设备', itemStyle: { color: '#94a3b8' } }
          ]
        }]
      };
      myChart.setOption(option);
    },

    updateChart() {
      this.initMemberChart();
    },

    updateRevenueChart() {
      this.initRevenueChart();
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 0;
  background: transparent;
}

.dashboard-header {
  padding: 24px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.welcome-title {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 800;
  background: var(--admin-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome-title i {
  color: var(--admin-primary);
  font-size: 30px;
}

.welcome-subtitle {
  margin: 0;
  color: var(--admin-text-dim);
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.refresh-btn {
  border-radius: 10px;
  font-weight: 600;
  background: var(--admin-gradient);
  border: none;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.25);
}

.current-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--admin-text-dim);
  font-size: 13px;
  background: rgba(37, 99, 235, 0.04);
  padding: 8px 14px;
  border-radius: 999px;
  border: 1px solid var(--admin-border);
}

.current-time i {
  color: var(--admin-primary);
}

.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 16px;
  padding: 20px;
  box-shadow: var(--admin-shadow);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.12);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 22px;
  flex-shrink: 0;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: var(--admin-text-main);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--admin-text-dim);
  margin-bottom: 6px;
}

.stat-change {
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.positive { color: #16a34a; }
.stat-change.negative { color: #ef4444; }

.charts-section {
  margin-bottom: 24px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 18px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 16px;
  box-shadow: var(--admin-shadow);
  overflow: hidden;
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.12);
}

.chart-header {
  padding: 18px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--admin-text-main);
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-header h3 i {
  color: var(--admin-primary);
}

.chart-container {
  padding: 20px;
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

.quick-actions {
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 16px;
  border-radius: 14px;
  background: rgba(37, 99, 235, 0.03);
  border: 1px solid rgba(37, 99, 235, 0.1);
  cursor: pointer;
  transition: all 0.25s ease;
}

.action-item:hover {
  transform: translateY(-3px);
  background: rgba(37, 99, 235, 0.06);
  border-color: rgba(37, 99, 235, 0.18);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: var(--admin-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.action-item span {
  font-size: 13px;
  font-weight: 600;
  color: var(--admin-text-main);
}

.news-header {
  padding: 18px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.news-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--admin-text-main);
  display: flex;
  align-items: center;
  gap: 8px;
}

.news-header h3 i {
  color: var(--admin-primary);
}

.news-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.news-item:last-child {
  border-bottom: none;
}

.news-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.news-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--admin-text-main);
  margin-bottom: 3px;
}

.news-time {
  font-size: 12px;
  color: var(--admin-text-dim);
}

@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
