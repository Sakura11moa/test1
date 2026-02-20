<template>
  <section class="my-bookings m-card">
    <header class="page-title">
      <h2><i class="el-icon-tickets"></i> 我的预约记录</h2>
      <p class="subtitle">查看您的课程与场地预约状态，支持在线评价</p>
    </header>

    <div class="table-actions">
      <el-button
        type="primary"
        size="small"
        icon="el-icon-refresh"
        @click="refresh"
        :loading="loading"
        class="refresh-btn"
      >
        刷新记录
      </el-button>
    </div>

    <el-table
      :data="tableData"
      stripe
      style="width: 100%"
      class="record-table"
    >
      <el-table-column type="index" label="序号" width="55" align="center" />
      
      <el-table-column label="预约详情" min-width="200">
        <template slot-scope="scope">
          <div class="booking-info-cell">
            <div class="course-name">{{ scope.row.courseName }}</div>
            <div class="booking-no">单号: {{ scope.row.bookingNo }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="场地/位置" min-width="180">
        <template slot-scope="scope">
          <div class="venue-info-cell">
            <div class="venue-name">{{ scope.row.venueName }}</div>
            <div class="venue-loc"><i class="el-icon-location-outline"></i> {{ scope.row.venueLocation }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="预约时间" width="160">
        <template slot-scope="scope">
          <div class="time-info-cell">
            <div class="date">{{ scope.row.bookingDate }}</div>
            <div class="time">{{ scope.row.startTime }} - {{ scope.row.endTime }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tooltip
            v-if="scope.row.status == '2'"
            effect="dark"
            :content="'驳回原因: ' + (scope.row.rejectReason || '未填写')"
            placement="top"
          >
            <el-tag type="danger" size="small" effect="light">已驳回</el-tag>
          </el-tooltip>
          <el-tag v-else :type="getStatusType(scope.row.status)" size="small" effect="light">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="140" fixed="right" align="center">
        <template slot-scope="scope">
          <!-- 待审批状态可以撤回 -->
          <el-button
            v-if="scope.row.status == '0'"
            type="danger"
            size="mini"
            round
            plain
            @click="withdraw(scope.row)"
          >
            取消
          </el-button>

          <!-- 已核销状态可以去评价 -->
          <el-button
            v-else-if="scope.row.status == '6' && !scope.row.isEvaluated"
            type="primary"
            size="mini"
            round
            @click="openEvalDialog(scope.row)"
          >
            评价
          </el-button>

          <span v-else class="status-done">
            {{ scope.row.isEvaluated ? '已评价' : '-' }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 评价弹窗 -->
    <el-dialog title="课程评价" :visible.sync="evalDialogVisible" width="400px" append-to-body>
      <el-form label-position="top" size="small">
        <el-form-item label="课程评分">
          <el-rate v-model="evalForm.score" show-text></el-rate>
        </el-form-item>
        <el-form-item label="您的评语">
          <el-input type="textarea" v-model="evalForm.content" placeholder="请输入您的上课感受..." :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="evalDialogVisible = false">取消</el-button>
        <el-button size="small" type="primary" :loading="evalLoading" @click="submitEval">提交评价</el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
import { cancelPendingBooking, getMyVenueBookings, evaluateBooking } from "@/api/allApi";

export default {
  name: "MyVenueBookings",
  data() {
    return {
      admin: {},
      tableData: [],
      loading: false,
      withdrawingNo: null,
      
      // 评价相关
      evalDialogVisible: false,
      evalLoading: false,
      currentBooking: null,
      evalForm: {
        score: 5,
        content: ''
      }
    };
  },
  created() {
    this.admin = JSON.parse(window.localStorage.getItem("access-member")) || {};
  },
  mounted() {
    this.refresh();
  },
  methods: {
    goBack() {
      this.$router.back();
    },
    getStatusText(status) {
      const s = String(status);
      const map = {
        '0': '审批中',
        '1': '预约成功',
        '2': '已驳回',
        '3': '会员撤回',
        '4': '管理取消',
        '5': '审批超时',
        '6': '已核销',
        '7': '已爽约',
      };
      return map[s] || '未知';
    },
    getStatusType(status) {
      const s = String(status);
      const map = {
        '0': 'warning',
        '1': 'success',
        '2': 'danger',
        '3': 'info',
        '4': 'info',
        '5': 'warning',
        '6': 'success',
        '7': 'danger',
      };
      return map[s] || 'info';
    },
    async refresh() {
      if (!this.admin || !this.admin.memberNo) {
        this.$message.error("未登录或会员信息缺失");
        return;
      }
      this.loading = true;
      try {
        const res = await getMyVenueBookings({ memberNo: this.admin.memberNo });
        this.tableData = res.data || [];
      } catch (e) {
        this.$message.error("获取预约记录失败");
      } finally {
        this.loading = false;
      }
    },
    openEvalDialog(row) {
      this.currentBooking = row;
      this.evalForm.score = 5;
      this.evalForm.content = "";
      this.evalDialogVisible = true;
    },
    async submitEval() {
      if (!this.evalForm.score) return this.$message.warning("请先评分");
      this.evalLoading = true;
      try {
        const res = await evaluateBooking({
          bookingNo: this.currentBooking.bookingNo,
          memberNo: this.admin.memberNo,
          courseNo: this.currentBooking.courseNo,
          score: this.evalForm.score,
          content: this.evalForm.content
        });
        if (res.data && res.data.code === 200) {
          this.$message.success("评价成功");
          this.evalDialogVisible = false;
          this.refresh();
        } else {
          this.$message.error(res.data?.message || "评价失败");
        }
      } finally {
        this.evalLoading = false;
      }
    },
    withdraw(row) {
      if (!row) return;
      const bNo = row.bookingNo || row.booking_no;
      if (!bNo) {
        this.$message.error("无法获取预约单号");
        return;
      }

      this.$confirm("确认取消该预约申请吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          this.withdrawingNo = bNo;
          try {
            const res = await cancelPendingBooking({
              bookingNo: bNo,
              memberNo: this.admin.memberNo,
            });
            if (res.data && res.data.code === 200) {
              this.$message.success("取消成功，申请已撤回");
              await this.refresh();
            } else {
              this.$message.error(res.data?.message || "取消失败");
            }
          } catch (e) {
            console.log(e?.message || e);
            this.$message.error("网络错误，请稍后再试");
          } finally {
            this.withdrawingNo = null;
          }
        })
        .catch(() => {});
    },
  },
};
</script>

<style scoped>
.my-bookings {
  width: 100%;
  box-sizing: border-box;
}

.page-title {
  margin-bottom: 24px;
}

.page-title h2 {
  margin: 0 0 8px 0;
  font-weight: 700;
  font-size: 24px;
  background: var(--member-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}

.subtitle {
  margin: 0;
  color: var(--member-text-dim);
  font-size: 14px;
}

.table-actions {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.refresh-btn {
  border-radius: 8px !important;
}

.record-table {
  background: transparent !important;
}

.booking-info-cell, .venue-info-cell, .time-info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px 0;
}

.course-name, .venue-name {
  font-weight: 600;
  color: var(--member-text-main);
  font-size: 14px;
}

.booking-no, .venue-loc, .date, .time {
  font-size: 12px;
  color: var(--member-text-dim);
}

.venue-loc i {
  color: var(--member-primary);
}

.status-done {
  color: var(--member-text-muted);
  font-size: 12px;
}

/* 弹窗样式 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: var(--member-gradient);
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: white !important;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white !important;
}

:deep(.el-rate) {
  margin-top: 8px;
}
</style>
