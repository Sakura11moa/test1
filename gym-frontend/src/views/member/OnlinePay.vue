<template>
  <section class="onlinePay m-card">
    <header class="page-title">
      <h2><i class="el-icon-wallet"></i> 在线充值</h2>
      <p class="subtitle">选择充值金额与支付方式，余额将实时到账</p>
    </header>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-label">可用积分</div>
        <div class="stat-value">
          <span class="stat-number">{{ MemberIntegral }}</span>
          <span class="stat-unit">积分</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-label">可用余额</div>
        <div class="stat-value">
          <span class="stat-number">{{ Number(MemberChange || 0).toFixed(2) }}</span>
          <span class="stat-unit">元</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-label">累计充值</div>
        <div class="stat-value">
          <span class="stat-number">{{ Number(TotalMoney || 0).toFixed(2) }}</span>
          <span class="stat-unit">元</span>
        </div>
      </div>
    </div>

    <div class="pay-grid">
      <div class="pay-panel">
        <div class="panel-title">充值金额</div>

        <div class="amount-options">
          <button
            v-for="button in buttons"
            :key="button"
            type="button"
            :class="['amount-chip', { active: String(price) === String(button) }]"
            @click="yuan(button)"
          >
            {{ button }} 元
          </button>
        </div>

        <div class="amount-custom">
          <div class="custom-label">自定义金额</div>
          <el-input
            v-model="input"
            size="small"
            placeholder="输入充值金额（元）"
            clearable
            @blur="inputClick"
          >
            <template slot="prefix">
              <i class="el-icon-money"></i>
            </template>
          </el-input>
          <div class="custom-hint">支持整数金额，最低 1 元</div>
        </div>

        <div class="confirm-bar">
          <div class="confirm-info">
            <div class="confirm-label">本次充值</div>
            <div class="confirm-amount">¥ {{ Number(price || 0).toFixed(2) }}</div>
          </div>
          <el-button
            type="primary"
            class="pay-btn"
            :disabled="Number(price || 0) <= 0"
            @click="pay"
          >
            立即充值
          </el-button>
        </div>
      </div>

      <div class="pay-panel">
        <div class="panel-title">支付方式</div>

        <div class="method-grid">
          <div class="method-card" :class="{ active: payMethod === 'wechat' }" @click="payMethod = 'wechat'">
            <img src="../../assets/images/微信支付.png" alt="微信支付" />
            <div class="method-name">微信支付</div>
          </div>
          <div class="method-card" :class="{ active: payMethod === 'alipay' }" @click="payMethod = 'alipay'">
            <img src="../../assets/images/支付-支付宝.png" alt="支付宝" />
            <div class="method-name">支付宝</div>
          </div>
          <div class="method-card" :class="{ active: payMethod === 'unionpay' }" @click="payMethod = 'unionpay'">
            <img src="../../assets/images/银联.png" alt="银联" />
            <div class="method-name">银联</div>
          </div>
        </div>

        <div class="method-tip text-dim">
          当前仅记录充值流水，支付方式用于展示与后续拓展。
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import {
  addEmployee,
  addRechargeByMemberNo,
  getMemberChange,
  getMemberIntegral,
  getRechargeByMemberNo,
  getTotalMoney, updateMemberChange
} from "@/api/allApi";

export default {
  name: "OnlinePay",
  data() {
    return{
      input:'',
      buttons:['10','30','50','100','200'],
      admin:{

      },
      MemberIntegral:'',
      MemberChange:'',
      TotalMoney:'',
      price:'',
      payMethod: 'wechat',
    }
  },
  created() {
    this.admin = JSON.parse(window.localStorage.getItem('access-member'))
  },
  mounted() {
    this.getMemberIntegral()
    this.getTotalMoney()
    this.getMemberChange()
  },
  methods:{
    yuan(button){
      this.price = button
    },
    inputClick() {
      this.price = this.input
    },
    //立即支付
    pay(){

      let _this = this
      if(_this.price > 0){
        addRechargeByMemberNo({
          memberNo: _this.admin.memberNo,
          rechargeMoney:this.price,
        }).then(res=>{
          if(res.data.code===200){
            alert(res.data.message)
            updateMemberChange({
              memberNo:_this.admin.memberNo
            })
            this.getMemberChange()
            this.getTotalMoney()
          }else{
            alert(res.data.message)
          }
        }).catch(err=>{
          console.log(err)
        })
      }
      else{
        alert("充值成功")
        this.getMemberChange()
        this.getTotalMoney()
      }

    },
    getMemberIntegral(){
      let _this = this
      getMemberIntegral({
        memberNo: _this.admin.memberNo}).then(res=>{
        this.MemberIntegral = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    },

    getTotalMoney(){
      let _this = this
      getTotalMoney({
        memberNo: _this.admin.memberNo}).then(res=>{
        this.TotalMoney = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    },
    getMemberChange(){
      let _this = this
      getMemberChange({
        memberNo: _this.admin.memberNo}).then(res=>{
        this.MemberChange = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    },
  }
}
</script>

<style scoped>
.onlinePay {
  width: 100%;
  box-sizing: border-box;
}

.page-title {
  margin-bottom: 30px;
}

.page-title h2 {
  margin: 0 0 8px 0;
  font-weight: 700;
  font-size: 28px;
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

/* 顶部状态统计 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(102, 126, 234, 0.04);
  padding: 20px;
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.stat-label {
  font-size: 13px;
  color: var(--member-text-dim);
  margin-bottom: 8px;
}

.stat-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: var(--member-text-main);
}

.stat-unit {
  font-size: 12px;
  color: var(--member-text-muted);
}

/* 充值支付主区域 */
.pay-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.pay-panel {
  background: white;
  padding: 24px;
  border-radius: 16px;
  border: 1px solid var(--member-border);
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--member-text-main);
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid var(--member-primary);
}

/* 金额选择 */
.amount-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.amount-chip {
  padding: 12px;
  border-radius: 12px;
  border: 2px solid #f1f5f9;
  background: #f8fafc;
  color: var(--member-text-main);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.amount-chip:hover {
  border-color: var(--member-primary);
  color: var(--member-primary);
}

.amount-chip.active {
  background: var(--member-gradient);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.amount-custom {
  margin-bottom: 30px;
}

.custom-label {
  font-size: 14px;
  color: var(--member-text-dim);
  margin-bottom: 10px;
}

.custom-hint {
  font-size: 12px;
  color: var(--member-text-muted);
  margin-top: 6px;
}

/* 确认栏 */
.confirm-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.confirm-label {
  font-size: 12px;
  color: var(--member-text-muted);
}

.confirm-amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--member-primary);
}

.pay-btn {
  border-radius: 12px !important;
  padding: 12px 30px !important;
  font-weight: 600 !important;
}

/* 支付方式 */
.method-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  margin-bottom: 20px;
}

.method-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border-radius: 12px;
  border: 2px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
}

.method-card:hover {
  border-color: rgba(102, 126, 234, 0.2);
  background: #f8fafc;
}

.method-card.active {
  border-color: var(--member-primary);
  background: rgba(102, 126, 234, 0.02);
  position: relative;
}

.method-card.active::after {
  content: '\e6da';
  font-family: 'element-icons';
  position: absolute;
  right: 15px;
  color: var(--member-primary);
  font-size: 20px;
}

.method-card img {
  width: 32px;
  height: 32px;
}

.method-name {
  font-weight: 500;
  color: var(--member-text-main);
}

.method-tip {
  font-size: 12px;
  line-height: 1.5;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr; }
  .pay-grid { grid-template-columns: 1fr; }
}
</style>
