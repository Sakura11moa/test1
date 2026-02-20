<template>
  <section class="booking-manage a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-date"></i>
          预约管理
        </h2>
        <p class="page-subtitle">全量查看会员课程/场地预约情况，支持审批与驳回</p>
      </div>

      <div class="action-area">
        <el-button
          type="primary"
          icon="el-icon-refresh"
          size="small"
          class="primary-btn"
          @click="refresh"
          :loading="loading"
        >
          刷新记录
        </el-button>
      </div>
    </header>

    <div class="filter-card">
      <el-input
        v-model="keyword"
        placeholder="搜索会员姓名/手机号/课程/场地/单号..."
        clearable
        size="small"
        class="filter-item filter-keyword"
      />

      <el-select v-model="statusFilter" clearable placeholder="状态" size="small" class="filter-item" style="width: 170px;">
        <el-option label="审批中" value="0" />
        <el-option label="预约成功" value="1" />
        <el-option label="已驳回" value="2" />
        <el-option label="会员撤回" value="3" />
        <el-option label="管理取消" value="4" />
        <el-option label="审批超时" value="5" />
        <el-option label="已核销" value="6" />
        <el-option label="已爽约" value="7" />
      </el-select>

      <el-date-picker
        v-model="dateFilter"
        type="date"
        value-format="yyyy-MM-dd"
        placeholder="预约日期"
        size="small"
        class="filter-item"
        style="width: 170px;"
        placement="bottom-start"
      />
    </div>

    <div class="table-card">
      <el-table
        :data="filteredData"
        stripe
        size="mini"
        style="width: 100%"
        class="modern-table"
      >
        <el-table-column type="index" label="序号" width="55" align="center" />

        <el-table-column prop="bookingNo" label="预约单号" width="100" align="center" />

        <el-table-column prop="memberName" label="会员姓名" width="120" />

        <el-table-column prop="memberPhone" label="手机号" width="130" align="center" />

        <el-table-column prop="courseName" label="预约课程" min-width="150" />

        <el-table-column label="教练" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.employeeNameCoach" size="mini" effect="plain" type="primary">
              {{ scope.row.employeeNameCoach }}
            </el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="venueName" label="场地" width="120" />

        <el-table-column prop="venueLocation" label="位置" min-width="150">
          <template slot-scope="scope">
            <div class="ellipsis">
              <i class="el-icon-location-outline"></i> {{ scope.row.venueLocation }}
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="bookingDate" label="预约日期" width="110" align="center" />

        <el-table-column label="预约时段" width="140" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.timeSlot" class="text-main">{{ scope.row.timeSlot }}</span>
            <span v-else class="text-main">{{ formatTime(scope.row.startTime) }} - {{ formatTime(scope.row.endTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="110" align="center">
          <template slot-scope="scope">
            <el-tooltip
              v-if="String(scope.row.status) === '2'"
              effect="dark"
              :content="'驳回原因: ' + (scope.row.rejectReason || '未填写')"
              placement="top"
            >
              <el-tag :type="statusTagType(scope.row.status)" size="mini" effect="dark">
                {{ statusText(scope.row.status) }}
              </el-tag>
            </el-tooltip>
            <el-tag v-else :type="statusTagType(scope.row.status)" size="mini" effect="dark">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="auditTime" label="处理时间" width="155" align="center">
          <template slot-scope="scope">
            <span class="text-dim" style="font-size: 12px">{{ scope.row.auditTime || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template slot-scope="scope">
            <div v-if="String(scope.row.status) === '0'" class="op-buttons">
              <el-button
                @click="handleApprove(scope.row, $event)"
                type="success"
                size="mini"
                icon="el-icon-check"
                circle
                plain
                :loading="approveLoadingBookingNo === scope.row.bookingNo"
              ></el-button>
              <el-button
                @click="openRejectDialog(scope.row)"
                type="danger"
                size="mini"
                icon="el-icon-close"
                circle
                plain
                :loading="rejectLoadingBookingNo === scope.row.bookingNo"
              ></el-button>
            </div>
            <span v-else class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="驳回预约申请" :visible.sync="rejectDialogVisible" width="420px" append-to-body>
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
  </section>
</template>

<script>
import { getAllBookings, approveBooking, rejectBooking } from "@/api/allApi";

export default {
  name: "BookingManage",
  data() {
    return {
      adminInfo: {},
      loading: false,
      submitLoading: false,
      tableData: [],
      approveLoadingBookingNo: null,
      rejectLoadingBookingNo: null,
      keyword: "",
      statusFilter: "",
      dateFilter: "",
      rejectDialogVisible: false,
      rejectReason: "",
      currentBooking: null,
    };
  },
  computed: {
    filteredData() {
      const kw = String(this.keyword || "").trim().toLowerCase();
      return (this.tableData || []).filter((r) => {
        const statusOk = this.statusFilter === "" || String(r.status) === String(this.statusFilter);
        const dateOk = !this.dateFilter || String(r.bookingDate || "") === String(this.dateFilter);
        if (!kw) return statusOk && dateOk;
        const hay = [
          r.memberName,
          r.memberPhone,
          r.courseName,
          r.venueName,
          r.venueLocation,
          r.bookingNo,
        ]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();
        return statusOk && dateOk && hay.includes(kw);
      });
    },
  },
  created() {
    this.adminInfo = JSON.parse(window.localStorage.getItem("access-admin")) || {};
  },
  mounted() {
    this.refresh();
  },
  methods: {
    async refresh() {
      this.loading = true;
      try {
        const res = await getAllBookings({});
        this.tableData = res?.data || [];
      } catch (e) {
        console.log(e?.message || e);
        this.$message.error("获取预约记录失败");
      } finally {
        this.loading = false;
      }
    },
    async handleApprove(row, evt) {
      if (evt && evt.stopPropagation) evt.stopPropagation();

      const bookingNo = row?.bookingNo;
      if (!bookingNo) {
        this.$message.error("缺少 bookingNo，无法审批");
        return;
      }

      if (this.approveLoadingBookingNo === bookingNo || this.rejectLoadingBookingNo === bookingNo) {
        return;
      }

      this.approveLoadingBookingNo = bookingNo;
      try {
        const res = await approveBooking({
          bookingNo: parseInt(bookingNo, 10),
          adminId: this.adminInfo?.adminNo || 1,
        });

        if (res?.data?.code === 200) {
          this.$message.success(res.data?.message || "审批通过成功");
          await this.refresh();
        } else {
          this.$message.error(res?.data?.message || "审批失败");
        }
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message;
        this.$message.error(msg || "审批失败");
      } finally {
        this.approveLoadingBookingNo = null;
      }
    },
    openRejectDialog(row) {
      this.currentBooking = row;
      this.rejectReason = "";
      this.rejectDialogVisible = true;
    },
    async handleReject() {
      if (!this.rejectReason.trim()) {
        this.$message.warning("请输入驳回原因");
        return;
      }

      const bookingNo = this.currentBooking?.bookingNo;
      if (!bookingNo) {
        this.$message.error("缺少 bookingNo，无法驳回");
        return;
      }

      if (this.approveLoadingBookingNo === bookingNo || this.rejectLoadingBookingNo === bookingNo) {
        return;
      }

      this.rejectLoadingBookingNo = bookingNo;
      this.submitLoading = true;
      try {
        const res = await rejectBooking({
          bookingNo: parseInt(bookingNo, 10),
          adminId: this.adminInfo?.adminNo || 1,
          reason: this.rejectReason,
        });

        if (res?.data?.code === 200) {
          this.$message.success(res.data?.message || "已驳回");
          this.rejectDialogVisible = false;
          await this.refresh();
        } else {
          this.$message.error(res?.data?.message || "驳回操作失败");
        }
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message;
        this.$message.error(msg || "驳回操作失败");
      } finally {
        this.submitLoading = false;
        this.rejectLoadingBookingNo = null;
      }
    },
    statusText(status) {
      const s = String(status);
      if (s === "0") return "审批中";
      if (s === "1") return "预约成功";
      if (s === "2") return "已驳回";
      if (s === "3") return "会员撤回";
      if (s === "4") return "管理取消";
      if (s === "5") return "审批超时";
      if (s === "6") return "已核销";
      if (s === "7") return "已爽约";
      return "未知";
    },
    statusTagType(status) {
      const s = String(status);
      if (s === "0") return "warning";
      if (s === "1") return "success";
      if (s === "2") return "danger";
      if (s === "3") return "info";
      if (s === "4") return "info";
      if (s === "5") return "warning";
      if (s === "6") return "success";
      if (s === "7") return "danger";
      return "info";
    },
    formatTime(dt) {
      if (!dt) return "-";

      if (typeof dt === "string") {
        const match = dt.match(/(\d{2}:\d{2})/);
        if (match) return match[1];
      }

      if (Array.isArray(dt) && dt.length >= 5) {
        const hh = String(dt[3]).padStart(2, "0");
        const mm = String(dt[4]).padStart(2, "0");
        return `${hh}:${mm}`;
      }

      return String(dt);
    },
  },
};
</script>

<style scoped>
.booking-manage {
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
  font-weight: 700;
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
  flex: 1;
}

.primary-btn {
  border-radius: 8px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.filter-card {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 14px;
  margin-bottom: 18px;
  border: 1px solid var(--admin-border);
  border-radius: 14px;
  background: rgba(37, 99, 235, 0.03);
}

.filter-item {
  width: 280px;
}

.filter-keyword {
  flex: 1;
  min-width: 260px;
}

.filter-card :deep(.el-input__inner) {
  border-radius: 20px;
}

.table-card {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 4px;
}

.modern-table :deep(.el-table__header th) {
  background: rgba(37, 99, 235, 0.05) !important;
  color: var(--admin-primary) !important;
  font-weight: 700;
  height: 50px;
}

.modern-table :deep(.el-table__row) {
  height: 58px;
}

.booking-cell,
.member-cell,
.course-cell,
.venue-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.booking-no,
.member-name,
.course-name,
.venue-name {
  font-weight: 700;
  color: var(--admin-text-main);
}

.booking-date,
.member-phone,
.venue-loc,
.coach-name {
  font-size: 12px;
  color: var(--admin-text-dim);
  display: flex;
  align-items: center;
  gap: 6px;
}

.booking-date i,
.member-phone i,
.venue-loc i {
  color: var(--admin-primary);
}

.sep {
  color: var(--admin-text-muted);
}

.audit-time {
  color: var(--admin-text-dim);
  font-weight: 600;
}

.op-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.text-muted {
  color: var(--admin-text-muted);
  font-size: 12px;
}

/* 弹窗样式统一 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: var(--admin-gradient);
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: white !important;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white !important;
}

@media (max-width: 1024px) {
  .filter-item {
    width: 100%;
  }
}
</style>
