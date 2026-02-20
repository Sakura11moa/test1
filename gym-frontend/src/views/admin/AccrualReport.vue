<template>
  <div class="accrual-report-container">
    <!-- 页面头部 -->
    <div class="page-header-section a-card">
      <div class="header-content">
        <div class="page-title">
          <i class="el-icon-s-data"></i>
          <h1>权责发生制财务报表</h1>
          <span class="page-subtitle">反映企业实际经营成果的财务报告</span>
        </div>
      </div>

      <!-- 期间选择器 -->
      <div class="period-selector">
        <el-button-group>
          <el-button :type="currentPeriod === 'last' ? 'primary' : ''" @click="changePeriod('last')" icon="el-icon-arrow-left">上月</el-button>
          <el-button :type="currentPeriod === 'current' ? 'primary' : ''" @click="changePeriod('current')">本月</el-button>
          <el-button :type="currentPeriod === 'next' ? 'primary' : ''" @click="changePeriod('next')">下月<i class="el-icon-arrow-right"></i></el-button>
        </el-button-group>
        <el-date-picker
          v-model="currentYearMonth"
          type="month"
          placeholder="选择月份"
          format="yyyy年MM月"
          value-format="yyyy-MM"
          @change="handleMonthChange"
          style="margin-left: 15px"
        />
        <span class="current-month-text">{{ currentMonthText }}</span>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="core-metrics-section">
      <el-row :gutter="24">
        <el-col :span="8">
          <div class="metric-card">
            <div class="metric-icon" style="background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);">
              <i class="el-icon-s-finance"></i>
            </div>
            <div class="metric-content">
              <div class="metric-label">本月确认收入</div>
              <div class="metric-value primary">¥{{ formatNumber(metrics.monthRecognizedIncome) }}</div>
              <div class="metric-desc">会籍摊销 + 课程核销</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="metric-card">
            <div class="metric-icon" style="background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);">
              <i class="el-icon-s-cooperation"></i>
            </div>
            <div class="metric-content">
              <div class="metric-label">月末预收负债余额</div>
              <div class="metric-value warning">¥{{ formatNumber(metrics.monthEndDeferredBalance) }}</div>
              <div class="metric-desc">储值 + 未耗会籍 + 未耗课程</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="metric-card">
            <div class="metric-icon" style="background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);">
              <i class="el-icon-s-release"></i>
            </div>
            <div class="metric-content">
              <div class="metric-label">本月退课总金额</div>
              <div class="metric-value danger">¥{{ formatNumber(metrics.monthRefundAmount) }}</div>
              <div class="metric-desc">退课返还储值余额</div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="24" style="margin-top: 24px;">
        <el-col :span="12">
          <div class="metric-card">
            <div class="metric-icon" style="background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);">
              <i class="el-icon-s-custom"></i>
            </div>
            <div class="metric-content">
              <div class="metric-label">本月新增会籍负债</div>
              <div class="metric-value success">¥{{ formatNumber(metrics.monthNewCardLiability) }}</div>
              <div class="metric-desc">现金续卡 + 余额续卡</div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="metric-card">
            <div class="metric-icon" style="background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);">
              <i class="el-icon-s-marketing"></i>
            </div>
            <div class="metric-content">
              <div class="metric-label">本月新增课程负债</div>
              <div class="metric-value info">¥{{ formatNumber(metrics.monthNewCourseLiability) }}</div>
              <div class="metric-desc">余额购课</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 储值余额变动摘要 -->
    <div class="balance-change-section a-card">
      <div class="section-header">
        <h3><i class="el-icon-s-order"></i> 储值余额变动摘要</h3>
      </div>
      <el-row :gutter="24" class="balance-flow">
        <el-col :span="4">
          <div class="balance-item">
            <div class="balance-label">期初储值余额</div>
            <div class="balance-value">¥{{ formatNumber(balanceChange.beginningBalance) }}</div>
          </div>
        </el-col>
        <el-col :span="2">
          <div class="balance-arrow">→</div>
        </el-col>
        <el-col :span="3">
          <div class="balance-item">
            <div class="balance-label">储值充值</div>
            <div class="balance-value in">+¥{{ formatNumber(balanceChange.rechargeIn) }}</div>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="balance-item">
            <div class="balance-label">余额续卡</div>
            <div class="balance-value out">-¥{{ formatNumber(balanceChange.balanceRenew) }}</div>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="balance-item">
            <div class="balance-label">余额购课</div>
            <div class="balance-value out">-¥{{ formatNumber(balanceChange.balanceCourse) }}</div>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="balance-item">
            <div class="balance-label">退课返还</div>
            <div class="balance-value in">+¥{{ formatNumber(balanceChange.refundIn) }}</div>
          </div>
        </el-col>
        <el-col :span="2">
          <div class="balance-arrow">→</div>
        </el-col>
        <el-col :span="4">
          <div class="balance-item">
            <div class="balance-label">期末储值余额</div>
            <div class="balance-value">¥{{ formatNumber(balanceChange.endingBalance) }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 预收负债构成 -->
    <div class="deferred-composition-section a-card">
      <div class="section-header">
        <h3><i class="el-icon-s-data"></i> 预收负债构成</h3>
      </div>
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="deferred-card balance-card">
            <div class="deferred-header">
              <span class="deferred-title">储值余额</span>
              <span class="deferred-percent">{{ calculatePercent(deferredComposition.balance, 'totalDeferred') }}%</span>
            </div>
            <div class="deferred-amount">¥{{ formatNumber(deferredComposition.balance) }}</div>
            <div class="deferred-bar">
              <div class="deferred-bar-fill balance" :style="{ width: calculatePercent(deferredComposition.balance, 'totalDeferred') + '%' }"></div>
            </div>
          </div>
        </el-col>
        <el-col :span="9">
          <div class="deferred-card card-deferred-card">
            <div class="deferred-header">
              <span class="deferred-title">未耗会籍</span>
              <span class="deferred-percent">{{ calculatePercent(deferredComposition.cardDeferred, 'totalDeferred') }}%</span>
            </div>
            <div class="deferred-amount">¥{{ formatNumber(deferredComposition.cardDeferred) }}</div>
            <div class="deferred-split">
              <div class="split-item">
                <span class="split-label">现金续卡</span>
                <span class="split-value">¥{{ formatNumber(deferredComposition.cardDeferredCash) }}</span>
              </div>
              <div class="split-item">
                <span class="split-label">余额续卡</span>
                <span class="split-value">¥{{ formatNumber(deferredComposition.cardDeferredBalance) }}</span>
              </div>
            </div>
            <div class="deferred-bar">
              <div class="deferred-bar-fill card" :style="{ width: calculatePercent(deferredComposition.cardDeferred, 'totalDeferred') + '%' }"></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="deferred-card course-deferred-card">
            <div class="deferred-header">
              <span class="deferred-title">未耗课程</span>
              <span class="deferred-percent">{{ calculatePercent(deferredComposition.courseDeferred, 'totalDeferred') }}%</span>
            </div>
            <div class="deferred-amount">¥{{ formatNumber(deferredComposition.courseDeferred) }}</div>
            <div class="deferred-bar">
              <div class="deferred-bar-fill course" :style="{ width: calculatePercent(deferredComposition.courseDeferred, 'totalDeferred') + '%' }"></div>
            </div>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="deferred-card total-deferred-card">
            <div class="deferred-header">
              <span class="deferred-title">合计</span>
            </div>
            <div class="deferred-amount">¥{{ formatNumber(deferredComposition.total) }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 到期时间分布 -->
    <div class="maturity-section a-card">
      <div class="section-header">
        <h3><i class="el-icon-time"></i> 到期时间分布</h3>
      </div>
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="maturity-card">
            <div class="maturity-icon"><i class="el-icon-time"></i></div>
            <div class="maturity-label">1个月内到期</div>
            <div class="maturity-amount">¥{{ formatNumber(maturityDistribution.withinOneMonth) }}</div>
            <div class="maturity-percent">{{ calculatePercent(maturityDistribution.withinOneMonth, 'maturity') }}%</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="maturity-card">
            <div class="maturity-icon"><i class="el-icon-time"></i></div>
            <div class="maturity-label">1-3个月到期</div>
            <div class="maturity-amount">¥{{ formatNumber(maturityDistribution.oneToThreeMonths) }}</div>
            <div class="maturity-percent">{{ calculatePercent(maturityDistribution.oneToThreeMonths, 'maturity') }}%</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="maturity-card">
            <div class="maturity-icon"><i class="el-icon-time"></i></div>
            <div class="maturity-label">3-6个月到期</div>
            <div class="maturity-amount">¥{{ formatNumber(maturityDistribution.threeToSixMonths) }}</div>
            <div class="maturity-percent">{{ calculatePercent(maturityDistribution.threeToSixMonths, 'maturity') }}%</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="maturity-card">
            <div class="maturity-icon"><i class="el-icon-time"></i></div>
            <div class="maturity-label">6个月以上到期</div>
            <div class="maturity-amount">¥{{ formatNumber(maturityDistribution.overSixMonths) }}</div>
            <div class="maturity-percent">{{ calculatePercent(maturityDistribution.overSixMonths, 'maturity') }}%</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 内部划转与退课统计 -->
    <div class="transfer-refund-section a-card">
      <div class="section-header">
        <h3><i class="el-icon-s-operation"></i> 内部划转与退课统计</h3>
      </div>
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);">
              <i class="el-icon-s-finance"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月余额续卡金额</div>
              <div class="stat-value">¥{{ formatNumber(transferStats.balanceRenew) }}</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);">
              <i class="el-icon-s-goods"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月余额购课金额</div>
              <div class="stat-value">¥{{ formatNumber(transferStats.balanceCourse) }}</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月退课总次数</div>
              <div class="stat-value">{{ transferStats.refundCount }} 次</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);">
              <i class="el-icon-s-release"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月退课总金额</div>
              <div class="stat-value danger">¥{{ formatNumber(transferStats.refundAmount) }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 明细列表 -->
    <div class="detail-section a-card">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="收入确认明细" name="recognition">
          <el-table :data="recognitionDetails" border stripe v-loading="loading">
            <el-table-column prop="createTime" label="确认日期" width="160" />
            <el-table-column prop="memberNo" label="会员编号" width="100" />
            <el-table-column prop="memberName" label="会员姓名" width="100" />
            <el-table-column prop="bizTypeName" label="收入类型" width="80" />
            <el-table-column prop="courseName" label="课程名称" width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.courseName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="100">
              <template slot-scope="scope">
                <span class="amount">¥{{ formatNumber(scope.row.amount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sourceBiz" label="来源业务" min-width="130" />
            <el-table-column prop="bizNo" label="关联单号" width="150" />
            <el-table-column prop="period" label="归属月份" width="90" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="退课明细" name="refund">
          <el-table :data="refundDetails" border stripe v-loading="loading">
            <el-table-column prop="createTime" label="退课时间" width="160" />
            <el-table-column prop="memberNo" label="会员编号" width="100" />
            <el-table-column prop="memberName" label="会员姓名" width="100" />
            <el-table-column prop="courseName" label="课程名称" width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.courseName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="refundTimes" label="退课节数" width="90">
              <template slot-scope="scope">
                <span>{{ scope.row.refundTimes || 0 }} 节</span>
              </template>
            </el-table-column>
            <el-table-column prop="originalPurchaseNo" label="原购课单号" width="150" />
            <el-table-column prop="refundAmount" label="退课金额" width="100">
              <template slot-scope="scope">
                <span class="amount">¥{{ formatNumber(scope.row.refundAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="refundChannelName" label="退款方式" width="90" />
            <el-table-column prop="refundReason" label="退课原因" min-width="120" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { getAccrualReportOverview } from '@/api/allApi'

export default {
  name: 'AccrualReport',
  data() {
    return {
      loading: false,
      currentPeriod: 'current',
      currentYearMonth: '',
      activeTab: 'recognition',
      // 核心指标
      metrics: {
        monthRecognizedIncome: 0,
        monthEndDeferredBalance: 0,
        monthRefundAmount: 0,
        monthNewCardLiability: 0,
        monthNewCourseLiability: 0
      },
      // 储值余额变动摘要
      balanceChange: {
        beginningBalance: 0,
        rechargeIn: 0,
        balanceRenew: 0,
        balanceCourse: 0,
        refundIn: 0,
        endingBalance: 0
      },
      // 预收负债构成
      deferredComposition: {
        balance: 0,
        cardDeferred: 0,
        cardDeferredCash: 0,
        cardDeferredBalance: 0,
        courseDeferred: 0,
        total: 0
      },
      // 到期时间分布
      maturityDistribution: {
        withinOneMonth: 0,
        oneToThreeMonths: 0,
        threeToSixMonths: 0,
        overSixMonths: 0,
        total: 0
      },
      // 内部划转与退课统计
      transferStats: {
        balanceRenew: 0,
        balanceCourse: 0,
        refundCount: 0,
        refundAmount: 0
      },
      // 明细列表
      recognitionDetails: [],
      refundDetails: []
    }
  },
  computed: {
    currentMonthText() {
      if (!this.currentYearMonth) return ''
      const [year, month] = this.currentYearMonth.split('-')
      return `${year}年${parseInt(month)}月`
    }
  },
  mounted() {
    this.initDate()
    this.loadData()
  },
  methods: {
    initDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      this.currentYearMonth = `${year}-${month}`
    },
    handleMonthChange(val) {
      if (val) {
        this.currentPeriod = ''
        this.loadData()
      }
    },
    changePeriod(period) {
      this.currentPeriod = period
      const now = new Date()
      let targetDate = new Date(now)

      if (period === 'last') {
        targetDate.setMonth(targetDate.getMonth() - 1)
      } else if (period === 'next') {
        targetDate.setMonth(targetDate.getMonth() + 1)
      }

      const year = targetDate.getFullYear()
      const month = String(targetDate.getMonth() + 1).padStart(2, '0')
      this.currentYearMonth = `${year}-${month}`

      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getAccrualReportOverview({ yearMonth: this.currentYearMonth })
        
        // axios 封装后，后端数据在 res.data 中
        const responseData = res.data
        console.log('后端返回数据:', responseData)
        
        if (responseData.code === 200 && responseData.data) {
          const data = responseData.data
          // 核心指标
          this.metrics = {
            monthRecognizedIncome: data.monthRecognizedIncome || 0,
            monthEndDeferredBalance: data.monthEndDeferredBalance || 0,
            monthRefundAmount: data.monthRefundAmount || 0,
            monthNewCardLiability: data.monthNewCardLiability || 0,
            monthNewCourseLiability: data.monthNewCourseLiability || 0
          }
          // 储值余额变动摘要
          if (data.balanceChangeSummary) {
            const bc = data.balanceChangeSummary
            this.balanceChange = {
              beginningBalance: bc.beginningBalance || 0,
              rechargeIn: bc.rechargeIn || 0,
              balanceRenew: bc.balanceRenew || 0,
              balanceCourse: bc.balanceCourse || 0,
              refundIn: bc.refundIn || 0,
              endingBalance: bc.endingBalance || 0
            }
          }
          // 预收负债构成 - 需要处理嵌套结构
          if (data.deferredComposition) {
            const dc = data.deferredComposition
            this.deferredComposition = {
              balance: dc.balance || 0,
              cardDeferred: dc.cardDeferred?.total || dc.cardDeferred || 0,
              cardDeferredCash: dc.cardDeferred?.cash || 0,
              cardDeferredBalance: dc.cardDeferred?.balance || 0,
              courseDeferred: dc.courseDeferred?.total || dc.courseDeferred || 0,
              total: dc.totalDeferred || dc.total || 0
            }
          }
          // 到期时间分布
          if (data.maturityDistribution) {
            const md = data.maturityDistribution
            this.maturityDistribution = {
              withinOneMonth: md.withinOneMonth || 0,
              oneToThreeMonths: md.oneToThreeMonths || 0,
              threeToSixMonths: md.threeToSixMonths || 0,
              overSixMonths: md.overSixMonths || 0,
              total: md.total || 0
            }
          }
          // 内部划转与退课统计 - 修正为正确字段
          this.transferStats = {
            balanceRenew: data.balanceChangeSummary?.balanceRenew || 0,
            balanceCourse: data.balanceChangeSummary?.balanceCourse || 0,
            refundCount: data.refundCount || (data.refundDetails ? data.refundDetails.length : 0),
            refundAmount: data.monthRefundAmount || 0
          }
          // 明细列表
          this.recognitionDetails = data.recognitionDetails || []
          this.refundDetails = data.refundDetails || []
        }
      } catch (error) {
        console.error('获取权责报表数据失败:', error)
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    formatNumber(num) {
      if (num == null || isNaN(num)) return '0.00'
      return Number(num).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    calculatePercent(value, type = 'totalDeferred') {
      let total = 1
      if (type === 'totalDeferred') {
        // 分母为储值+未耗会籍+未耗课程
        total = this.metrics.monthEndDeferredBalance || 1
      } else if (type === 'maturity') {
        // 分母为会籍+课程负债总额
        total = (this.deferredComposition.cardDeferred || 0) + (this.deferredComposition.courseDeferred || 0) || 1
      }
      const percent = ((value || 0) / total * 100).toFixed(1)
      return percent
    }
  }
}
</script>

<style scoped>
.accrual-report-container {
  padding: 24px;
}

/* 页面头部 */
.page-header-section {
  margin-bottom: 24px;
  padding: 24px;
}

.header-content {
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

/* 期间选择器 */
.period-selector {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.current-month-text {
  font-size: 18px;
  color: #606266;
  font-weight: 600;
}

/* 核心指标卡片 */
.core-metrics-section {
  margin-bottom: 24px;
}

.metric-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 0;
  transition: all 0.3s ease;
  border: 1px solid #f0f2f5;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.metric-icon {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
}

.metric-icon i {
  font-size: 32px;
  color: #fff;
}

.metric-content {
  flex: 1;
}

.metric-label {
  font-size: 15px;
  color: #909399;
  margin-bottom: 10px;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
}

.metric-desc {
  font-size: 13px;
  color: #c0c4cc;
  margin-top: 6px;
}

.metric-value.primary { color: #409eff; }
.metric-value.warning { color: #e6a23c; }
.metric-value.danger { color: #f56c6c; }
.metric-value.success { color: #67c23a; }
.metric-value.info { color: #909399; }

/* 区块通用样式 */
.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header h3 i {
  color: var(--admin-primary);
}

/* 储值余额变动摘要 */
.balance-change-section {
  padding: 24px;
  margin-bottom: 24px;
}

.balance-flow {
  display: flex;
  align-items: center;
}

.balance-item {
  text-align: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.balance-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.balance-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.balance-value.in { color: #67c23a; }
.balance-value.out { color: #f56c6c; }

.balance-arrow {
  font-size: 24px;
  color: #409eff;
  text-align: center;
}

/* 预收负债构成 */
.deferred-composition-section {
  padding: 24px;
  margin-bottom: 24px;
}

.deferred-card {
  border-radius: 12px;
  padding: 20px;
  background: #f8f9fa;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.deferred-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.deferred-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.deferred-title {
  font-size: 15px;
  color: #606266;
  font-weight: 500;
}

.deferred-percent {
  font-size: 16px;
  color: #409eff;
  font-weight: 700;
}

.deferred-amount {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 16px;
}

.deferred-split {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.split-item {
  display: flex;
  flex-direction: column;
}

.split-label {
  font-size: 13px;
  color: #909399;
}

.split-value {
  font-size: 15px;
  color: #606266;
  font-weight: 600;
}

.deferred-bar {
  height: 8px;
  background: #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.deferred-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.deferred-bar-fill.balance { background: linear-gradient(90deg, #409eff, #66b1ff); }
.deferred-bar-fill.card { background: linear-gradient(90deg, #67c23a, #85ce61); }
.deferred-bar-fill.course { background: linear-gradient(90deg, #e6a23c, #ebb563); }

.total-deferred-card {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
}

.total-deferred-card .deferred-title,
.total-deferred-card .deferred-amount {
  color: #fff;
}

.total-deferred-card .deferred-percent {
  color: rgba(255, 255, 255, 0.8);
}

/* 到期时间分布 */
.maturity-section {
  padding: 24px;
  margin-bottom: 24px;
}

.maturity-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.maturity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.maturity-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.maturity-icon i {
  font-size: 22px;
  color: #fff;
}

.maturity-label {
  font-size: 15px;
  color: #606266;
  margin-bottom: 12px;
}

.maturity-amount {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.maturity-percent {
  font-size: 15px;
  color: #909399;
}

/* 内部划转与退课统计 */
.transfer-refund-section {
  padding: 24px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-icon i {
  font-size: 26px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.stat-value.danger {
  color: #f56c6c;
}

/* 明细列表 */
.detail-section {
  padding: 24px;
}

.amount {
  font-weight: 600;
  color: #67c23a;
}
</style>
