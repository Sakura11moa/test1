<template>
  <section class="course-booking m-card">
    <header class="page-header-v2">
      <div class="title-area">
        <h2 class="page-title-main">
          <span class="icon-wrap"><i class="el-icon-date"></i></span>
          课程预约
        </h2>
        <p class="page-subtitle-v2">选择心仪课程与预约时段，开启高效健身</p>
      </div>
      <div class="header-actions">
        <el-button size="medium" @click="refresh" :loading="loading" icon="el-icon-refresh" circle class="action-btn"></el-button>
        <el-button size="medium" type="primary" class="my-bookings-btn" @click="toMyBookings">
          <i class="el-icon-tickets"></i> 我的预约记录
        </el-button>
      </div>
    </header>

    <div class="booking-config-card">
      <div class="config-grid-v2">
        <div class="config-item-v2">
          <div class="item-label">
            <i class="el-icon-collection"></i> <span>选择课程</span>
          </div>
          <el-select
            v-model="selectedCourseNo"
            placeholder="请先选择要预约的课程"
            filterable
            class="modern-select"
            :loading="courseLoading"
            @change="onCourseChange"
          >
            <el-option
              v-for="c in courseList"
              :key="c.courseNo"
              :label="formatCourseLabel(c)"
              :value="c.courseNo"
            />
          </el-select>
        </div>

        <div class="config-item-v2">
          <div class="item-label">
            <i class="el-icon-calendar"></i> <span>预约日期</span>
          </div>
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="点击选择日期"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            class="modern-date-picker"
            @change="onDateChange"
          />
        </div>
      </div>

      <div class="selection-empty-tip" v-if="!selectedCourseNo || !selectedDate">
        <div class="tip-inner">
          <i class="el-icon-magic-stick"></i>
          <span>请完成上方配置，系统将为您匹配最佳场地与时段</span>
        </div>
      </div>
    </div>

    <div class="venue-select-section" v-if="selectedCourseNo && selectedDate">
      <div class="config-item">
        <label class="config-label">选择场地</label>
        <el-select
          v-model="selectedVenueNo"
          placeholder="请选择场地"
          filterable
          class="config-select venue-select"
          :loading="venueLoading"
          @change="onVenueChange"
        >
          <el-option
            v-for="v in venueList"
            :key="v.venueNo"
            :label="formatVenueLabel(v)"
            :value="v.venueNo"
          />
        </el-select>
      </div>

      <div v-if="selectedVenueNo" class="time-options-section">
        <div class="section-divider section-divider-strong">
          <span>选择预约时段</span>
        </div>
        
        <div v-if="startTimesLoading" class="loading-box">
          <i class="el-icon-loading"></i> 正在获取实时排期...
        </div>

        <div v-else>
          <!-- 推荐时段区：精修为悬浮感卡片 -->
          <div v-if="recommendOptions && recommendOptions.length > 0" class="recommend-section">
            <div class="section-title">
              <i class="el-icon-star-on" style="color: #FF9F43"></i> 
              <span>热门推荐</span>
              <span class="title-tip">黄金时段 建议提前预约</span>
            </div>
            <div class="recommend-cards">
              <div 
                v-for="(item, index) in recommendOptions" 
                :key="index"
                :class="['recommend-card', { active: selectedStartTime === item.startTime }]"
                @click="selectedStartTime = item.startTime"
                tabindex="0"
                @keyup.enter="selectedStartTime = item.startTime"
              >
                <div class="recommend-badge" v-if="index === 0">
                  <i class="el-icon-medal-1"></i> 最佳
                </div>
                <div class="card-glow"></div>
                <div class="card-time">{{ item.startTime }}</div>
                <div class="card-reason">{{ item.reason }}</div>
              </div>
            </div>
          </div>

          <!-- 所有可选时段：增加分组逻辑隔离与精致胶囊 -->
          <div class="available-section">
            <div class="section-title">
              <i class="el-icon-time"></i>
              <span>全量时段</span>
            </div>
            
            <div class="time-group-container">
              <!-- 上午时段 -->
              <div class="time-group-card" v-if="morningSlots.length > 0">
                <div class="group-header">
                  <i class="el-icon-sunny"></i>
                  <span class="group-name">上午</span>
                  <span class="group-desc">06:00 - 12:00</span>
                </div>
                <div class="time-grid">
                  <div
                    v-for="t in morningSlots"
                    :key="t"
                    :class="['time-capsule', { active: selectedStartTime === t }]"
                    @click="selectedStartTime = t"
                    tabindex="0"
                    @keyup.enter="selectedStartTime = t"
                  >
                    {{ t }}
                  </div>
                </div>
              </div>

              <!-- 下午时段 -->
              <div class="time-group-card" v-if="afternoonSlots.length > 0">
                <div class="group-header">
                  <i class="el-icon-cloudy-and-sunny"></i>
                  <span class="group-name">下午</span>
                  <span class="group-desc">12:00 - 18:00</span>
                </div>
                <div class="time-grid">
                  <div
                    v-for="t in afternoonSlots"
                    :key="t"
                    :class="['time-capsule', { active: selectedStartTime === t }]"
                    @click="selectedStartTime = t"
                    tabindex="0"
                    @keyup.enter="selectedStartTime = t"
                  >
                    {{ t }}
                  </div>
                </div>
              </div>

              <!-- 晚上时段 -->
              <div class="time-group-card" v-if="eveningSlots.length > 0">
                <div class="group-header">
                  <i class="el-icon-moon"></i>
                  <span class="group-name">晚上</span>
                  <span class="group-desc">18:00 - 23:00</span>
                </div>
                <div class="time-grid">
                  <div
                    v-for="t in eveningSlots"
                    :key="t"
                    :class="['time-capsule', { active: selectedStartTime === t }]"
                    @click="selectedStartTime = t"
                    tabindex="0"
                    @keyup.enter="selectedStartTime = t"
                  >
                    {{ t }}
                  </div>
                </div>
              </div>

              <div v-if="availableOptions.length === 0" class="no-data-v2">
                <i class="el-icon-document-delete"></i>
                <p>该日期暂无可预约时段，请尝试其他日期</p>
              </div>
            </div>
          </div>

          <div class="submit-section">
            <el-button
              type="primary"
              class="primary-btn submit-btn"
              :loading="bookingLoading"
              :disabled="!canBook"
              @click="submitBooking"
            >
              确认预约 {{ selectedStartTime || '' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="venue-list-section">
      <div class="section-divider section-divider-strong">
        <span>可用场地概览</span>
      </div>

      <el-table :data="venueList" stripe class="modern-table">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="venueName" label="场地名称" min-width="120" />
        <el-table-column label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" effect="plain" type="info">
              <template v-if="scope.row.venueType == '1'">健身房</template>
              <template v-else-if="scope.row.venueType == '2'">瑜伽室</template>
              <template v-else-if="scope.row.venueType == '3'">游泳池</template>
              <template v-else-if="scope.row.venueType == '4'">篮球场</template>
              <template v-else-if="scope.row.venueType == '5'">羽毛球场</template>
              <template v-else>其他</template>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="venueLocation" label="场地位置" min-width="150" />
        <el-table-column prop="venueCapacity" label="容量" width="80" align="center" />
        <el-table-column prop="openTime" label="开放时间" width="120" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.venueState == '1'" type="success" size="small">可用</el-tag>
            <el-tag v-else-if="scope.row.venueState == '2'" type="warning" size="small">维护</el-tag>
            <el-tag v-else type="danger" size="small">关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              plain
              class="select-venue-btn"
              :disabled="scope.row.venueState == '2' || scope.row.venueState == '4'"
              @click="selectVenue(scope.row)"
            >
              选此场地
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<script>
import {
  bookVenueV3,
  getAllVenue,
  getTimeOptions,
  getPurchasedCourses,
} from "@/api/allApi";

export default {
  name: "CourseBooking",
  data() {
    return {
      admin: {},

      courseLoading: false,
      courseList: [],
      selectedCourseNo: null,

      selectedDate: "",
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        },
      },

      venueLoading: false,
      venueList: [],
      selectedVenueNo: null,

      startTimesLoading: false,
      availableStartTimes: [],
      selectedStartTime: "",

      timeOptions: null,
      recommendOptions: [],
      availableOptions: [],

      loading: false,
      bookingLoading: false,
    };
  },
  computed: {
    selectedCourse() {
      return this.courseList.find((c) => c.courseNo === this.selectedCourseNo) || null;
    },
    canBook() {
      return (
        !!this.selectedCourseNo &&
        !!this.selectedDate &&
        !!this.selectedVenueNo &&
        !!this.selectedStartTime &&
        !this.bookingLoading
      );
    },
    morningSlots() {
      return (this.availableOptions || []).filter(t => t < '12:00');
    },
    afternoonSlots() {
      return (this.availableOptions || []).filter(t => t >= '12:00' && t < '18:00');
    },
    eveningSlots() {
      return (this.availableOptions || []).filter(t => t >= '18:00');
    },
  },
  created() {
    this.admin = JSON.parse(localStorage.getItem("access-member")) || {};
  },
  mounted() {
    this.fetchPurchasedCourses();
    this.fetchVenues();
  },
  methods: {
    goBack() {
      this.$router.back();
    },
    toMyBookings() {
      this.$router.push("/memberLayout/myVenueBookings");
    },
    formatCourseLabel(c) {
      const price = c.discountPrice ?? c.coursePrice ?? "-";
      const remain = c.remainTimes ?? "-";
      const coach = c.employeeNameCoach ? ` [教练: ${c.employeeNameCoach}]` : "";
      return `${c.courseName || "未命名课程"}${coach}（余量:${remain} / ${price}元）`;
    },
    formatVenueLabel(v) {
      const loc = v.venueLocation || "-";
      return `${v.venueName || "未命名场地"}（${loc}）`;
    },

    async fetchPurchasedCourses() {
      this.courseLoading = true;
      try {
        const stored = JSON.parse(localStorage.getItem("access-member") || "{}");
        const memberNo = (this.admin && this.admin.memberNo) || stored.memberNo;
        if (!memberNo) {
          this.courseList = [];
          this.selectedCourseNo = null;
          this.$message.error("会员信息缺失，请重新登录");
          return;
        }

        const res = await getPurchasedCourses({ memberNo });
        const allPurchased = Array.isArray(res) ? res : res?.data ?? [];
        this.courseList = allPurchased.filter((c) => (c.remainTimes ?? 0) > 0);
        if (this.selectedCourseNo && !this.courseList.some((c) => c.courseNo === this.selectedCourseNo)) {
          this.selectedCourseNo = null;
        }
      } catch (e) {
        console.log(e?.message || e);
        this.courseList = [];
        this.selectedCourseNo = null;
        this.$message.error("获取已购课程列表失败");
      } finally {
        this.courseLoading = false;
      }
    },

    async fetchVenues() {
      this.venueLoading = true;
      try {
        const res = await getAllVenue({ page: 0, size: 100 });
        this.venueList = res?.data || [];
      } catch (e) {
        console.log(e?.message || e);
        this.venueList = [];
        this.$message.error("获取场地列表失败");
      } finally {
        this.venueLoading = false;
      }
    },

    refresh() {
      // 刷新按钮：重拉可用开始时间
      if (this.selectedVenueNo && this.selectedDate) {
        this.onVenueChange(this.selectedVenueNo);
      }
    },

    onCourseChange(courseNo) {
      this.selectedVenueNo = null;
      this.recommendOptions = [];
      this.availableOptions = [];
      this.selectedStartTime = "";
      
      const course = this.courseList.find(c => c.courseNo === courseNo);
      if (course && course.venueNo) {
        this.selectedVenueNo = course.venueNo;
        if (this.selectedDate) {
          this.fetchTimeOptions();
        }
      }
    },

    onDateChange() {
      this.selectedStartTime = "";
      this.recommendOptions = [];
      this.availableOptions = [];
      if (this.selectedCourseNo && this.selectedVenueNo) {
        this.fetchTimeOptions();
      }
    },

    onVenueChange() {
      this.selectedStartTime = "";
      this.recommendOptions = [];
      this.availableOptions = [];
      if (this.selectedCourseNo && this.selectedDate) {
        this.fetchTimeOptions();
      }
    },

    async fetchTimeOptions() {
      if (!this.selectedCourseNo || !this.selectedVenueNo || !this.selectedDate) return;

      this.startTimesLoading = true;
      try {
        const res = await getTimeOptions({
          memberNo: this.admin.memberNo,
          courseNo: this.selectedCourseNo,
          venueNo: this.selectedVenueNo,
          date: this.selectedDate
        });
        
        // 假设后端返回结构为 { recommend: [...], available: [...] }
        const data = res.data || res;
        this.recommendOptions = data.recommend || [];
        this.availableOptions = data.available || [];
      } catch (e) {
        console.error("获取时段失败:", e);
        this.$message.error("获取预约时段失败");
      } finally {
        this.startTimesLoading = false;
      }
    },

    selectVenue(v) {
      this.selectedVenueNo = v.venueNo;
      this.onVenueChange();
    },

    async submitBooking() {
      if (!this.canBook) return;
      if (!this.admin || !this.admin.memberNo) {
        this.$message.error("未登录或会员信息缺失");
        return;
      }

      this.bookingLoading = true;
      try {
        const res = await bookVenueV3({
          memberNo: this.admin.memberNo,
          courseNo: this.selectedCourseNo,
          venueNo: this.selectedVenueNo,
          bookingDate: this.selectedDate,
          startTime: this.selectedStartTime,
        });

        if (res.data && res.data.code === 200) {
          this.$message.success("预约申请已提交，请等待管理员审批");
          await this.fetchPurchasedCourses();
          this.selectedStartTime = "";
          await this.fetchTimeOptions();
        } else {
          this.$message.warning(res.data?.message || "预约失败");
        }
      } catch (e) {
        console.error("提交预约失败:", e);
        this.$message.error("预约失败，请稍后再试");
      } finally {
        this.bookingLoading = false;
      }
    },
  },
};
</script>

