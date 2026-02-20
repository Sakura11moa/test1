<template>
  <section class="echarts-dashboard a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-data-analysis"></i>
          数据分析
        </h2>
        <p class="page-subtitle">运营关键指标一览，支持按时间范围切换</p>
      </div>

      <div class="action-area">
        <el-radio-group v-model="range" size="small" @change="updateAllCharts" class="range-switch">
          <el-radio-button label="7">近7天</el-radio-button>
          <el-radio-button label="30">近30天</el-radio-button>
          <el-radio-button label="90">近90天</el-radio-button>
        </el-radio-group>
      </div>
    </header>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-label">新增会员</div>
        <div class="stat-value">{{ kpi.newMembers }}</div>
        <div class="stat-sub text-dim">较上期 {{ kpi.memberDelta >= 0 ? '+' : '' }}{{ kpi.memberDelta }}%</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">预约量</div>
        <div class="stat-value">{{ kpi.bookings }}</div>
        <div class="stat-sub text-dim">较上期 {{ kpi.bookingDelta >= 0 ? '+' : '' }}{{ kpi.bookingDelta }}%</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">收入(估算)</div>
        <div class="stat-value">¥{{ kpi.revenue }}</div>
        <div class="stat-sub text-dim">较上期 {{ kpi.revenueDelta >= 0 ? '+' : '' }}{{ kpi.revenueDelta }}%</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">设备使用率</div>
        <div class="stat-value">{{ kpi.equipmentRate }}%</div>
        <div class="stat-sub text-dim">较上期 {{ kpi.equipmentDelta >= 0 ? '+' : '' }}{{ kpi.equipmentDelta }}%</div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="chart-card">
        <div class="chart-header">
          <h3><i class="el-icon-s-data"></i> 会员增长趋势</h3>
        </div>
        <div class="chart-container">
          <div ref="memberChart" class="chart"></div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h3><i class="el-icon-money"></i> 收入走势</h3>
        </div>
        <div class="chart-container">
          <div ref="revenueChart" class="chart"></div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h3><i class="el-icon-tickets"></i> 预约量走势</h3>
        </div>
        <div class="chart-container">
          <div ref="bookingChart" class="chart"></div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h3><i class="el-icon-cpu"></i> 设备使用率</h3>
        </div>
        <div class="chart-container">
          <div ref="equipmentChart" class="chart"></div>
        </div>
      </div>
    </div>

    <div class="hint text-muted">
      当前为演示数据。若你希望接入真实数据，我可以把这些图表对接到你现有的统计接口或新增一个聚合统计 API。
    </div>
  </section>
</template>

