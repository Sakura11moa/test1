<template>
  <section class="m-card allCourse">
    <header class="page-title">
      <h2><i class="el-icon-reading"></i> 全部课程</h2>
    </header>

    <el-table
        :data="tableData"
        height="520px"
        stripe
      size="mini"
      class="course-table"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <div class="expand-wrapper">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="课程名称"><span>{{ props.row.courseName }}</span></el-form-item>
              <el-form-item label="课程价格"><span>{{ props.row.coursePrice }} 元</span></el-form-item>
              <el-form-item label="开课时间"><span>{{ props.row.courseTime }}</span></el-form-item>
              <el-form-item label="课程时长"><span>{{ props.row.courseDuration }} 分钟</span></el-form-item>
              <el-form-item label="授课教练"><span>{{ props.row.employeeNameCoach }}</span></el-form-item>
              <el-form-item label="联系方式"><span>{{ props.row.employeePhoneCoach }}</span></el-form-item>
              <el-form-item label="课程积分"><span>{{ props.row.courseIntegral }}</span></el-form-item>
              <el-form-item label="课程描述"><span>{{ props.row.courseDesc }}</span></el-form-item>
            </el-form>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="courseNo" label="编号" width="80" />
      <el-table-column prop="courseName" label="课程名称" min-width="120" />
      <el-table-column label="授课教练" width="100">
        <template slot-scope="scope">
          <el-tag size="mini" type="info" v-if="scope.row.employeeNameCoach">{{ scope.row.employeeNameCoach }}</el-tag>
          <span v-else>未分配</span>
        </template>
      </el-table-column>
      <el-table-column prop="courseDesc" label="课程描述" min-width="180" />
      <el-table-column prop="coursePrice" label="原价" width="80" />
      <el-table-column prop="discountPrice" label="优惠价" width="80" />
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button
            type="primary"
            plain
            size="mini"
            @click="handleBuy(scope.row)"
          >购买</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 购买确认弹窗 -->
    <el-dialog title="购买课程" :visible.sync="buyDialogVisible" width="350px" append-to-body class="m-glass">
      <el-form label-position="top" size="mini">
        <div class="balance-info" style="margin-bottom: 15px; padding: 10px; background: rgba(24, 160, 251, 0.1); border-radius: 8px;">
          <p style="margin: 0; color: #666;">当前可用余额：<b style="color: #18a0fb; font-size: 16px;">{{ currentBalance }}</b> 元</p>
        </div>

        <el-form-item label="购买数量 (1-99)">
          <el-input-number v-model="buyQuantity" :min="1" :max="99" style="width: 100%"></el-input-number>
        </el-form-item>

        <div v-if="selectedCourseForBuy" style="margin-top: 10px; font-size: 13px; border-top: 1px dashed #eee; padding-top: 10px;">
          <p>课程单价：{{ selectedCourseForBuy.coursePrice }} 元</p>
          <p>应付总计：<b style="color: #f56c6c; font-size: 16px;">{{ totalPrice }}</b> 元</p>
          
          <div v-if="totalPrice > currentBalance" style="color: #f56c6c; margin-top: 8px; font-weight: bold;">
            <i class="el-icon-warning"></i> 余额不足，请先充值
          </div>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="buyDialogVisible = false">取消</el-button>
        <el-button 
          size="mini" 
          type="primary" 
          :loading="buyLoading" 
          :disabled="!selectedCourseForBuy || totalPrice > currentBalance"
          @click="confirmPurchase"
        >确认购买</el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
import moment from "moment";
import {
  getAllCourse,
  getMemberPower,
  purchaseCourse,
  getMemberChange,
} from "@/api/allApi";

export default {
  name: "AllCourse",
  data() {
    return {
      tableData: [],
      admin: {},
      memberPower: "",
      // 购买相关
      currentBalance: 0,
      buyDialogVisible: false,
      buyLoading: false,
      buyQuantity: 1,
      selectedCourseForBuy: null,
    };
  },
  computed: {
    totalPrice() {
      if (!this.selectedCourseForBuy) return 0;
      return (this.selectedCourseForBuy.coursePrice * this.buyQuantity).toFixed(2);
    }
  },
  filters: {
    dataFormat(value) {
      return moment(value).format("YYYY-MM-DD");
    },
  },
  created() {
    this.admin = JSON.parse(localStorage.getItem("access-member")) || {};
  },
  mounted() {
    this.getMemberPower();
    this.getAllCourse();
  },
  methods: {
    async handleBuy(row) {
      this.selectedCourseForBuy = row;
      this.buyQuantity = 1;
      this.buyDialogVisible = true;
      // 打开弹窗时刷新余额
      try {
        const res = await getMemberChange({ memberNo: this.admin.memberNo });
        this.currentBalance = Number(res?.data || 0);
      } catch (e) {
        this.currentBalance = 0;
      }
    },
    async confirmPurchase() {
      if (!this.selectedCourseForBuy) return;
      this.buyLoading = true;
      try {
        const res = await purchaseCourse({
          memberNo: this.admin.memberNo,
          courseNo: this.selectedCourseForBuy.courseNo,
          quantity: this.buyQuantity
        });
        
        if (res.data.code === 200) {
          const totalAmount = res.data.totalAmount || (this.selectedCourseForBuy.coursePrice * this.buyQuantity);
          this.$message.success(`购买成功，课程次数已生效！总计消费: ${totalAmount} 元`);
          this.buyDialogVisible = false;
        } else {
          this.$message.error(res.data.message || "购买失败");
        }
      } catch (e) {
        console.error(e);
        this.$message.error("购买失败，请检查余额或稍后再试");
      } finally {
        this.buyLoading = false;
      }
    },
    async getMemberPower() {
      try {
        const res = await getMemberPower({ memberNo: this.admin.memberNo });
        this.memberPower = res.data;
      } catch (e) {
        console.log(e);
      }
    },
    async getAllCourse() {
      try {
        const res = await getAllCourse({ page: 0, size: 50 });
        this.tableData = res.data || [];
      } catch (e) {
        console.log(e);
      }
    },
  },
};
</script>

<style scoped>
  .allCourse {
  width: 100%;
  box-sizing: border-box;
  }
.page-title {
  margin-bottom: 20px;
  text-align: left;
}

.page-title h2 {
  margin: 0;
  font-weight: 700;
  font-size: 24px;
  background: var(--member-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}
.course-table {
  --el-table-bg-color: transparent;
}

.expand-wrapper {
  width: 90%;
  margin: auto;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>