<style scoped>
.course-booking {
  padding: 0;
  width: 100%;
}

/* 顶部标题区精修 */
.page-header-v2 {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 22px 22px 12px;
}

.page-title-main {
  margin: 0;
  font-size: 26px;
  font-weight: 900;
  letter-spacing: -0.6px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--member-text-main);
}

.icon-wrap {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(102,126,234,0.18) 0%, rgba(118,75,162,0.14) 100%);
  border: 1px solid rgba(102,126,234,0.18);
}

.icon-wrap i {
  font-size: 18px;
  color: var(--member-primary);
}

.page-subtitle-v2 {
  margin: 10px 0 0;
  color: var(--member-text-dim);
  font-size: 14px;
  line-height: 1.6;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-btn {
  border-radius: 12px !important;
  box-shadow: 0 10px 22px rgba(102, 126, 234, 0.16);
}

.my-bookings-btn {
  border-radius: 14px !important;
  padding: 10px 14px !important;
  font-weight: 700 !important;
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.22);
}

.my-bookings-btn i {
  margin-right: 6px;
}

/* 选择区卡片化 */
.booking-config-card {
  margin: 0 22px 18px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(102, 126, 234, 0.12);
  box-shadow: 0 14px 30px rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(10px);
}

.config-grid-v2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.config-item-v2 {
  background: linear-gradient(180deg, rgba(102,126,234,0.06) 0%, rgba(255,255,255,0.9) 100%);
  border: 1px solid rgba(102,126,234,0.10);
  border-radius: 16px;
  padding: 14px;
}

