<template>
  <div class="vipCard m-glass m-card">
    <div class="charge-header">
      <div class="charge-header-left">
        <b class="number">{{MemberIntegral}}</b> 积分
        <p>可用积分</p>
      </div>
      <div class="charge-header-middle">
        <template>
          <b class="number" v-if="this.memberPower == '1'">体验VIP</b>
          <b class="number" v-else-if="this.memberPower == '2'">包月VIP</b>
          <b class="number" v-else-if="this.memberPower == '3'">包季VIP</b>
          <b class="number" v-else-if="this.memberPower == '4'">包年VIP</b>
          <b class="number" v-else>普通用户</b>
        </template>
        <p>当前权限</p>
      </div>
      <div class="charge-header-right">
        <b class="number">无</b>
        <p>剩余VIP免费训练次数</p>
      </div>
    </div>

    <div class="vip-items">
      <div class="item" v-for="item in vipCard">
        <div class="title" :style="{backgroundColor:item.color}">{{item.name}}</div>
        <div class="price">
          {{item.price}}
          <span>积分</span>
        </div>
        <div class="time">
          {{item.time}}
        </div>
          <ul>
            <li> {{item.li1}}</li>
            <li> {{item.li2}}</li>
            <li> {{item.li3}}</li>
          </ul>
        <a href="javascript:;" class="btn" :style="{backgroundColor:item.color}" @click="upgradeClick(item.price,item.name)">立即升级</a>
      </div>
    </div>

    <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="30%"
        :before-close="handleClose"
        append-to-body>
      <span>确定要使用<b style="color: #309975">{{price}}积分</b>开通<b style="color: #309975">{{name}}</b>吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="upgradeVIP()">确 定</el-button>
      </span>
    </el-dialog>
  </div>




</template>


<script>
import {
  getMemberChange,
  getMemberIntegral,
  getMemberPower,
  updateMemberIntegral,
  updateMemberPower
} from "@/api/allApi";

