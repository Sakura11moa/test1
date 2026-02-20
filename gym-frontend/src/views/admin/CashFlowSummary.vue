<template>
  <div class="cash-flow-summary-container">
    <!-- 页面头部 -->
    <div class="page-header-section a-card">
      <div class="header-content">
        <div class="page-title">
          <i class="el-icon-s-data"></i>
          <h1>现金收款汇总</h1>
          <span class="page-subtitle">充值和现金续卡汇总统计</span>
        </div>
      </div>

      <!-- 快捷日期选择 -->
      <div class="quick-date-btns">
        <el-button-group>
          <el-button :type="quickDate === 'today' ? 'primary' : ''" @click="selectQuickDate('today')" icon="el-icon-date">今日</el-button>
          <el-button :type="quickDate === 'week' ? 'primary' : ''" @click="selectQuickDate('week')" icon="el-icon-date">本周</el-button>
          <el-button :type="quickDate === 'month' ? 'primary' : ''" @click="selectQuickDate('month')" icon="el-icon-date">本月</el-button>
          <el-button :type="quickDate === 'custom' ? 'primary' : ''" @click="selectQuickDate('custom')" icon="el-icon-edit">自定义</el-button>
        </el-button-group>
      </div>
      
      <!-- 自定义日期选择 -->
      <div class="date-picker-area" v-if="quickDate === 'custom'">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleDateRangeChange"
        />
        <el-button type="primary" icon="el-icon-search" @click="loadSummary">查询</el-button>
      </div>

      <!-- 核心指标卡片 -->
      <div class="summary-cards">
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="summary-card">
              <div class="summary-icon" style="background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);">
                <i class="el-icon-money"></i>
              </div>
              <div class="summary-content">
                <div class="summary-label">本月总收款</div>
                <div class="summary-value income">¥{{ summaryData.totalIncome.toLocaleString() }}</div>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="summary-card">
              <div class="summary-icon" style="background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);">
                <i class="el-icon-wallet"></i>
              </div>
              <div class="summary-content">
                <div class="summary-label">本月充值</div>
                <div class="summary-value">¥{{ summaryData.rechargeIncome.toLocaleString() }}</div>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="summary-card">
              <div class="summary-icon" style="background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);">
                <i class="el-icon-postcard"></i>
              </div>
              <div class="summary-content">
                <div class="summary-label">本月现金续卡</div>
                <div class="summary-value">¥{{ summaryData.renewIncome.toLocaleString() }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section a-card">
      <el-row :gutter="24">
        <!-- 收款趋势图 - 柱状图 -->
        <el-col :span="14">
          <div class="chart-wrapper">
            <div class="chart-header">
              <h3><i class="el-icon-s-data"></i> 收款趋势图</h3>
            </div>
            <div id="trendChart" style="height: 340px"></div>
          </div>
        </el-col>
        <!-- 收入构成分析 - 环形图 -->
        <el-col :span="10">
          <div class="chart-wrapper">
            <div class="chart-header">
              <h3><i class="el-icon-pie-chart"></i> 收入构成分析</h3>
            </div>
            <div id="pieChart" style="height: 340px"></div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 业务类型汇总表 -->
    <div class="detail-card a-card" v-loading="loading">
      <div class="card-header">
        <h3><i class="el-icon-s-grid"></i> 业务类型汇总表</h3>
      </div>
      
      <el-table :data="summaryData.details" border stripe style="width: 100%">
        <el-table-column prop="period" label="日期" width="150" />
        <el-table-column prop="rechargeAmount" label="充值金额" width="140">
          <template slot-scope="scope">
            <span class="amount">¥{{ (scope.row.rechargeAmount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="renewAmount" label="续卡金额" width="140">
          <template slot-scope="scope">
            <span class="amount">¥{{ (scope.row.renewAmount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="cashAmount" label="现金" width="120">
          <template slot-scope="scope">
            <span class="amount">¥{{ (scope.row.cashAmount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="onlineAmount" label="在线支付" width="120">
          <template slot-scope="scope">
            <span class="amount">¥{{ (scope.row.onlineAmount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="合计">
          <template slot-scope="scope">
            <span class="amount total">¥{{ (scope.row.totalAmount || 0).toLocaleString() }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getCashSummary } from '@/api/allApi'

export default {
  name: 'CashFlowSummary',
  data() {
    return {
      loading: false,
      quickDate: 'month',
      dateRange: [],
      summaryData: {
        totalIncome: 0,
        rechargeIncome: 0,
        renewIncome: 0,
        details: []
      },
      reportTitle: '',
      trendChart: null,
      pieChart: null
    }
  },
  created() {
    this.selectQuickDate('month')
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.pieChart) {
      this.pieChart.dispose()
    }
  },
  methods: {
    selectQuickDate(type) {
      this.quickDate = type
      
      const now = new Date()
      const formatDate = (date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      if (type === 'today') {
        const today = formatDate(now)
        this.loadSummaryData(today, today)
      } else if (type === 'week') {
        const dayOfWeek = now.getDay() || 7
        const monday = new Date(now)
        monday.setDate(now.getDate() - dayOfWeek + 1)
        this.loadSummaryData(formatDate(monday), formatDate(now))
      } else if (type === 'month') {
        const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
        this.loadSummaryData(formatDate(firstDay), formatDate(now))
      }
      // 自定义模式不自动加载，等待用户选择日期
    },
    handleDateRangeChange(val) {
      if (val && val.length === 2) {
        this.loadSummaryData(val[0], val[1])
      }
    },
    async loadSummary() {
      if (this.dateRange && this.dateRange.length === 2) {
        this.loadSummaryData(this.dateRange[0], this.dateRange[1])
      }
    },
    async loadSummaryData(start, end) {
      this.loading = true
      try {
        const params = {
          start: start,
          end: end
        }
        
        const res = await getCashSummary(params)
        const responseData = res.data
        
        if (responseData && responseData.code === 200) {
          const totalMap = responseData.total || {}
          this.summaryData = {
            totalIncome: totalMap.totalIncome || 0,
            rechargeIncome: totalMap.rechargeIncome || 0,
            renewIncome: totalMap.renewIncome || 0,
            details: Array.isArray(responseData.data) ? responseData.data : []
          }
          
          this.$nextTick(() => {
            this.updateCharts()
          })
        }
      } catch (error) {
        console.error('获取汇总数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    initCharts() {
      this.trendChart = echarts.init(document.getElementById('trendChart'))
      this.pieChart = echarts.init(document.getElementById('pieChart'))
    },
    updateCharts() {
      if (!this.trendChart || !this.pieChart) {
        this.initCharts()
      }
      
      // 更新趋势图（柱状图）
      const dates = this.summaryData.details.map(item => item.period)
      const totals = this.summaryData.details.map(item => item.totalAmount || 0)
      
      this.trendChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br/>收款金额: ¥{c}'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '金额(元)'
        },
        series: [{
          data: totals,
          type: 'bar',
          barWidth: '50%',
          itemStyle: {
            color: '#409EFF'
          }
        }]
      })
      
      // 更新环形图
      const recharge = this.summaryData.rechargeIncome
      const renew = this.summaryData.renewIncome
      
      this.pieChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['充值', '现金续卡']
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            formatter: '{b}: ¥{c}\n{d}%'
          },
          data: [
            { value: recharge, name: '充值' },
            { value: renew, name: '现金续卡' }
          ],
          itemStyle: {
            color: function(params) {
              const colorList = ['#67C23A', '#E6A23C']
              return colorList[params.dataIndex]
            }
          }
        }]
      })
    }
  }
}
</script>

<style scoped>
.cash-flow-summary-container {
  padding: 24px;
}

.page-header-section {
  margin-bottom: 24px;
  padding: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 14px;
}

.page-title i {
  font-size: 32px;
  color: var(--admin-primary);
}

.page-title h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
  font-weight: 700;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin-left: 14px;
  padding-left: 14px;
  border-left: 1px solid #dcdfe6;
}

.quick-date-btns {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.date-picker-area {
  margin-bottom: 24px;
}

.date-picker-area .el-date-picker {
  margin-right: 10px;
}

.summary-cards {
  margin-top: 24px;
}

.summary-card {
  display: flex;
  align-items: center;
  padding: 28px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid #f0f2f5;
}

.summary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.summary-icon {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
}

.summary-icon i {
  font-size: 32px;
  color: #fff;
}

.summary-content {
  flex: 1;
}

.summary-label {
  font-size: 15px;
  color: #909399;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.summary-value.income {
  color: #67c23a;
}

.charts-section {
  margin-bottom: 24px;
  padding: 24px;
}

.chart-wrapper {
  height: 400px;
}

.chart-header {
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-header h3 i {
  color: var(--admin-primary);
}

.detail-card {
  padding: 24px;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header h3 i {
  color: var(--admin-primary);
}

.amount {
  font-weight: 600;
}

.amount.total {
  font-weight: bold;
  color: #409EFF;
}
</style>
