<template>
  <div class="profile-container">
    <!-- 页面头部 -->
    <div class="profile-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-user"></i>
          个人资料
        </h1>
        <p class="page-subtitle">完善您的个人信息，让健身之旅更加精彩</p>
      </div>
    </div>

    <div class="profile-content">
      <!-- 左侧 - 头像和基本信息 -->
      <div class="profile-left">
        <!-- 头像卡片 -->
        <div class="avatar-card">
          <div class="avatar-section">
            <div class="avatar-upload">
              <img :src="currentAvatar" class="profile-avatar" alt="头像" ref="avatarImg">
              <div class="avatar-overlay" @click="triggerFileSelect">
                <i class="el-icon-camera"></i>
                <span>更换头像</span>
              </div>
              <input
                type="file"
                ref="fileInput"
                @change="handleAvatarChange"
                accept="image/*"
                style="display: none"
              >
            </div>
            <div class="avatar-info">
              <h3>{{ memberForm.memberName || '会员昵称' }}</h3>
              <p class="username">@{{ memberForm.memberUsername }}</p>
              <div class="member-level">
                <el-tag
                  :type="getLevelType(memberForm.memberPower)"
                  size="small"
                  effect="light"
                >
                  {{ getLevelText(memberForm.memberPower) }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 身体指标卡片 -->
          <div class="body-metrics-card">
            <h4 class="card-title">
              <i class="el-icon-s-data"></i>
              身体指标
            </h4>
            <div class="metrics-grid">
              <div class="metric-item">
                <div class="metric-label">身高</div>
                <div class="metric-value">{{ memberForm.memberHeight || 0 }}cm</div>
                <el-progress
                  :percentage="getHeightPercentage(memberForm.memberHeight)"
                  :stroke-width="6"
                  :show-text="false"
                  color="#67C23A"
                ></el-progress>
              </div>
              <div class="metric-item">
                <div class="metric-label">体重</div>
                <div class="metric-value">{{ memberForm.memberWeight || 0 }}kg</div>
                <el-progress
                  :percentage="getWeightPercentage(memberForm.memberWeight)"
                  :stroke-width="6"
                  :show-text="false"
                  color="#E6A23C"
                ></el-progress>
              </div>
              <div class="metric-item">
                <div class="metric-label">BMI</div>
                <div class="metric-value">{{ calculateBMI() }}</div>
                <el-progress
                  :percentage="getBmiPercentage()"
                  :stroke-width="6"
                  :show-text="false"
                  :color="getBmiColor()"
                ></el-progress>
              </div>
            </div>
          </div>

          <!-- 统计信息卡片 -->
          <div class="stats-card">
            <h4 class="card-title">
              <i class="el-icon-s-marketing"></i>
              健身统计
            </h4>
            <div class="stats-list">
              <div class="stat-item">
                <span class="stat-label">注册时间</span>
                <span class="stat-value">{{ formatDate(memberForm.cardTime) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">可用积分</span>
                <span class="stat-value">{{ memberForm.memberIntegral || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">账户余额</span>
                <span class="stat-value">¥{{ memberForm.memberChange || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 会员卡状态卡片 -->
          <div class="card-status-card" :class="{ 'card-expired': cardStatus.cardStatus !== 1 }">
            <h4 class="card-title">
              <i class="el-icon-postcard"></i>
              会员卡状态
              <el-button
                type="text"
                icon="el-icon-refresh"
                size="mini"
                circle
                @click="loadCardStatus"
                class="refresh-btn"
                title="刷新卡状态"
              ></el-button>
            </h4>
            <div class="card-status-content">
              <div class="status-row">
                <span class="status-label">到期时间</span>
                <span class="status-value">{{ formatExpireTime(cardStatus.expireTime) }}</span>
              </div>
              <div class="status-row">
                <span class="status-label">剩余天数</span>
                <span class="status-value" :class="{ 'text-danger': cardStatus.daysLeft <= 7 && cardStatus.daysLeft > 0 }">
                  {{ cardStatus.daysLeft !== null && cardStatus.daysLeft !== undefined ? cardStatus.daysLeft + ' 天' : '暂无' }}
                </span>
              </div>
              <div class="status-row">
                <span class="status-label">卡状态</span>
                <el-tag
                  :type="getCardStatusType(cardStatus.daysLeft, cardStatus.cardStatus)"
                  size="small"
                  effect="light"
                >
                  {{ getCardStatusText(cardStatus.daysLeft, cardStatus.cardStatus) }}
                </el-tag>
              </div>
              <!-- 过期提示 -->
              <div v-if="cardStatus.daysLeft <= 0" class="expire-warning">
                <i class="el-icon-warning"></i>
                <span>请联系管理员续卡</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧 - 表单信息 -->
      <div class="profile-right">
        <div class="form-card">
          <div class="form-header">
            <h3 class="form-title">
              <i class="el-icon-edit"></i>
              编辑个人信息
            </h3>
            <p class="form-subtitle">修改您的基本信息和偏好设置</p>
          </div>

          <el-form
            ref="profileForm"
            :model="memberForm"
            :rules="formRules"
            label-position="top"
            class="profile-form"
          >
            <!-- 基本信息区域 -->
            <div class="form-section">
              <h4 class="section-title">
                <i class="el-icon-info"></i>
                基本信息
              </h4>
              <div class="form-grid">
                <el-form-item label="用户名" prop="memberUsername">
                  <el-input
                    v-model="memberForm.memberUsername"
                    disabled
                    class="readonly-input"
                  >
                    <i slot="prefix" class="el-input__icon el-icon-user"></i>
                  </el-input>
                </el-form-item>

                <el-form-item label="姓名" prop="memberName">
                  <el-input
                    v-model="memberForm.memberName"
                    placeholder="请输入您的姓名"
                    clearable
                  >
                    <i slot="prefix" class="el-input__icon el-icon-edit"></i>
                  </el-input>
                </el-form-item>

                <el-form-item label="年龄" prop="memberAge">
                  <el-input-number
                    v-model="memberForm.memberAge"
                    :min="1"
                    :max="120"
                    placeholder="年龄"
                    controls-position="right"
                  ></el-input-number>
                </el-form-item>

                <el-form-item label="性别" prop="memberGender">
                  <el-select v-model="memberForm.memberGender" placeholder="请选择性别">
                    <el-option label="男" value="M">
                      <i class="el-icon-male"></i>
                      <span>男</span>
                    </el-option>
                    <el-option label="女" value="F">
                      <i class="el-icon-female"></i>
                      <span>女</span>
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item label="手机号" prop="memberPhone">
                  <el-input
                    v-model="memberForm.memberPhone"
                    placeholder="请输入手机号"
                    clearable
                  >
                    <i slot="prefix" class="el-input__icon el-icon-mobile-phone"></i>
                  </el-input>
                </el-form-item>

                <el-form-item label="注册时间">
                  <el-input
                    :value="formatDate(memberForm.cardTime)"
                    disabled
                    class="readonly-input"
                  >
                    <i slot="prefix" class="el-input__icon el-icon-date"></i>
                  </el-input>
                </el-form-item>
              </div>
            </div>

            <!-- 身体信息区域 -->
            <div class="form-section">
              <h4 class="section-title">
                <i class="el-icon-s-data"></i>
                身体信息
              </h4>
              <div class="form-grid">
                <el-form-item label="身高(cm)" prop="memberHeight">
                  <el-input-number
                    v-model="memberForm.memberHeight"
                    :min="50"
                    :max="250"
                    :precision="1"
                    placeholder="身高"
                    controls-position="right"
                  >
                    <template slot="append">cm</template>
                  </el-input-number>
                </el-form-item>

                <el-form-item label="体重(kg)" prop="memberWeight">
                  <el-input-number
                    v-model="memberForm.memberWeight"
                    :min="20"
                    :max="300"
                    :precision="1"
                    placeholder="体重"
                    controls-position="right"
                  >
                    <template slot="append">kg</template>
                  </el-input-number>
                </el-form-item>
              </div>

              <!-- BMI计算结果 -->
              <div class="bmi-result" v-if="memberForm.memberHeight && memberForm.memberWeight">
                <div class="bmi-card">
                  <div class="bmi-header">
                    <span class="bmi-title">BMI 身体质量指数</span>
                    <span class="bmi-value" :class="getBmiStatus().class">{{ calculateBMI() }}</span>
                  </div>
                  <div class="bmi-status">{{ getBmiStatus().text }}</div>
                  <div class="bmi-progress">
                    <el-progress
                      :percentage="getBmiPercentage()"
                      :stroke-width="8"
                      :color="getBmiColor()"
                      :show-text="false"
                    ></el-progress>
                  </div>
                </div>
              </div>
            </div>

            <!-- 个性签名区域 -->
            <div class="form-section">
              <h4 class="section-title">
                <i class="el-icon-chat-dot-round"></i>
                个性签名
              </h4>
              <el-form-item prop="personalizedSignature">
                <el-input
                  type="textarea"
                  v-model="memberForm.personalizedSignature"
                  placeholder="写下您的健身宣言或个性签名..."
                  :rows="4"
                  maxlength="200"
                  show-word-limit
                  resize="none"
                ></el-input>
              </el-form-item>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <el-button
                type="primary"
                size="large"
                @click="onSubmit"
                :loading="submitting"
                class="submit-btn"
              >
                <i class="el-icon-check"></i>
                保存修改
              </el-button>
              <el-button
                size="large"
                @click="resetForm"
                class="reset-btn"
              >
                <i class="el-icon-refresh"></i>
                重置
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 头像裁剪弹窗 -->
    <el-dialog
      title="裁剪头像"
      :visible.sync="cropperVisible"
      width="600px"
      class="cropper-dialog"
      :close-on-click-modal="false"
      append-to-body
    >
      <div class="cropper-container">
        <img :src="cropperSrc" alt="裁剪图片" ref="cropperImg">
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cropperVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCrop" :loading="cropping">
          确定裁剪
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMemberByMemberNo, getMemberCardStatus, updateMemberByMemberNo } from "@/api/allApi";
import moment from "moment";

export default {
  name: "MyProfile",
  data() {
    return {
      memberForm: {},
      member: {},
      cardStatus: {
        expireTime: null,
        cardStatus: 1,
        daysLeft: null,
        statusDesc: ''
      },
      submitting: false,

      // 头像相关
      currentAvatar: require("@/assets/images/avatar.png"),
      cropperVisible: false,
      cropperSrc: "",
      cropping: false,

      // 表单验证规则
      formRules: {
        memberName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        memberAge: [
          { required: true, message: '请输入年龄', trigger: 'blur' },
          { type: 'number', min: 1, max: 120, message: '年龄必须在 1 到 120 之间', trigger: 'blur' }
        ],
        memberPhone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        memberHeight: [
          { type: 'number', min: 50, max: 250, message: '身高必须在 50 到 250 之间', trigger: 'blur' }
        ],
        memberWeight: [
          { type: 'number', min: 20, max: 300, message: '体重必须在 20 到 300 之间', trigger: 'blur' }
        ],
        personalizedSignature: [
          { max: 200, message: '个性签名不能超过 200 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.member = JSON.parse(window.localStorage.getItem('access-member'))
    // 强制重新读取会员信息
    this.refreshAllData()
  },
  mounted() {
    this.getMemberByMemberNo()
    this.loadCardStatus()
  },
  activated() {
    // 当使用 keep-alive 缓存时，每次激活组件都会调用
    this.loadCardStatus()
  },
  watch: {
    // 监听会员编号变化，强制刷新数据
    'member.memberNo': {
      handler(newVal) {
        if (newVal) {
          this.refreshAllData()
        }
      },
      immediate: true
    }
  },
  filters:{
    dataFormat(value){
      return moment(value).format("YYYY-MM-DD")
    }
  },
  methods: {
    /* 会员等级相关 */
    getLevelType(level) {
      const types = {
        '0': 'info',
        '1': '',
        '2': 'success',
        '3': 'warning',
        '4': 'danger'
      };
      return types[level] || 'info';
    },

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

    /* 身体指标相关 */
    getHeightPercentage(height) {
      if (!height) return 0;
      // 假设正常身高范围是150-190cm，计算百分比
      const min = 150;
      const max = 190;
      const percentage = ((height - min) / (max - min)) * 100;
      return Math.max(0, Math.min(100, percentage));
    },

    getWeightPercentage(weight) {
      if (!weight) return 0;
      // 假设正常体重范围是45-90kg，计算百分比
      const min = 45;
      const max = 90;
      const percentage = ((weight - min) / (max - min)) * 100;
      return Math.max(0, Math.min(100, percentage));
    },

    calculateBMI() {
      const height = this.memberForm.memberHeight;
      const weight = this.memberForm.memberWeight;

      if (!height || !weight) return '0.0';

      const bmi = (weight / ((height / 100) ** 2)).toFixed(1);
      return bmi;
    },

    getBmiPercentage() {
      const bmi = parseFloat(this.calculateBMI());
      if (bmi === 0) return 0;

      // BMI正常范围是18.5-24.9，计算百分比
      const min = 18.5;
      const max = 24.9;
      const percentage = ((bmi - min) / (max - min)) * 100;
      return Math.max(0, Math.min(100, percentage));
    },

    getBmiColor() {
      const bmi = parseFloat(this.calculateBMI());
      if (bmi < 18.5) return '#409EFF'; // 偏瘦
      if (bmi < 24.9) return '#67C23A'; // 正常
      if (bmi < 28) return '#E6A23C'; // 偏胖
      return '#F56C6C'; // 肥胖
    },

    getBmiStatus() {
      const bmi = parseFloat(this.calculateBMI());
      if (bmi < 18.5) return { text: '偏瘦 - 建议增加营养摄入', class: 'underweight' };
      if (bmi < 24.9) return { text: '正常 - 保持良好的生活习惯', class: 'normal' };
      if (bmi < 28) return { text: '偏胖 - 建议适当运动减重', class: 'overweight' };
      return { text: '肥胖 - 建议专业指导减重', class: 'obese' };
    },

    /* 头像相关方法 */
    triggerFileSelect() {
      this.$refs.fileInput.click();
    },

    handleAvatarChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      // 验证文件类型
      if (!file.type.includes('image/')) {
        this.$message.error('请选择图片文件');
        return;
      }

      // 验证文件大小 (最大5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.$message.error('图片大小不能超过5MB');
        return;
      }

      // 创建预览URL
      const reader = new FileReader();
      reader.onload = (e) => {
        this.cropperSrc = e.target.result;
        this.cropperVisible = true;
      };
      reader.readAsDataURL(file);
    },

    confirmCrop() {
      // 这里可以集成图片裁剪库，比如Cropper.js
      // 暂时直接使用选择的图片
      this.currentAvatar = this.cropperSrc;
      this.cropperVisible = false;
      this.$message.success('头像更新成功');
    },

    /* 表单相关方法 */
    formatDate(date) {
      if (!date) return '暂无';
      return moment(date).format("YYYY-MM-DD");
    },

    async onSubmit() {
      try {
        await this.$refs.profileForm.validate();

        this.submitting = true;

        const result = await updateMemberByMemberNo({
          memberName: this.memberForm.memberName,
          memberGender: this.memberForm.memberGender,
          memberAge: this.memberForm.memberAge,
          memberPhone: this.memberForm.memberPhone,
          memberHeight: this.memberForm.memberHeight,
          memberWeight: this.memberForm.memberWeight,
          personalizedSignature: this.memberForm.personalizedSignature,
          memberNo: this.member.memberNo
        });

        if (result.data.code === 200) {
          this.$message.success(result.data.message || '资料更新成功');
          // 重新获取数据
          await this.getMemberByMemberNo();
        } else {
          this.$message.error(result.data.message || '更新失败');
        }
      } catch (error) {
        if (error !== 'validation_failed') {
          console.log(error.message);
          this.$message.error('网络错误，请稍后重试');
        }
      } finally {
        this.submitting = false;
      }
    },

    resetForm() {
      this.getMemberByMemberNo();
      this.$message.info('已重置表单');
    },

    async getMemberByMemberNo() {
      try {
        const res = await getMemberByMemberNo({
          memberNo: this.member.memberNo
        });
        this.memberForm = res.data;
      } catch (err) {
        console.log(err.message);
        this.$message.error('获取个人信息失败');
      }
    },

    // 获取会员卡状态
    async loadCardStatus() {
      try {
        // 添加时间戳参数强制刷新
        const timestamp = new Date().getTime();
        const res = await getMemberCardStatus({
          memberNo: this.member.memberNo,
          _t: timestamp
        });
        console.log('========== 会员卡状态API原始返回 ==========');
        console.log('完整响应:', res);
        console.log('res.data:', res.data);
        console.log('expireTime:', res.data?.expireTime);
        console.log('cardStatus:', res.data?.cardStatus);
        console.log('daysLeft:', res.data?.daysLeft);
        console.log('statusDesc:', res.data?.statusDesc);
        console.log('==========================================');
        
        if (res.data && res.data.code === 200) {
          this.cardStatus = {
            expireTime: res.data.expireTime,
            cardStatus: res.data.cardStatus,
            daysLeft: res.data.daysLeft,
            statusDesc: res.data.statusDesc
          };
          console.log('解析后的卡状态数据:', this.cardStatus);
        } else {
          console.warn('API返回异常:', res.data);
        }
      } catch (err) {
        console.error('获取卡状态失败:', err);
      }
    },

    // 强制刷新所有数据
    async refreshAllData() {
      try {
        // 重新获取会员基本信息
        const memberRes = await getMemberByMemberNo({
          memberNo: this.member.memberNo
        });
        if (memberRes.data) {
          this.memberForm = memberRes.data;
          console.log('刷新后的会员基本信息:', this.memberForm);
        }
        
        // 重新获取卡状态
        await this.loadCardStatus();
      } catch (err) {
        console.error('刷新数据失败:', err);
      }
    },

    // 格式化到期时间，处理 null 值
    formatExpireTime(value) {
      if (!value) return '暂无';
      try {
        return moment(value).format("YYYY-MM-DD");
      } catch (e) {
        return '暂无';
      }
    },

    // 根据剩余天数和卡状态获取标签类型
    getCardStatusType(daysLeft, cardStatus) {
      // 如果剩余天数 > 0 或者卡状态为1（有效），显示成功状态
      if ((daysLeft !== null && daysLeft !== undefined && daysLeft > 0) || cardStatus === 1) {
        return 'success';
      }
      // 否则显示过期状态
      return 'danger';
    },

    // 根据剩余天数和卡状态获取状态文本
    getCardStatusText(daysLeft, cardStatus) {
      // 如果剩余天数 > 0 或者卡状态为1（有效），显示有效
      if ((daysLeft !== null && daysLeft !== undefined && daysLeft > 0) || cardStatus === 1) {
        return '有效';
      }
      // 已过期
      return '已过期';
    }
  }
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  padding: 0;
}

/* 页面头部重塑 */
.profile-header {
  margin-bottom: 30px;
  padding-top: 12px;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  background: var(--member-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.page-subtitle {
  margin: 0;
  color: var(--member-text-dim);
  font-size: 14px;
  line-height: 1.6;
}

.username {
  margin: 0 0 12px 0;
  color: var(--member-text-dim);
  font-size: 14px;
}

.metric-label,
.stat-label,
.bmi-title {
  font-size: 12px;
  color: var(--member-text-dim);
  font-weight: 600;
}

.bmi-status {
  color: var(--member-text-dim);
  font-size: 14px;
}

.metric-value,
.stat-value {
  color: var(--member-text-main);
  font-weight: 700;
}

/* 布局调整 */
.profile-content {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 30px;
}

/* 卡片统一重塑 */
.avatar-card, .form-card {
  background: var(--member-card-bg);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 1px solid var(--member-border);
  box-shadow: var(--card-shadow);
  padding: 30px;
}

.avatar-section {
  text-align: center;
}

.profile-avatar {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.avatar-upload:hover .profile-avatar {
  transform: scale(1.05);
  border-color: var(--member-primary);
}

.avatar-info h3 {
  margin: 15px 0 5px 0;
  color: var(--member-text-main);
  font-size: 22px;
}

/* 身体指标与统计卡片样式优化 */
.body-metrics-card, .stats-card {
  margin-top: 25px;
  background: rgba(102, 126, 234, 0.03);
  border-radius: 15px;
  padding: 20px;
  border: 1px solid rgba(102, 126, 234, 0.05);
}

/* 会员卡状态卡片 */
.card-status-card {
  margin-top: 25px;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.1) 0%, rgba(103, 194, 58, 0.05) 100%);
  border-radius: 15px;
  padding: 20px;
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.card-status-card.card-expired {
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.1) 0%, rgba(245, 108, 108, 0.05) 100%);
  border-color: rgba(245, 108, 108, 0.2);
}

.card-status-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-label {
  font-size: 13px;
  color: var(--member-text-dim);
}

.status-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--member-text-main);
}

.status-value.text-danger {
  color: #f56c6c;
}

.expire-warning {
  margin-top: 12px;
  padding: 12px;
  background: rgba(245, 108, 108, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #f56c6c;
  font-size: 13px;
  font-weight: 500;
}

.expire-warning i {
  font-size: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--member-primary);
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.refresh-btn {
  padding: 4px !important;
  margin-left: 8px;
}

.refresh-btn:hover {
  background: rgba(102, 126, 234, 0.1) !important;
  color: var(--member-primary) !important;
}

/* 表单区域精修 */
.form-header {
  margin-bottom: 24px;
  text-align: center;
}

.form-title {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--member-text-main);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.form-subtitle {
  margin: 0;
  color: var(--member-text-dim);
  font-size: 14px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--member-text-main);
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
  margin: 30px 0 20px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
}

/* 按钮样式对齐 */
.submit-btn {
  background: var(--member-gradient) !important;
  border: none !important;
  border-radius: 12px !important;
  padding: 12px 40px !important;
  font-weight: 600 !important;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3) !important;
}

.reset-btn {
  border-radius: 12px !important;
  padding: 12px 30px !important;
}

@media (max-width: 1024px) {
  .profile-content { grid-template-columns: 1fr; }
}
</style>