.item-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 13px;
  font-weight: 800;
  color: var(--member-text-main);
}

.item-label i {
  color: var(--member-primary);
}

.modern-select,
.modern-date-picker {
  width: 100%;
}

.booking-config-card :deep(.el-input__inner) {
  border-radius: 14px !important;
  height: 42px;
  line-height: 42px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 1);
  transition: all 0.2s ease;
}

.booking-config-card :deep(.el-input__inner:focus) {
  border-color: rgba(102,126,234,0.6);
  box-shadow: 0 0 0 3px rgba(102,126,234,0.14);
}

.selection-empty-tip {
  margin-top: 14px;
}

.tip-inner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(255,159,67,0.10) 0%, rgba(102,126,234,0.08) 100%);
  border: 1px dashed rgba(102, 126, 234, 0.22);
  color: var(--member-text-dim);
  font-size: 13px;
}

.tip-inner i {
  font-size: 18px;
  color: #ff9f43;
}

/* 分隔标题（选择预约时段 / 可用场地概览）加深颜色 */
.section-divider-strong {
  margin: 18px 0 14px;
}

.section-divider-strong span {
  color: rgba(17, 24, 39, 0.92);
  font-weight: 900;
  letter-spacing: 0.2px;
}

/* 选择场地 label 加深颜色 */
.config-label {
  color: rgba(17, 24, 39, 0.92);
  font-weight: 800;
}

