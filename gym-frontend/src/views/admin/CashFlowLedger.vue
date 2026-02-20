<template>
  <div class="cash-flow-ledger-container">
    <!-- 页面头部 -->
    <div class="page-header-section a-card">
      <div class="header-content">
        <div class="page-title">
          <i class="el-icon-s-order"></i>
          <h1>现金流水账本</h1>
          <span class="page-subtitle">记录所有充值和现金续卡流水</span>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ statistics.totalCount }}</span>
            <span class="stat-label">总收款笔数</span>
          </div>
          <div class="stat-item highlight">
            <span class="stat-number income">¥{{ statistics.totalIncome.toLocaleString() }}</span>
            <span class="stat-label">总收款金额</span>
          </div>
        </div>
      </div>

      <!-- 快捷日期选择 -->
      <div class="quick-date-btns">
        <el-button-group>
          <el-button :type="quickDate === 'today' ? 'primary' : ''" @click="selectQuickDate('today')" icon="el-icon-date">今日</el-button>
          <el-button :type="quickDate === 'week' ? 'primary' : ''" @click="selectQuickDate('week')" icon="el-icon-date">本周</el-button>
          <el-button :type="quickDate === 'month' ? 'primary' : ''" @click="selectQuickDate('month')" icon="el-icon-date">本月</el-button>
        </el-button-group>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            @change="handleDateChange"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="会员搜索">
          <el-input v-model="searchForm.keyword" placeholder="姓名/手机号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="流水号">
          <el-input v-model="searchForm.bizNo" placeholder="精确搜索" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="searchForm.channel" multiple placeholder="全部" clearable style="width: 220px">
            <el-option label="微信" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="现金" value="CASH" />
            <el-option label="银行卡" value="BANK_CARD" />
            <el-option label="在线支付" value="ONLINE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 流水列表 -->
    <div class="table-container a-card">
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="createTime" label="交易时间" width="180" />
        <el-table-column prop="ledgerNo" label="流水号" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.ledgerNo || scope.row.bizNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="memberNo" label="会员编号" width="100" />
        <el-table-column label="会员姓名" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.memberName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="bizType" label="业务类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.bizType)">
              {{ getTypeName(scope.row.bizType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template slot-scope="scope">
            <span class="amount">¥{{ scope.row.amount ? scope.row.amount.toLocaleString() : '0' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="channel" label="支付渠道" width="100">
          <template slot-scope="scope">
            {{ getPayMethodName(scope.row.channel) }}
          </template>
        </el-table-column>
        <el-table-column prop="bizNo" label="关联单号" width="180" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getCashFlowList } from '@/api/allApi'

export default {
  name: 'CashFlowLedger',
  data() {
    return {
      loading: false,
      dateRange: [],
      quickDate: '',
      searchForm: {
        start: '',
        end: '',
        keyword: '',
        bizNo: '',
        channel: []
      },
      tableData: [],
      statistics: {
        totalCount: 0,
        totalIncome: 0
      },
      pagination: {
        currentPage: 1,
        pageSize: 15,
        total: 0
      }
    }
  },
  created() {
    // 默认选择本月
    this.selectQuickDate('month')
  },
  methods: {
    selectQuickDate(type) {
      this.quickDate = type
      const now = new Date()
      let start, end
      
      // 格式化日期为 YYYY-MM-DD
      const formatDate = (date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      if (type === 'today') {
        start = formatDate(now)
        end = formatDate(now)
      } else if (type === 'week') {
        // 本周一开始
        const dayOfWeek = now.getDay() || 7
        const monday = new Date(now)
        monday.setDate(now.getDate() - dayOfWeek + 1)
        start = formatDate(monday)
        end = formatDate(now)
      } else if (type === 'month') {
        // 本月1日开始
        const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
        start = formatDate(firstDay)
        end = formatDate(now)
      }
      
      this.dateRange = [start, end]
      this.searchForm.start = start
      this.searchForm.end = end
      this.loadData()
    },
    handleDateChange(val) {
      if (val && val.length === 2) {
        this.searchForm.start = val[0]
        this.searchForm.end = val[1]
        this.quickDate = ''  // 手动选择日期时清除快捷按钮高亮
      } else {
        this.searchForm.start = ''
        this.searchForm.end = ''
      }
      this.loadData()
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.dateRange = []
      this.quickDate = ''
      this.searchForm = {
        start: '',
        end: '',
        keyword: '',
        bizNo: '',
        channel: []
      }
      this.pagination.currentPage = 1
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        // 处理渠道多选参数
        let channelParam = ''
        if (this.searchForm.channel && this.searchForm.channel.length > 0) {
          channelParam = this.searchForm.channel.join(',')
        }
        
        const params = {
          start: this.searchForm.start,
          end: this.searchForm.end,
          keyword: this.searchForm.keyword,
          bizNo: this.searchForm.bizNo,
          channel: channelParam,
          page: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        
        const res = await getCashFlowList(params)
        const responseData = res.data
        
        if (responseData && responseData.code === 200) {
          this.tableData = Array.isArray(responseData.data) ? responseData.data : []
          this.pagination.total = responseData.total || 0
          
          // 使用后端返回的总金额
          this.statistics.totalCount = responseData.total || 0
          this.statistics.totalIncome = responseData.totalIncome || 0
        }
      } catch (error) {
        console.error('获取流水数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadData()
    },
    getTypeName(type) {
      const typeMap = {
        'RECHARGE': '充值',
        'CARD_RENEW': '现金续卡'
      }
      return typeMap[type] || type
    },
    getTypeTag(type) {
      const tagMap = {
        'RECHARGE': 'success',
        'CARD_RENEW': 'warning'
      }
      return tagMap[type] || ''
    },
    getPayMethodName(method) {
      const methodMap = {
        'WECHAT': '微信',
        'ALIPAY': '支付宝',
        'CASH': '现金',
        'BALANCE': '余额',
        'BANK_CARD': '银行卡',
        'ONLINE': '在线支付'
      }
      return methodMap[method] || method
    }
  }
}
</script>

<style scoped>
.cash-flow-ledger-container {
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

.header-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid #ebeef5;
}

.stat-item.highlight {
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
  border-color: #67c23a;
}

.stat-item .stat-number {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-item .stat-number.income {
  color: #67c23a;
}

.stat-item .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 6px;
}

.quick-date-btns {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.search-form {
  margin-top: 20px;
}

.search-form .el-form-item {
  margin-bottom: 0;
  margin-right: 20px;
}

.table-container {
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
}

.table-container .el-table {
  border-radius: 8px;
}

.pagination-wrapper {
  margin-top: 24px;
  text-align: right;
  padding: 16px 20px;
  background: #fafafa;
  border-radius: 0 0 8px 8px;
}

.amount {
  font-weight: 600;
  color: #67c23a;
}
</style>