<script>
export default {
  name: 'EchartsDashboard',
  data() {
    return {
      range: '30',
      charts: {
        member: null,
        revenue: null,
        booking: null,
        equipment: null,
      },
      kpi: {
        newMembers: 0,
        memberDelta: 0,
        bookings: 0,
        bookingDelta: 0,
        revenue: 0,
        revenueDelta: 0,
        equipmentRate: 0,
        equipmentDelta: 0,
      }
    }
  },
  mounted() {
    this.initCharts();
    window.addEventListener('resize', this.resizeCharts, { passive: true });
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
    this.disposeCharts();
  },
  methods: {
    updateAllCharts() {
      this.generateMockData();
      this.renderAllCharts();
    },
    initCharts() {
      this.$nextTick(() => {
        this.charts.member = this.$echarts.init(this.$refs.memberChart);
        this.charts.revenue = this.$echarts.init(this.$refs.revenueChart);
        this.charts.booking = this.$echarts.init(this.$refs.bookingChart);
        this.charts.equipment = this.$echarts.init(this.$refs.equipmentChart);

        this.updateAllCharts();
      });
    },
    disposeCharts() {
      Object.values(this.charts).forEach((c) => {
        if (c && c.dispose) c.dispose();
      });
      this.charts = { member: null, revenue: null, booking: null, equipment: null };
    },
    resizeCharts() {
      Object.values(this.charts).forEach((c) => {
        if (c && c.resize) c.resize();
      });
    },
    generateMockData() {
      const days = Number(this.range);
      const labels = [];
      const members = [];
      const revenue = [];
      const bookings = [];

      let mBase = days === 7 ? 18 : days === 30 ? 80 : 220;
      let rBase = days === 7 ? 12000 : days === 30 ? 56000 : 160000;
      let bBase = days === 7 ? 90 : days === 30 ? 420 : 1200;

      for (let i = days; i >= 1; i--) {
        labels.push(`${i}天前`);
        members.push(Math.round(mBase + Math.random() * (mBase * 0.25)));
        revenue.push(Math.round(rBase / days + Math.random() * (rBase / days * 0.3)));
        bookings.push(Math.round(bBase / days + Math.random() * (bBase / days * 0.35)));
      }

      const sum = (arr) => arr.reduce((a, b) => a + b, 0);

      this._mock = { labels: labels.reverse(), members, revenue, bookings };

      this.kpi.newMembers = sum(members);
      this.kpi.bookings = sum(bookings);
      this.kpi.revenue = sum(revenue);
      this.kpi.equipmentRate = days === 7 ? 76 : days === 30 ? 82 : 88;

      this.kpi.memberDelta = days === 7 ? 6.2 : days === 30 ? 12.4 : 18.1;
      this.kpi.bookingDelta = days === 7 ? 3.7 : days === 30 ? 9.3 : 14.6;
      this.kpi.revenueDelta = days === 7 ? 4.8 : days === 30 ? 10.1 : 16.5;
      this.kpi.equipmentDelta = days === 7 ? 1.3 : days === 30 ? 2.4 : 3.9;
    },
    renderAllCharts() {
      this.renderMemberChart();
      this.renderRevenueChart();
      this.renderBookingChart();
      this.renderEquipmentChart();
    },
    baseGrid() {
      return { left: '3%', right: '4%', bottom: '3%', containLabel: true };
    },
    baseAxis() {
      return {
        axisLine: { lineStyle: { color: '#e5e7eb' } },
        axisLabel: { color: '#4b5563' },
        splitLine: { lineStyle: { color: 'rgba(0,0,0,0.04)' } },
      };
    },
    renderMemberChart() {
      if (!this.charts.member) return;
      const { labels, members } = this._mock;
      this.charts.member.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(17,24,39,0.9)', textStyle: { color: '#fff' } },
        grid: this.baseGrid(),
        xAxis: { type: 'category', data: labels, ...this.baseAxis(), boundaryGap: false },
        yAxis: { type: 'value', ...this.baseAxis() },
        series: [{
          name: '新增会员',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { color: '#2563eb', width: 3 },
          itemStyle: { color: '#2563eb' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(37, 99, 235, 0.22)' },
                { offset: 1, color: 'rgba(37, 99, 235, 0.04)' }
              ]
            }
          },
          data: members
        }]
      });
    },
    renderRevenueChart() {
      if (!this.charts.revenue) return;
      const { labels, revenue } = this._mock;
      this.charts.revenue.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: this.baseGrid(),
        xAxis: { type: 'category', data: labels, ...this.baseAxis() },
        yAxis: { type: 'value', ...this.baseAxis() },
        series: [{
          name: '收入',
          type: 'bar',
          data: revenue,
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
      });
    },
    renderBookingChart() {
      if (!this.charts.booking) return;
      const { labels, bookings } = this._mock;
      this.charts.booking.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(17,24,39,0.9)', textStyle: { color: '#fff' } },
        grid: this.baseGrid(),
        xAxis: { type: 'category', data: labels, ...this.baseAxis() },
        yAxis: { type: 'value', ...this.baseAxis() },
        series: [{
          name: '预约量',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { color: '#0ea5e9', width: 3 },
          itemStyle: { color: '#0ea5e9' },
          data: bookings
        }]
      });
    },
    renderEquipmentChart() {
      if (!this.charts.equipment) return;
      const rate = this.kpi.equipmentRate;
      this.charts.equipment.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}%' },
        series: [{
          name: '设备使用率',
          type: 'pie',
          radius: ['55%', '75%'],
          center: ['50%', '52%'],
          label: { show: false },
          labelLine: { show: false },
          data: [
            { value: rate, name: '使用中', itemStyle: { color: '#2563eb' } },
            { value: 100 - rate, name: '空闲', itemStyle: { color: 'rgba(37, 99, 235, 0.12)' } }
          ]
        }]
      });
    }
  }
}
</script>

<style scoped>
.echarts-dashboard {
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
  gap: 16px;
  flex-wrap: wrap;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 800;
  background: var(--admin-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  color: var(--admin-primary);
}

.page-subtitle {
  margin: 0;
  color: var(--admin-text-dim);
  font-size: 14px;
}

.action-area {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex: 1;
}

.range-switch :deep(.el-radio-button__inner) {
  border-radius: 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin: 18px 0 18px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 16px;
  padding: 18px;
  box-shadow: var(--admin-shadow);
}

.stat-label {
  font-size: 13px;
  color: var(--admin-text-dim);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 26px;
  font-weight: 900;
  color: var(--admin-text-main);
  margin-bottom: 4px;
}

.stat-sub {
  font-size: 12px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 16px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 16px;
  box-shadow: var(--admin-shadow);
  overflow: hidden;
}

.chart-header {
  padding: 16px 18px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.chart-header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 800;
  color: var(--admin-text-main);
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-header i {
  color: var(--admin-primary);
}

.chart-container {
  padding: 18px;
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

.hint {
  margin-top: 16px;
  font-size: 12px;
}

@media (max-width: 1024px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