/* 预约时段区域 */
.time-options-section {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .page-header-v2 {
    padding: 18px 14px 8px;
    flex-direction: column;
    align-items: stretch;
  }

  .booking-config-card {
    margin: 0 14px 14px;
    padding: 14px;
  }

  .config-grid-v2 {
    grid-template-columns: 1fr;
  }

  .my-bookings-btn {
    width: 100%;
    justify-content: center;
  }
}

.section-title {
  font-size: 18px;
  font-weight: 800;
  color: rgba(17, 24, 39, 0.92);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-tip {
  font-size: 12px;
  font-weight: normal;
  color: var(--member-text-dim);
  background: rgba(255, 159, 67, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

/* 推荐卡片精修 */
.recommend-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 220px));
  gap: 20px;
  margin-bottom: 32px;
  justify-content: center;
}

.recommend-card {
  background: white;
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 20px;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.03);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.recommend-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #FF9F43 0%, #FF6B6B 100%);
  color: white;
  font-size: 11px;
  padding: 4px 10px;
  border-bottom-left-radius: 12px;
  font-weight: 700;
  z-index: 2;
  box-shadow: -2px 2px 8px rgba(255, 107, 107, 0.3);
}

.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.8) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.recommend-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 30px rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.3);
}

.recommend-card:active {
  transform: translateY(-2px) scale(0.98);
}

