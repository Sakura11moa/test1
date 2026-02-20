<template>
  <div class="my-finance-container">
    <!-- 页面头部 -->
    <div class="page-header-section">
      <div class="header-content">
        <div class="page-title">
          <i class="el-icon-wallet"></i>
          <h1>我的财务概览</h1>
          <span class="page-subtitle">查看预收账款与已实现收入</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-balance">
            <div class="stat-icon">
              <i class="el-icon-money"></i>
            </div>
            <div class="stat-info">
              <span class="stat-label">储值余额</span>
              <span class="stat-value">¥{{ (overview.balance || 0).toFixed(2) }}</span>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-deferred">
            <div class="stat-icon">
              <i class="el-icon-collection-tag"></i>
            </div>
            <div class="stat-info">
              <span class="stat-label">预收账款总额</span>
              <span class="stat-value">¥{{ (overview.totalDeferred || 0).toFixed(2) }}</span>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-realized">
            <div class="stat-icon">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-info">
              <span class="stat-label">已实现收入</span>
              <span class="stat-value">¥{{ (overview.totalRecognized || 0).toFixed(2) }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 预收账款详情 -->
    <div class="detail-section">
      <div class="section-header">
        <h3><i class="el-icon-tickets"></i> 预收账款明细</h3>
        <p>您已预付但尚未确认收入的款项</p>
      </div>

      <el-table
        :data="deferredList"
        stripe
        style="width: 100%"
        v-loading="loading"
        class="modern-table"
      >
        <el-table-column label="来源类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag
              :type="getSourceTypeTag(scope.row.sourceType)"
              size="small"
              effect="light"
            >
              {{ getSourceTypeText(scope.row.sourceType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="单号" width="120" align="center">
          <template slot-scope="scope">
            <span class="biz-no">{{ scope.row.sourceNo }}</span>
          </template>
        </el-table-column>

        <el-table-column label="原始金额" width="120" align="right">
          <template slot-scope="scope">
            <span class="amount">¥{{ scope.row.sourceAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="已确认" width="120" align="right">
          <template slot-scope="scope">
            <span class="amount recognized">¥{{ scope.row.recognizedAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="待确认" width="120" align="right">
          <template slot-scope="scope">
            <span class="amount deferred">
              ¥{{ (scope.row.deferredAmount - scope.row.recognizedAmount).toFixed(2) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="分摊进度" width="200" align="center">
          <template slot-scope="scope">
            <el-progress
              :percentage="getProgress(scope.row)"
              :status="getProgressStatus(scope.row)"
              :stroke-width="10"
            ></el-progress>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag
              :type="getStatusTag(scope.row.status)"
              size="small"
              effect="light"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="scope">
            <span class="time-text">{{ scope.row.createTime | timeFormat }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 收入确认记录 -->
    <div class="detail-section">
      <div class="section-header">
        <h3><i class="el-icon-circle-check"></i> 已实现收入记录</h3>
        <p>已完成确认的收入明细</p>
      </div>

      <el-table
        :data="recognitionList"
        stripe
        style="width: 100%"
        v-loading="recognitionLoading"
        class="modern-table"
      >
        <el-table-column label="确认方式" width="120" align="center">
          <template slot-scope="scope">
            <el-tag
              :type="getRecognitionTypeTag(scope.row.recognitionType)"
              size="small"
              effect="light"
            >
              {{ getRecognitionTypeText(scope.row.recognitionType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="确认金额" width="120" align="right">
          <template slot-scope="scope">
            <span class="amount income">+¥{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="期间/日期" width="120" align="center">
          <template slot-scope="scope">
            <span class="period-text">{{ scope.row.period }}</span>
          </template>
        </el-table-column>

        <el-table-column label="关联业务" width="150" align="center">
          <template slot-scope="scope">
            <span class="biz-no">{{ scope.row.relatedBizNo || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="备注" min-width="150">
          <template slot-scope="scope">
            <span class="remark-text">{{ scope.row.remark || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="确认时间" width="160" align="center">
          <template slot-scope="scope">
            <span class="time-text">{{ scope.row.createTime | timeFormat }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getMemberDeferredOverview, getMemberDeferredDetails, getMemberRevenueRecognitions, getMemberChange } from '@/api/allApi';

export default {
  name: 'MyFinance',
  data() {
    return {
      loading: false,
      recognitionLoading: false,
      memberNo: null,
      overview: {
        balance: 0,
        totalDeferred: 0,
        totalRecognized: 0
      },
      deferredList: [],
      recognitionList: []
    };
  },
  filters: {
    timeFormat(value) {
      if (!value) return '-';
      return new Date(value).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  },
  created() {
    const member = JSON.parse(localStorage.getItem('access-member') || '{}');
    this.memberNo = member.memberNo;
  },
  mounted() {
    if (this.memberNo) {
      this.loadOverview();
      this.loadDeferredList();
      this.loadRecognitionList();
    }
  },
  methods: {
    // 加载概览
    async loadOverview() {
      try {
        // 获取储值余额
        const balanceRes = await getMemberChange({ memberNo: this.memberNo });
        this.overview.balance = balanceRes.data || 0;

        // 获取递延收益概览
        const overviewRes = await getMemberDeferredOverview({ memberNo: this.memberNo });
        if (overviewRes.data && overviewRes.data.code === 200) {
          const data = overviewRes.data.data || {};
          this.overview.totalDeferred = data.totalDeferred || 0;
          this.overview.totalRecognized = data.totalRecognized || 0;
        }
      } catch (err) {
        console.error('获取概览失败:', err);
      }
    },

    // 加载预收账款列表
    async loadDeferredList() {
      this.loading = true;
      try {
        const res = await getMemberDeferredDetails({ memberNo: this.memberNo });
        if (res.data && res.data.code === 200) {
          this.deferredList = res.data.data || [];
        }
      } catch (err) {
        console.error('获取预收账款列表失败:', err);
      } finally {
        this.loading = false;
      }
    },

    // 加载收入确认列表
    async loadRecognitionList() {
      this.recognitionLoading = true;
      try {
        const res = await getMemberRevenueRecognitions({ memberNo: this.memberNo });
        if (res.data && res.data.code === 200) {
          this.recognitionList = res.data.data || [];
        }
      } catch (err) {
        console.error('获取收入确认列表失败:', err);
      } finally {
        this.recognitionLoading = false;
      }
    },

    // 计算分摊进度
    getProgress(row) {
      if (row.sourceAmount <= 0) return 0;
      return Math.round((row.recognizedAmount / row.sourceAmount) * 100);
    },

    // 分摊进度状态
    getProgressStatus(row) {
      const progress = this.getProgress(row);
      if (progress >= 100) return 'success';
      if (progress >= 50) return 'warning';
      return '';
    },

    // 来源类型标签
    getSourceTypeTag(type) {
      const tags = {
        'RECHARGE': 'success',
        'CARD_RENEW': 'primary',
        'PURCHASE': 'warning'
      };
      return tags[type] || 'info';
    },

    // 来源类型文本
    getSourceTypeText(type) {
      const texts = {
        'RECHARGE': '充值',
        'CARD_RENEW': '续卡',
        'PURCHASE': '购课'
      };
      return texts[type] || type;
    },

    // 状态标签
    getStatusTag(status) {
      const tags = {
        1: 'warning',  // 进行中
        2: 'success',  // 已完成
        3: 'danger'    // 已取消
      };
      return tags[status] || 'info';
    },

    // 状态文本
    getStatusText(status) {
      const texts = {
        1: '进行中',
        2: '已完成',
        3: '已取消'
      };
      return texts[status] || status;
    },

    // 确认方式标签
    getRecognitionTypeTag(type) {
      const tags = {
        'MONTHLY': 'primary',
        'COURSE_COMPLETE': 'success'
      };
      return tags[type] || 'info';
    },

    // 确认方式文本
    getRecognitionTypeText(type) {
      const texts = {
        'MONTHLY': '月卡分摊',
        'COURSE_COMPLETE': '课程核销'
      };
      return texts[type] || type;
    }
  }
};
</script>

<style scoped>
.my-finance-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 页面头部 */
.page-header-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.header-content {
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  background: var(--member-gradient, linear-gradient(135deg, #667eea 0%, #764ba2 100%));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 32px;
  color: #667eea;
}

.page-subtitle {
  color: #666;
  font-size: 14px;
  margin-left: 44px;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-balance .stat-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.stat-deferred .stat-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #f5a623 100%);
}

.stat-realized .stat-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #333;
}

/* 详情区域 */
.detail-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h3 i {
  color: #667eea;
}

.section-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
  margin-left: 28px;
}

/* 表格样式 */
.modern-table :deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

.modern-table :deep(.el-table th) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  font-weight: 600;
  border: none !important;
}

.modern-table :deep(.el-table td) {
  border: none !important;
  padding: 14px 12px;
}

.amount {
  font-weight: 600;
  font-family: monospace;
  color: #333;
}

.amount.recognized {
  color: #409eff;
}

.amount.deferred {
  color: #e6a23c;
}

.amount.income {
  color: #67c23a;
}

.biz-no {
  font-family: monospace;
  font-size: 12px;
  color: #909399;
}

.time-text {
  font-size: 13px;
  color: #666;
}

.period-text {
  font-weight: 500;
  color: #333;
}

.remark-text {
  font-size: 13px;
  color: #666;
}
</style>
