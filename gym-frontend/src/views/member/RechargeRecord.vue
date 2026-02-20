<template>
  <section class="rechargeRecord m-card">
    <header class="page-title">
      <h2><i class="el-icon-money"></i> 充值记录</h2>
      <p class="subtitle">查看账户充值明细与状态</p>
    </header>

    <el-table
      :data="tableData"
      stripe
      height="520"
      style="width: 100%"
      class="record-table"
    >
      <el-table-column prop="rechargeDate" label="充值时间" min-width="170" />
      <el-table-column prop="rechargeMoney" label="金额" width="120">
        <template slot-scope="scope">
          <span class="money">¥ {{ Number(scope.row.rechargeMoney || 0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="rechargeMethod" label="方式" width="140">
        <template slot-scope="scope">
          <el-tag size="mini" effect="light" type="info">{{ scope.row.rechargeMethod || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rechargeStatus" label="状态" width="140">
        <template slot-scope="scope">
          <el-tag
            size="mini"
            effect="light"
            :type="String(scope.row.rechargeStatus) === '1' ? 'success' : 'danger'"
          >
            {{ String(scope.row.rechargeStatus) === '1' ? '充值成功' : '充值失败' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script>
import {getAllMember, getRechargeByMemberNo, totalMember} from "@/api/allApi";

export default {
  data() {
    return {
      tableData: [],
      admin: {

      },
    }
  },
  mounted() {
    let _this = this
    getRechargeByMemberNo({
      memberNo:_this.admin.memberNo
    }).then(res=>{
      this.tableData = res.data
    }).catch(err=>{
      console.log(err.message)
    })
  },
  created() {
    this.admin = JSON.parse(window.localStorage.getItem('access-member'))
  },
  methods:{

  }
}
</script>
<style scoped>
.rechargeRecord {
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

.money {
  font-weight: 600;
  color: var(--member-primary);
  font-size: 15px;
}

.record-table {
  background: transparent !important;
}
</style>