.recommend-card.active {
  background: var(--member-gradient);
  border-color: transparent;
  box-shadow: 0 15px 25px rgba(102, 126, 234, 0.4);
}

.recommend-card.active .card-glow {
  opacity: 0.2;
}

.recommend-card.active .card-time {
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.recommend-card.active .card-reason {
  color: rgba(255, 255, 255, 0.9);
}

.card-time {
  font-size: 28px;
  font-weight: 900;
  color: var(--member-primary);
  margin-bottom: 8px;
  transition: all 0.3s;
  letter-spacing: -0.5px;
}

.card-reason {
  font-size: 13px;
  color: var(--member-text-dim);
  font-weight: 500;
  transition: all 0.3s;
}

/* 全量时段分组卡片 */
.available-section {
  padding: 0;
  background: transparent;
  border: none;
}

.time-group-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 0 16px; /* 增加左右内边距，使卡片不贴边界 */
}

.time-group-card {
  background: white;
  border-radius: 18px;
  padding: 20px;
  border: 1px solid #f0f2f5;
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}

.group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f8f9fa;
}

.group-header i {
  font-size: 18px;
  color: #ff9f43;
}

.group-name {
  font-weight: 700;
  font-size: 15px;
  color: var(--member-text-main);
}

.group-desc {
  font-size: 12px;
  color: var(--member-text-dim);
  margin-left: auto;
  background: #f8f9fa;
  padding: 2px 8px;
  border-radius: 10px;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
  gap: 12px;
}

.time-capsule {
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #edf2f7;
  background: #fff;
  color: var(--member-text-main);
  outline: none;
}

.time-capsule:hover {
  border-color: var(--member-primary);
  color: var(--member-primary);
  background: rgba(102, 126, 234, 0.04);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.1);
}

.time-capsule:focus-visible {
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.time-capsule.active {
  background: var(--member-gradient);
  border-color: transparent;
  color: white;
  box-shadow: 0 6px 15px rgba(102, 126, 234, 0.35);
  transform: translateY(-2px) scale(1.03);
}

.no-data-v2 {
  text-align: center;
  padding: 40px 20px;
  background: #f8f9fa;
  border-radius: 20px;
  color: var(--member-text-dim);
}

.no-data-v2 i {
  font-size: 40px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.submit-section {
  padding: 28px 0 52px;
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.submit-btn {
  padding: 14px 52px !important;
  border-radius: 16px !important;
  font-weight: 800 !important;
  font-size: 16px !important;
  box-shadow: 0 14px 30px rgba(102, 126, 234, 0.35);
}

@media (max-width: 1024px) {
  .time-grid { grid-template-columns: repeat(auto-fill, minmax(85px, 1fr)); }
}

@media (max-width: 768px) {
  .recommend-cards { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .time-grid { grid-template-columns: repeat(4, 1fr); gap: 8px; }
  .card-time { font-size: 22px; }
  .time-capsule { font-size: 13px; height: 38px; }
}

@media (max-width: 480px) {
  .time-grid { grid-template-columns: repeat(3, 1fr); }
}
</style>