export default {
  name: "VipCard",
  data(){
    return {
      vipCards: [
        {
          name: '体验VIP',
          price: 15,
          duration: '1天体验',
          level: '1',
          icon: 'el-icon-present',
          gradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
          popular: false,
          hovered: false,
          benefits: [
            { icon: 'el-icon-price-tag', text: '全场9折优惠' },
            { icon: 'el-icon-time', text: '1次免费训练' },
            { icon: 'el-icon-star-off', text: '基础会员标识' }
          ],
          features: ['新用户专享']
        },
        {
          name: '包月VIP',
          price: 60,
          duration: '1个月',
          level: '2',
          icon: 'el-icon-moon',
          gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          popular: true,
          hovered: false,
          benefits: [
            { icon: 'el-icon-price-tag', text: '全场8折优惠' },
            { icon: 'el-icon-time', text: '5次免费训练' },
            { icon: 'el-icon-service', text: '专属教练指导' },
            { icon: 'el-icon-trophy', text: '优先预约课程' }
          ],
          features: ['热门选择', '性价比高']
        },
        {
          name: '包季VIP',
          price: 120,
          duration: '3个月',
          level: '3',
          icon: 'el-icon-orange',
          gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
          popular: false,
          hovered: false,
          benefits: [
            { icon: 'el-icon-price-tag', text: '全场7折优惠' },
            { icon: 'el-icon-time', text: '10次免费训练' },
            { icon: 'el-icon-service', text: '专属教练指导' },
            { icon: 'el-icon-trophy', text: '优先预约课程' },
            { icon: 'el-icon-data-analysis', text: '健身数据分析' }
          ],
          features: ['超值优惠']
        },
        {
          name: '包年VIP',
          price: 300,
          duration: '1年',
          level: '4',
          icon: 'el-icon-crown',
          gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
          popular: false,
          hovered: false,
          benefits: [
            { icon: 'el-icon-price-tag', text: '全场6折优惠' },
            { icon: 'el-icon-time', text: '20次免费训练' },
            { icon: 'el-icon-service', text: '专属教练指导' },
            { icon: 'el-icon-trophy', text: '优先预约课程' },
            { icon: 'el-icon-data-analysis', text: '专业健身数据分析' },
            { icon: 'el-icon-present', text: '生日专属礼物' },
            { icon: 'el-icon-mobile-phone', text: '24小时客服' }
          ],
          features: ['年度特惠', '尊贵体验']
        }
      ],
      comparisonFeatures: [
        {
          name: '折扣优惠',
          icon: 'el-icon-price-tag',
          levels: [
            { level: '0', available: false },
            { level: '1', available: true, value: '9折' },
            { level: '2', available: true, value: '8折' },
            { level: '3', available: true, value: '7折' },
            { level: '4', available: true, value: '6折' }
          ]
        },
        {
          name: '免费训练次数',
          icon: 'el-icon-time',
          levels: [
            { level: '0', available: false },
            { level: '1', available: true, value: '1次' },
            { level: '2', available: true, value: '5次' },
            { level: '3', available: true, value: '10次' },
            { level: '4', available: true, value: '20次' }
          ]
        },
        {
          name: '专属教练',
          icon: 'el-icon-service',
          levels: [
            { level: '0', available: false },
            { level: '1', available: false },
            { level: '2', available: true },
            { level: '3', available: true },
            { level: '4', available: true }
          ]
        },
        {
          name: '优先预约',
          icon: 'el-icon-trophy',
          levels: [
            { level: '0', available: false },
            { level: '1', available: false },
            { level: '2', available: true },
            { level: '3', available: true },
            { level: '4', available: true }
          ]
        },
        {
          name: '数据分析',
          icon: 'el-icon-data-analysis',
          levels: [
            { level: '0', available: false },
            { level: '1', available: false },
            { level: '2', available: false },
            { level: '3', available: true },
            { level: '4', available: true }
          ]
        },
        {
          name: '24h客服',
          icon: 'el-icon-mobile-phone',
          levels: [
            { level: '0', available: false },
            { level: '1', available: false },
            { level: '2', available: false },
            { level: '3', available: false },
            { level: '4', available: true }
          ]
        }
      ],
      admin: {},
      MemberIntegral: '',
      memberPower: '',
      upgradeDialog: {
        visible: false,
        card: null
      },
      upgrading: false
    }
  },
  created() {
    this.admin = JSON.parse(window.localStorage.getItem('access-member'))
  },
  mounted() {
    this.getMemberIntegral();
    this.getMemberPower();
  },
  methods:{
    /* 工具方法 */
    getLevelText(level) {
      const texts = {
        '0': '普通用户',
        '1': '体验VIP',
        '2': '包月VIP',
        '3': '包季VIP',
        '4': '包年VIP'
      };
      return texts[level] || '普通用户';
    },

    getLevelDesc(level) {
      const descs = {
        '0': '享受基础服务',
        '1': '体验VIP特权',
        '2': '月度会员尊享',
        '3': '季度会员优惠',
        '4': '年度会员专属'
      };
      return descs[level] || '享受基础服务';
    },

    getLevelClass(level) {
      const classes = {
        '0': 'normal',
        '1': 'trial',
        '2': 'monthly',
        '3': 'quarterly',
        '4': 'yearly'
      };
      return classes[level] || 'normal';
    },

    getFreeTrainings(level) {
      const trainings = {
        '0': '0',
        '1': '1',
        '2': '5',
        '3': '10',
        '4': '20'
      };
      return trainings[level] || '0';
    },

    isCurrentLevel(cardLevel) {
      return this.memberPower === cardLevel;
    },

    canUpgrade(price) {
      return parseInt(this.MemberIntegral) >= parseInt(price);
    },

    getComparisonIcon(available) {
      return available ? 'el-icon-check' : 'el-icon-close';
    },

    /* 升级相关方法 */
    handleUpgrade(card) {
      if (this.isCurrentLevel(card.level)) {
        this.$message.info('您已经是当前等级');
        return;
      }

      if (!this.canUpgrade(card.price)) {
        this.$message.warning('积分不足，无法升级');
        return;
      }

      this.upgradeDialog.card = card;
      this.upgradeDialog.visible = true;
    },

    async confirmUpgrade() {
      if (!this.upgradeDialog.card) return;

      try {
        this.upgrading = true;

        const card = this.upgradeDialog.card;

        // 更新积分
        await updateMemberIntegral({
          price: card.price,
          memberNo: this.admin.memberNo,
        });

        // 更新会员等级
        await updateMemberPower({
          memberPower: parseInt(card.level),
          memberNo: this.admin.memberNo
        });

        this.$message.success(`恭喜您成功升级为${card.name}！`);

        // 刷新数据
        await this.getMemberIntegral();
        await this.getMemberPower();

        this.upgradeDialog.visible = false;
        this.upgradeDialog.card = null;

      } catch (error) {
        console.error(error);
        this.$message.error('升级失败，请稍后重试');
      } finally {
        this.upgrading = false;
      }
    },

    /* 数据获取方法 */
    async getMemberIntegral(){
      try {
        const res = await getMemberIntegral({
          memberNo: this.admin.memberNo
        });
        this.MemberIntegral = res.data;
      } catch(err){
        console.log(err.message);
        this.$message.error('获取积分信息失败');
      }
    },

    async getMemberPower(){
      try {
        const res = await getMemberPower({
          memberNo: this.admin.memberNo
        });
        this.memberPower = res.data;
      } catch(err){
        console.log(err.message);
        this.$message.error('获取会员等级失败');
      }
    }
  }
}

