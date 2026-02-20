<template>
  <div class="finance-ledger-container">
    <div class="page-header">
      <h2>财务流水管理</h2>
    </div>
    <!-- 搜索区域 -->
    <div class="search-area">
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
          />
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="会员充值" value="recharge" />
            <el-option label="课程购买" value="course" />
            <el-option label="场地预约" value="venue" />
            <el-option label="教练课购买" value="coach" />
            <el-option label="会员卡购买" value="member_card" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="searchForm.payMethod" placeholder="请选择" clearable>
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="现金" value="cash" />
            <el-option label="刷卡" value="card" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-label">总收入</div>
              <div class="stat-value">¥{{ statistics.totalIncome.toLocaleString() }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-label">充值收入</div>
              <div class="stat-value">¥{{ statistics.rechargeIncome.toLocaleString() }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-label">课程收入</div>
              <div class="stat-value">¥{{ statistics.courseIncome.toLocaleString() }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-label">其他收入</div>
              <div class="stat-value">¥{{ statistics.otherIncome.toLocaleString() }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <!-- 流水列表 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading" 
        style="width: 100%"
        :header-cell-style="{ background: 'linear-gradient(135deg, #409EFF 0%, #67C23A 100%)', color: '#fff', border: 'none' }"
      >
        <el-table-column prop="transactionDate" label="交易日期" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="memberName" label="会员姓名" width="120" />
        <el-table-column prop="memberPhone" label="手机号" width="130" />
        <el-table-column prop="transactionType" label="交易类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.transactionType)">
              {{ getTypeName(scope.row.transactionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100">
          <template slot-scope="scope">
            {{ getPayMethodName(scope.row.payMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template slot-scope="scope">
            <span class="amount">¥{{ scope.row.amount ? scope.row.amount.toLocaleString() : '0' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <!-- 分页 -->
      <div class="pagination">
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
    </el-card>
  </div>
</template>

<script>
import { getLedgerList, exportLedgerExcel } from '@/api/allApi'

export default {
  name: 'FinanceLedger',
  data() {
    return {
      searchForm: {
        startDate: '',
        endDate: '',
        type: '',
        payMethod: ''
      },
      dateRange: [],
      tableData: [],
      loading: false,
      statistics: {
        totalIncome: 0,
        rechargeIncome: 0,
        courseIncome: 0,
        otherIncome: 0
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    handleDateChange(val) {
      if (val && val.length === 2) {
        this.searchForm.startDate = val[0]
        this.searchForm.endDate = val[1]
      } else {
        this.searchForm.startDate = ''
        this.searchForm.endDate = ''
      }
      this.loadData()
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        startDate: '',
        endDate: '',
        type: '',
        payMethod: ''
      }
      this.dateRange = []
      this.pagination.currentPage = 1
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          ...this.searchForm,
          page: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const res = await getLedgerList(params)
        if (res.data) {
          this.tableData = Array.isArray(res.data.list) ? res.data.list : []
          this.pagination.total = res.data.total || 0
          this.calculateStatistics()
        }
      } catch (error) {
        console.error('获取流水数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    calculateStatistics() {
      let totalIncome = 0
      let rechargeIncome = 0
      let courseIncome = 0
      let otherIncome = 0
      
      this.tableData.forEach(item => {
        const amount = item.amount || 0
        totalIncome += amount
        
        if (item.transactionType === 'recharge') {
          rechargeIncome += amount
        } else if (item.transactionType === 'course') {
          courseIncome += amount
        } else {
          otherIncome += amount
        }
      })
      
      this.statistics = {
        totalIncome,
        rechargeIncome,
        courseIncome,
        otherIncome
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
    handleExport() {
      this.$confirm('确定要导出财务流水Excel吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(async () => {
        try {
          const res = await exportLedgerExcel(this.searchForm)
          const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
          const link = document.createElement('a')
          link.href = window.URL.createObjectURL(blob)
          link.download = `财务流水_${new Date().toISOString().slice(0, 10)}.xlsx`
          link.click()
          this.$message.success('导出成功')
        } catch (error) {
          console.error('导出失败:', error)
          this.$message.error('导出失败')
        }
      }).catch(() => {})
    },
    getTypeName(type) {
      const typeMap = {
        'recharge': '会员充值',
        'course': '课程购买',
        'venue': '场地预约',
        'coach': '教练课购买',
        'member_card': '会员卡购买'
      }
      return typeMap[type] || type
    },
    getTypeTag(type) {
      const tagMap = {
        'recharge': 'success',
        'course': 'primary',
        'venue': 'warning',
        'coach': 'info',
        'member_card': 'danger'
      }
      return tagMap[type] || ''
    },
    getPayMethodName(method) {
      const methodMap = {
        'wechat': '微信支付',
        'alipay': '支付宝',
        'cash': '现金',
        'card': '刷卡'
      }
      return methodMap[method] || method
    }
  }
}
</script>

<style scoped>
.finance-ledger-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  color: #333;
}
.search-area {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.statistics-cards {
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
}
.stat-content {
  padding: 10px 0;
}
.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}
.table-card {
  margin-bottom: 20px;
}
.amount {
  font-weight: bold;
  color: #67c23a;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
