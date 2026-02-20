<template>
  <div class="aside">
    <div class="sideMenu">
      <el-menu
          style="width: 100%; min-height: calc(100vh - 50px);"
          default-active="2"
          class="el-menu-vertical-demo"
          @open="handleOpen"
          @close="handleClose">
        <el-submenu index="0">
          <template slot="title">
            <i class="el-icon-user-solid"></i>
            <span>主页</span>
          </template>
          <el-menu-item index="1-1" @click="toIndex">主页</el-menu-item>
        </el-submenu>
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-user-solid"></i>
            <span>会员管理</span>
          </template>
          <el-menu-item index="1-1" @click="toMemberManage">查看会员</el-menu-item>
          <el-menu-item index="1-2" @click="toAddMembers">新增会员</el-menu-item>
        </el-submenu>
        <el-submenu index="2" v-if="adminInfo.adminAccount === 'admin'">
          <template slot="title">
            <i class="el-icon-s-home"></i>
            <span>健身房管理</span>
          </template>
          <el-menu-item index="2-1" @click="toCourseManage">课程管理</el-menu-item>
          <el-menu-item index="2-2" @click="toEquipmentManage">器材管理</el-menu-item>
          <el-menu-item index="2-3" @click="toVenueManage">场地管理</el-menu-item>
        </el-submenu>
        <el-submenu index="3">
          <template slot="title">
            <i class="el-icon-s-custom"></i>
            <span>员工管理</span>
          </template>
          <el-menu-item index="3-1" @click="toEmployeeManage">查看员工</el-menu-item>
          <el-menu-item index="3-2" @click="toAddEmployee" v-if="adminInfo.adminAccount === 'admin'">新增员工</el-menu-item>
        </el-submenu>
        <el-submenu index="4">
          <template slot="title">
            <i class="el-icon-s-order"></i>
            <span>预约与报名管理</span>
          </template>
          <el-menu-item index="4-2" @click="toBookingManage" v-if="adminInfo.adminAccount === 'admin'">会员预约管理</el-menu-item>
          <el-menu-item index="4-3" @click="toCoachBookingManage">教练课表管理</el-menu-item>
        </el-submenu>
        <el-submenu index="5" v-if="adminInfo.adminAccount === 'admin'">
          <template slot="title">
            <i class="el-icon-wallet"></i>
            <span>财务管理</span>
          </template>
          <el-menu-item index="5-1" @click="toFinanceLedger" v-if="false">财务流水</el-menu-item>
          <el-menu-item index="5-2" @click="toFinanceReport" v-if="false">财务报表</el-menu-item>
          <el-menu-item index="5-3" @click="toAccrualReport">权责发生制报表</el-menu-item>
          <el-menu-item index="5-4" @click="toCashFlowLedger">现金流水账本</el-menu-item>
          <el-menu-item index="5-5" @click="toCashFlowSummary">现金流水汇总</el-menu-item>
        </el-submenu>
      </el-menu>
    </div>

  </div>
</template>

<script>
export default {
  name: "Aside",
  data() {
    return {
      adminInfo: {}
    }
  },
  created() {
    this.adminInfo = JSON.parse(window.localStorage.getItem('access-admin')) || {};
  },
  methods: {
    handleOpen(key, keyPath) {

    },
    handleClose(key, keyPath) {

    },
    toIndex() {
      this.$router.push('/layout/index')
    },
    toMemberManage() {
      this.$router.push('/layout/MemberManage')
    },
    toAddMembers() {
      this.$router.push('/layout/AddMembers')
    },
    toCourseManage() {
      this.$router.push('/layout/CourseManage')
    },
    toEquipmentManage() {
      this.$router.push('/layout/EquipmentManage')
    },
    toVenueManage() {
      this.$router.push('/layout/VenueManage')
    },
    toEmployeeManage() {
      this.$router.push('/layout/EmployeeManage')
    },
    toAddEmployee() {
      this.$router.push('/layout/AddEmployee')
    },
    toRegisterManage() {
      this.$router.push('/layout/RegisterManage')
    },
    toBookingManage() {
      this.$router.push('/layout/bookingManage')
    },
    toCoachBookingManage() {
      this.$router.push('/layout/coachBookingManage')
    },
    toFinanceLedger() {
      this.$router.push('/layout/financeLedger')
    },
    toFinanceReport() {
      this.$router.push('/layout/financeReport')
    },
    toAccrualReport() {
      this.$router.push('/layout/accrualReport')
    },
    toCashFlowLedger() {
      this.$router.push('/layout/cashFlowLedger')
    },
    toCashFlowSummary() {
      this.$router.push('/layout/cashFlowSummary')
    },
  }
}
</script>

<style scoped>
  .sideMenu {
    width: 200px;
    height: 100%;
  }

  .aside {
    position: fixed;
    top: 70px;
    left: 0;
  }
</style>