</script>

<style scoped>
.vip-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

/* 页面头部 */
.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  text-align: center;
}

.header-title h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.header-title i {
  font-size: 36px;
  color: #667eea;
}

.header-subtitle {
  margin: 0;
  color: var(--member-text-dim);
  font-size: 16px;
}

/* 用户状态概览 */
.status-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.status-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  animation: slideInUp 0.6s ease-out;
}

.status-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.status-card:nth-child(1) { animation-delay: 0.1s; }
.status-card:nth-child(2) { animation-delay: 0.2s; }
.status-card:nth-child(3) { animation-delay: 0.3s; }

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.status-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.current-status .status-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.points-balance .status-icon {
  background: linear-gradient(135deg, #67C23A, #85CE61);
  color: white;
}

.free-trainings .status-icon {
  background: linear-gradient(135deg, #E6A23C, #F5DAA3);
  color: white;
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 14px;
  color: var(--member-text-dim);
  margin-bottom: 8px;
  font-weight: 500;
}

.status-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
  transition: all 0.3s ease;
}

.status-value.normal { color: var(--member-text-muted); }
.status-value.trial { color: #409EFF; }
.status-value.monthly { color: #67C23A; }
.status-value.quarterly { color: #E6A23C; }
.status-value.yearly { color: #F56C6C; }

.status-desc {
  font-size: 12px;
  color: #999;
}

/* VIP卡片区域 */
.vip-cards-section {
  margin-bottom: 32px;
}

.section-header {
  text-align: center;
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.section-title i {
  color: #667eea;
}

.section-desc {
  margin: 0;
  color: var(--member-text-dim);
  font-size: 14px;
}

.vip-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

/* VIP卡片 */
.vip-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  transition: all 0.4s ease;
  position: relative;
  animation: fadeInScale 0.8s ease-out;
}

.vip-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.vip-card.popular {
  border: 2px solid #E6A23C;
  box-shadow: 0 8px 32px rgba(230, 162, 60, 0.2);
}

.vip-card.current {
  border: 2px solid #67C23A;
  box-shadow: 0 8px 32px rgba(103, 194, 58, 0.2);
}

/* 卡片头部 */
.card-header {
  padding: 24px;
  color: white;
  position: relative;
  overflow: hidden;
}

.card-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: inherit;
  opacity: 0.9;
}

.card-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-badge.current {
  background: rgba(103, 194, 58, 0.9);
  color: white;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 8px;
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  font-size: 24px;
}

.card-price {
  margin-bottom: 8px;
  position: relative;
  z-index: 1;
}

.price-number {
  font-size: 36px;
  font-weight: 800;
  display: block;
  line-height: 1;
}

.price-unit {
  font-size: 14px;
  font-weight: 500;
  opacity: 0.9;
}

.card-duration {
  font-size: 12px;
  opacity: 0.8;
  position: relative;
  z-index: 1;
}

/* 卡片内容 */
.card-content {
  padding: 24px;
}

.benefits-list {
  margin-bottom: 16px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.benefit-icon {
  color: #667eea;
  font-size: 16px;
  flex-shrink: 0;
}

.card-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.feature-tag {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 卡片底部 */
.card-footer {
  padding: 0 24px 24px 24px;
}

.upgrade-btn {
  width: 100%;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.upgrade-btn:not(.disabled):not(.current) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.upgrade-btn:not(.disabled):not(.current):hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.upgrade-btn.disabled {
  background: #F5F7FA;
  color: #C0C4CC;
  cursor: not-allowed;
}

.upgrade-btn.current {
  background: linear-gradient(135deg, #67C23A, #85CE61);
  color: white;
  cursor: default;
}

/* 悬停效果 */
.card-hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(102, 126, 234, 0.95);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 24px;
  text-align: center;
  z-index: 10;
}

.hover-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}

.hover-benefits {
  font-size: 14px;
  line-height: 1.6;
}

/* 权益对比表 */
.comparison-section {
  margin-bottom: 32px;
}

.comparison-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.comparison-header {
  text-align: center;
  margin-bottom: 24px;
}

.comparison-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.comparison-title i {
  color: #667eea;
}

.comparison-table {
  overflow-x: auto;
}

.comparison-row {
  display: grid;
  grid-template-columns: 200px repeat(5, 1fr);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.comparison-row.header {
  background: rgba(102, 126, 234, 0.05);
  border-bottom: 2px solid #667eea;
}

.comparison-row.header .comparison-cell {
  font-weight: 600;
  color: #333;
}

.comparison-cell {
  padding: 12px 16px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}

.comparison-cell.feature {
  text-align: left;
  justify-content: flex-start;
  font-weight: 500;
  color: #333;
}

.comparison-cell.feature i {
  color: #667eea;
  margin-right: 8px;
}

/* 升级弹窗 */
.upgrade-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.upgrade-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin: 0;
  padding: 20px 24px;
}

.upgrade-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
}

.upgrade-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.upgrade-confirm {
  max-width: 400px;
  margin: 0 auto;
}

.confirm-header {
  text-align: center;
  margin-bottom: 24px;
}

.target-level {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.confirm-content {
  margin-bottom: 24px;
}

.confirm-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.confirm-item:last-child {
  border-bottom: none;
}

.confirm-item .label {
  font-weight: 500;
  color: var(--member-text-dim);
}

.confirm-item .value {
  font-weight: 600;
  color: #333;
}

.confirm-benefits {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
  padding: 20px;
}

.confirm-benefits h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
}

.confirm-benefits ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.confirm-benefits li {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.confirm-benefits li:last-child {
  margin-bottom: 0;
}

.confirm-benefits li i {
  color: #67C23A;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .vip-cards-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }

  .comparison-row {
    grid-template-columns: 150px repeat(5, 1fr);
  }
}

@media (max-width: 768px) {
  .vip-container {
    padding: 16px;
  }

  .status-overview {
    grid-template-columns: 1fr;
  }

  .vip-cards-grid {
    grid-template-columns: 1fr;
  }

  .comparison-row {
    grid-template-columns: 120px repeat(5, 1fr);
    font-size: 12px;
  }

  .comparison-cell {
    padding: 8px;
  }

  .page-title {
    font-size: 24px;
  }

  .status-card {
    padding: 20px;
  }

  .vip-card {
    margin: 0 auto;
    max-width: 320px;
  }
}

/* 滚动条样式 */
.comparison-table::-webkit-scrollbar {
  height: 6px;
}

.comparison-table::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.comparison-table::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 3px;
}
</style>