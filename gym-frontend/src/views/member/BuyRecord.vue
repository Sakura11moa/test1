<template>
  <div class="buyRecord m-card">
    <header class="page-title">
      <h2><i class="el-icon-tickets"></i> 购买记录与课时余量</h2>
    </header>

    <el-table
        :data="tableData"
        stripe
        height="520px"
        style="width: 100%"
        class="course-table">
      <el-table-column
          type="index"
          label="序号"
          width="60">
      </el-table-column>
      <el-table-column
          prop="courseName"
          label="课程名称"
          min-width="120">
      </el-table-column>
      <el-table-column
          prop="totalTimes"
          label="总购买课时"
          width="100">
        <template slot-scope="scope">
          <el-tag size="mini" type="info">{{ scope.row.totalTimes || 0 }} 节</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="remainTimes"
          label="可用余量"
          width="100">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.remainTimes > 0 ? 'success' : 'danger'">
            {{ scope.row.remainTimes || 0 }} 节
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="employeeNameCoach"
          label="授课教练">
      </el-table-column>
      <el-table-column
          prop="employeePhoneCoach"
          label="教练电话"
          width="130">
      </el-table-column>
      <el-table-column
          prop="memberPhone"
          label="我的手机号"
          width="130">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getPurchasedCourses } from "@/api/allApi";

export default {
  name: "BuyRecord",
  data() {
    return {
      tableData: [],
      admin: {},
    };
  },
  created() {
    this.admin = JSON.parse(window.localStorage.getItem('access-member')) || {};
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      // 1. 兜底获取 memberNo
      const stored = JSON.parse(localStorage.getItem('access-member') || '{}');
      const memberNo = (this.admin && this.admin.memberNo) || stored.memberNo;
      
      console.log('[BuyRecord] 正在请求 memberNo:', memberNo);

      if (!memberNo) {
        this.$message.error("会员信息缺失，请重新登录");
        return;
      }

      getPurchasedCourses({ memberNo }).then(res => {
        console.log('[BuyRecord] 接口原始返回:', res);
        
        // 2. 兼容各种返回结构
        let list = [];
        if (Array.isArray(res)) {
          list = res;
        } else if (res && Array.isArray(res.data)) {
          list = res.data;
        } else if (res && res.code === 200 && Array.isArray(res.data)) {
          list = res.data;
        }

        console.log('[BuyRecord] 解析后的列表:', list);
        this.tableData = list;

        if (list.length > 0) {
          console.log('[BuyRecord] 第一条数据详情(检查字段名):', {
            courseName: list[0].courseName,
            totalTimes: list[0].totalTimes,
            remainTimes: list[0].remainTimes
          });
        }
      }).catch(err => {
        console.error('[BuyRecord] 请求异常:', err);
        this.$message.error("获取购买记录失败，请查看控制台");
      });
    }
  }
}
</script>

<style scoped>
.buyRecord {
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
  background: transparent !important;
}
</style>