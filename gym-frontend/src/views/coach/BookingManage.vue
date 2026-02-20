<template>
  <div class="booking-manage-container">
    <div class="page-header-section">
      <div class="header-content">
        <div class="page-title">
          <i class="el-icon-s-management"></i>
          <h1>教练课表管理</h1>
          <span class="page-subtitle">管理我负责的课程预约、核销及考勤</span>
        </div>
        <div class="header-actions">
          <el-button type="primary" icon="el-icon-refresh" @click="refresh" :loading="loading">刷新</el-button>
        </div>
      </div>

      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索会员姓名/手机号/课程..."
          clearable
          size="small"
          class="filter-item"
        />
        <el-select v-model="statusFilter" clearable placeholder="预约状态" size="small" class="filter-item" style="width: 160px;">
          <el-option label="待审批" value="0" />
          <el-option label="待核销" value="1" />
          <el-option label="已核销" value="6" />
          <el-option label="已爽约" value="7" />
          <el-option label="已驳回" value="2" />
        </el-select>
      </div>
    </div>

    <div class="table-container">
      <el-table
        :data="filteredData"
        stripe
        style="width: 100%"
        :header-cell-style="{ background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)', color: '#fff', border: 'none' }"
        class="modern-table"
      >
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="bookingNo" label="单号" width="90" />
        <el-table-column prop="memberName" label="会员姓名" width="120" />
        <el-table-column prop="memberPhone" label="手机号" width="130" />
        <el-table-column label="课程/教练" min-width="160">
          <template slot-scope="scope">
            <div class="course-cell">
              <div class="course-name">{{ scope.row.courseName }}</div>
              <div class="coach-name" v-if="scope.row.employeeNameCoach">
                <el-tag size="mini" type="info">教练: {{ scope.row.employeeNameCoach }}</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="venueName" label="场地" min-width="140" />
        <el-table-column prop="bookingDate" label="日期" width="110" />
        <el-table-column label="预约时段" width="160">
          <template slot-scope="scope">
            <span v-if="scope.row.timeSlot" style="font-weight: 600;">{{ scope.row.timeSlot }}</span>
            <span v-else>{{ formatTime(scope.row.startTime) }} - {{ formatTime(scope.row.endTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <!-- 待审批状态 (0) -->
            <div v-if="String(scope.row.status) === '0'">
              <el-button
                type="success"
                size="mini"
                icon="el-icon-check"
                @click="handleApprove(scope.row)"
              >通过</el-button>
              <el-button
                type="danger"
                size="mini"
                icon="el-icon-close"
                @click="openRejectDialog(scope.row)"
              >驳回</el-button>
            </div>
            
            <!-- 已通过/待核销状态 (1) -->
            <div v-else-if="String(scope.row.status) === '1'">
              <el-button
                type="primary"
                size="mini"
                icon="el-icon-finished"
                @click="handleFinish(scope.row)"
              >核销完成</el-button>
              <el-button
                type="warning"
                size="mini"
                @click="handleNoShow(scope.row)"
              >记爽约</el-button>
            </div>
            
            <span v-else class="text-muted">已结束</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 驳回原因弹窗 -->
    <el-dialog title="驳回预约申请" :visible.sync="rejectDialogVisible" width="400px">
      <el-form label-position="top">
        <el-form-item label="驳回原因" required>
          <el-input
            type="textarea"
            v-model="rejectReason"
            placeholder="请输入驳回具体原因..."
            :rows="3"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="rejectDialogVisible = false">取消</el-button>
        <el-button size="small" type="primary" :loading="submitLoading" @click="handleReject">确认驳回</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getAllBookings, 
  coachApproveBooking, 
  coachRejectBooking, 
  coachFinishBooking, 
  coachMarkNoShow 
} from "@/api/allApi";

export default {
  name: "CoachBookingManage",
  data() {
    return {
      coachInfo: {},
      loading: false,
      submitLoading: false,
      tableData: [],
      keyword: "",
      statusFilter: "",
      rejectDialogVisible: false,
      rejectReason: "",
      currentBooking: null,
    };
  },
  computed: {
    isAdmin() {
      return this.coachInfo.adminAccount === 'admin';
    },
    filteredData() {
      const kw = String(this.keyword || "").trim().toLowerCase();
      return (this.tableData || []).filter((r) => {
        // 如果是管理员，则看到全部；如果是教练，只看自己的 employeeNo
        const isMyCourse = this.isAdmin || String(r.employeeNo) === String(this.coachInfo.employeeNo);
        const statusOk = this.statusFilter === "" || String(r.status) === String(this.statusFilter);
        if (!isMyCourse) return false;
        if (!kw) return statusOk;
        const hay = [r.memberName, r.memberPhone, r.courseName, r.employeeNameCoach, r.bookingNo]
          .filter(Boolean).join(" ").toLowerCase();
        return statusOk && hay.includes(kw);
      });
    },
  },
  created() {
    this.coachInfo = JSON.parse(window.localStorage.getItem("access-admin")) || {};
    // 管理员登录时不提示缺少教练信息，仅对教练账号做此检查
    if (!this.isAdmin && !this.coachInfo.employeeNo) {
      this.$message.error("未获取到教练信息，请重新登录");
    }
  },
  mounted() {
    this.refresh();
  },
  methods: {
    async refresh() {
      this.loading = true;
      try {
        // 为了展示核销功能，我们需要获取所有状态的记录，而不只是 PENDING
        // 这里暂时复用 getAllBookings，靠 filteredData 做 employeeNo 过滤
        const res = await getAllBookings({});
        this.tableData = res?.data || [];
      } catch (e) {
        this.$message.error("获取记录失败");
      } finally {
        this.loading = false;
      }
    },
    async handleApprove(row) {
      try {
        const res = await coachApproveBooking({
          bookingNo: row.bookingNo,
          employeeNo: row.employeeNo // 使用记录本身的 employeeNo
        });
        if (res?.data?.code === 200) {
          this.$message.success("审批通过");
          this.refresh();
        } else {
          this.$message.error(res?.data?.message || "操作失败");
        }
      } catch (e) {
        this.$message.error("服务器异常");
      }
    },
    openRejectDialog(row) {
      this.currentBooking = row;
      this.rejectReason = "";
      this.rejectDialogVisible = true;
    },
    async handleReject() {
      if (!this.rejectReason.trim()) return this.$message.warning("请输入原因");
      this.submitLoading = true;
      try {
        const res = await coachRejectBooking({
          bookingNo: this.currentBooking.bookingNo,
          employeeNo: this.currentBooking.employeeNo, // 使用记录本身的 employeeNo
          reason: this.rejectReason
        });
        if (res?.data?.code === 200) {
          this.$message.success("已驳回");
          this.rejectDialogVisible = false;
          this.refresh();
        }
      } finally {
        this.submitLoading = false;
      }
    },
    async handleFinish(row) {
      this.$confirm('确认该会员已到店并完成课程？', '提示', { type: 'success' }).then(async () => {
        const res = await coachFinishBooking({
          bookingNo: row.bookingNo,
          employeeNo: row.employeeNo // 使用记录本身的 employeeNo
        });
        if (res?.data?.code === 200) {
          this.$message.success("核销成功");
          this.refresh();
        }
      }).catch(() => {});
    },
    async handleNoShow(row) {
      this.$confirm('确认将会员标记为爽约？', '警告', { type: 'warning' }).then(async () => {
        const res = await coachMarkNoShow({
          bookingNo: row.bookingNo,
          employeeNo: row.employeeNo // 使用记录本身的 employeeNo
        });
        if (res?.data?.code === 200) {
          this.$message.warning("已标记爽约");
          this.refresh();
        }
      }).catch(() => {});
    },
    statusText(status) {
      const s = String(status);
      const dict = { "0": "待审批", "1": "待核销", "2": "已驳回", "3": "会员撤回", "4": "管理取消", "5": "超时", "6": "已核销", "7": "已爽约" };
      return dict[s] || "未知";
    },
    statusTagType(status) {
      const s = String(status);
      const dict = { "0": "warning", "1": "primary", "6": "success", "7": "danger", "2": "info" };
      return dict[s] || "info";
    },
    formatTime(dt) {
      if (!dt) return "-";
      if (Array.isArray(dt) && dt.length >= 5) {
        return `${String(dt[3]).padStart(2, '0')}:${String(dt[4]).padStart(2, '0')}`;
      }
      const m = String(dt).match(/(\d{2}:\d{2})/);
      return m ? m[1] : dt;
    }
  }
};
</script>

<style scoped>
.booking-manage-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.page-header-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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
  background: linear-gradient(135deg, #667eea, #764ba2);
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

.header-actions :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.header-actions :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  width: 280px;
}

.filter-bar :deep(.el-input__inner) {
  border-radius: 25px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.filter-bar :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.table-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 24px;
}

.modern-table :deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

.modern-table :deep(.el-table th) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  font-weight: 600;
  border: none !important;
  padding: 16px 12px;
}

.modern-table :deep(.el-table td) {
  border: none !important;
  padding: 16px 12px;
}

.modern-table :deep(.el-table--striped .el-table__row--striped td) {
  background: rgba(102, 126, 234, 0.02) !important;
}

.modern-table :deep(.el-table tr:hover td) {
  background: rgba(102, 126, 234, 0.05) !important;
}

.course-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.course-name {
  font-weight: 600;
  color: #333;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-item {
    width: 100%;
  }
}
</style>
