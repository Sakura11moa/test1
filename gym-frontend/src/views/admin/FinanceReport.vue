<template>
  <div class="finance-report-container">
    <div class="page-header">
      <h2>财务报表</h2>
    </div>
    <!-- 日期类型切换 -->
    <div class="report-type-switch">
      <el-radio-group v-model="reportType" @change="handleTypeChange">
        <el-radio-button label="daily">日报</el-radio-button>
        <el-radio-button label="monthly">月报</el-radio-button>
      </el-radio-group>
    </div>
    <!-- 日期选择 -->
    <div class="date-picker-area">
      <el-date-picker
        v-model="selectedDate"
        :type="reportType === 'daily' ? 'date' : 'month'"
        :placeholder="reportType === 'daily' ? '选择日期' : '选择月份'"
        :value-format="reportType === 'daily' ? 'yyyy-MM-dd' : 'yyyy-MM'"
        @change="handleDateChange"
      />
      <el-button type="primary" @click="loadReport">查询</el-button>
    </div>
    <!-- 报表内容 -->
    <div class="report-content" v-loading="loading">
      <el-card class="report-card">
        <div class="report-title">
          <h3>{{ reportTitle }}</h3>
        </div>
        <el-table :data="reportData" border stripe style="width: 100%">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="rechargeAmount" label="充值金额" width="150">
            <template slot-scope="scope">
              ¥{{ scope.row.rechargeAmount ? scope.row.rechargeAmount.toLocaleString() : '0' }}
            </template>
          </el-table-column>
          <el-table-column prop="courseAmount" label="课程收入" width="150">
            <template slot-scope="scope">
              ¥{{ scope.row.courseAmount ? scope.row.courseAmount.toLocaleString() : '0' }}
            </template>
          </el-table-column>
          <el-table-column prop="venueAmount" label="场地收入" width="150">
            <template slot-scope="scope">
              ¥{{ scope.row.venueAmount ? scope.row.venueAmount.toLocaleString() : '0' }}
            </template>
          </el-table-column>
          <el-table-column prop="coachAmount" label="教练课收入" width="150">
            <template slot-scope="scope">
              ¥{{ scope.row.coachAmount ? scope.row.coachAmount.toLocaleString() : '0' }}
            </template>
          </el-table-column>
          <el-table-column prop="memberCardAmount" label="会员卡收入" width="150">
            <template slot-scope="scope">
              ¥{{ scope.row.memberCardAmount ? scope.row.memberCardAmount.toLocaleString() : '0' }}
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="总收入">
            <template slot-scope="scope">
              <span class="total-amount">¥{{ scope.row.totalAmount ? scope.row.totalAmount.toLocaleString() : '0' }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <!-- 图表展示 -->
      <el-card class="chart-card" v-if="reportData.length > 0">
        <div id="financeChart" style="width: 100%; height: 400px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getFinanceReport } from '@/api/allApi'

export default {
  name: 'FinanceReport',
  data() {
    return {
      reportType: 'daily',
      selectedDate: new Date().toISOString().slice(0, 10),
      reportTitle: '',
      reportData: [],
      loading: false
    }
  },
  mounted() {
    this.loadReport()
  },
  methods: {
    handleTypeChange() {
      if (this.reportType === 'monthly') {
        this.selectedDate = new Date().toISOString().slice(0, 7)
      } else {
        this.selectedDate = new Date().toISOString().slice(0, 10)
      }
      this.loadReport()
    },
    handleDateChange() {
      this.loadReport()
    },
    async loadReport() {
      this.loading = true
      try {
        const params = {
          type: this.reportType,
          date: this.selectedDate
        }
        const res = await getFinanceReport(params)
        if (res.data) {
          this.reportData = Array.isArray(res.data) ? res.data : [res.data]
          this.reportTitle = this.reportType === 'daily' 
            ? `${this.selectedDate} 日报表` 
            : `${this.selectedDate} 月报表`
          this.$nextTick(() => {
            this.initChart()
          })
        }
      } catch (error) {
        console.error('获取报表失败:', error)
        this.$message.error('获取报表数据失败')
      } finally {
        this.loading = false
      }
    },
    initChart() {
      const chartDom = document.getElementById('financeChart')
      if (!chartDom) return
      
      if (this.chartInstance) {
        this.chartInstance.dispose()
      }
      
      const echarts = require('echarts')
      this.chartInstance = echarts.init(chartDom)
      
      const dates = this.reportData.map(item => item.date)
      const seriesData = [
        { name: '充值金额', data: this.reportData.map(item => item.rechargeAmount || 0) },
        { name: '课程收入', data: this.reportData.map(item => item.courseAmount || 0) },
        { name: '场地收入', data: this.reportData.map(item => item.venueAmount || 0) },
        { name: '教练课收入', data: this.reportData.map(item => item.coachAmount || 0) },
        { name: '会员卡收入', data: this.reportData.map(item => item.memberCardAmount || 0) }
      ]
      
      const option = {
        title: {
          text: '收入趋势图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: seriesData.map(item => item.name)
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '金额(元)'
        },
        series: seriesData.map(item => ({
          name: item.name,
          type: 'bar',
          data: item.data
        }))
      }
      
      this.chartInstance.setOption(option)
    }
  },
  beforeDestroy() {
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  }
}
</script>

<style scoped>
.finance-report-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  color: #333;
}
.report-type-switch {
  margin-bottom: 20px;
}
.date-picker-area {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}
.report-card {
  margin-bottom: 20px;
}
.report-title {
  text-align: center;
  margin-bottom: 20px;
}
.report-title h3 {
  margin: 0;
  color: #333;
}
.total-amount {
  font-weight: bold;
  color: #f56c6c;
}
.chart-card {
  margin-bottom: 20px;
}
</style>
