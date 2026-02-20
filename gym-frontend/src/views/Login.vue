<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="background-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-wrapper">
          <img src="../assets/logo2.png" class="logo-icon" alt="Logo">
          <div class="logo-text">
            <h1 class="brand-name">健身房管理系统</h1>
            <p class="brand-subtitle">Fitness Management System</p>
          </div>
        </div>
        <div class="welcome-text">
          <span class="text-primary">欢迎回来！</span>
          <span class="text-secondary">请登录您的账户</span>
        </div>
      </div>

      <!-- 表单区域 -->
      <el-form
        :model="ruleForm"
        :rules="rules"
        ref="ruleForm"
        label-position="left"
        label-width="0px"
        class="login-form"
      >
        <!-- 用户身份选择 -->
        <div class="identity-selector">
          <div class="selector-tabs">
            <div
              :class="['tab-item', { 'active': identity === '1' }]"
              @click="identity = '1'"
            >
              <i class="el-icon-user-solid"></i>
              <span>会员登录</span>
            </div>
            <div
              :class="['tab-item', { 'active': identity === '2' }]"
              @click="identity = '2'"
            >
              <i class="el-icon-s-custom"></i>
              <span>管理员登录</span>
            </div>
            <div
              :class="['tab-item', { 'active': identity === '3' }]"
              @click="identity = '3'"
            >
              <i class="el-icon-coordinate"></i>
              <span>教练登录</span>
            </div>
          </div>
        </div>

        <!-- 登录表单 (会员/管理员/教练) -->
        <div v-if="!isRegisterMode" class="form-fields">
          <el-form-item prop="adminAccount" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-user input-icon"></i>
              <el-input
                v-model="ruleForm.adminAccount"
                :placeholder="identity === '1' ? '请输入手机号' : '请输入用户名'"
                autocomplete="off"
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>

          <el-form-item prop="adminPassword" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-lock input-icon"></i>
              <el-input
                type="password"
                v-model="ruleForm.adminPassword"
                placeholder="请输入密码"
                autocomplete="off"
                @keyup.enter.native="submitForm"
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>
        </div>

        <!-- 注册表单 -->
        <div v-else class="form-fields">
          <el-form-item prop="memberName" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-user input-icon"></i>
              <el-input
                v-model="ruleForm.memberName"
                placeholder="请输入姓名"
                autocomplete="off"
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>

          <el-form-item prop="memberGender" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-male input-icon"></i>
              <el-select
                v-model="ruleForm.memberGender"
                placeholder="请选择性别"
                class="custom-input"
                style="width: 100%;"
              >
                <el-option label="男" value="M"></el-option>
                <el-option label="女" value="F"></el-option>
              </el-select>
            </div>
          </el-form-item>

          <el-form-item prop="memberAge" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-date input-icon"></i>
              <el-input
                v-model="ruleForm.memberAge"
                placeholder="请输入年龄"
                autocomplete="off"
                class="custom-input"
                type="number"
              ></el-input>
            </div>
          </el-form-item>

          <el-form-item prop="memberPhone" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-mobile-phone input-icon"></i>
              <el-input
                v-model="ruleForm.memberPhone"
                placeholder="请输入手机号"
                autocomplete="off"
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>

          <el-form-item prop="memberPassword" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-lock input-icon"></i>
              <el-input
                type="password"
                v-model="ruleForm.memberPassword"
                placeholder="请输入密码"
                autocomplete="off"
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>

          <el-form-item prop="confirmPassword" class="form-item">
            <div class="input-wrapper">
              <i class="el-icon-lock input-icon"></i>
              <el-input
                type="password"
                v-model="ruleForm.confirmPassword"
                placeholder="请确认密码"
                autocomplete="off"
                class="custom-input"
              ></el-input>
            </div>
          </el-form-item>
        </div>

        <!-- 按钮 -->
        <el-form-item class="form-item">
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="submitForm"
          >
            <span class="btn-text">{{ isRegisterMode ? '立即注册' : '立即登录' }}</span>
            <i class="el-icon-arrow-right btn-icon"></i>
          </el-button>
        </el-form-item>

        <!-- 注册跳转链接 -->
        <div v-if="!isRegisterMode && identity === '1'" class="register-link-section">
          <span class="register-hint">还没账号？</span>
          <el-link type="primary" :underline="false" @click="isRegisterMode = true" class="register-link">去注册</el-link>
          <span class="divider-v">|</span>
          <el-link type="info" :underline="false" @click="forgetDialogVisible = true" class="register-link">忘记密码？</el-link>
        </div>
        <div v-if="isRegisterMode" class="register-link-section">
          <el-link type="info" :underline="false" @click="isRegisterMode = false" class="register-link">返回登录</el-link>
        </div>
      </el-form>

      <!-- 忘记密码弹窗 -->
      <el-dialog title="重置会员密码" :visible.sync="forgetDialogVisible" width="400px" append-to-body>
        <el-form :model="forgetForm" :rules="forgetRules" ref="forgetForm" label-position="top">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="forgetForm.phone" placeholder="请输入注册手机号"></el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <div style="display: flex; gap: 10px;">
              <el-input v-model="forgetForm.code" placeholder="6位验证码"></el-input>
              <el-button type="primary" @click="handleSendCode" :disabled="codeTimer > 0">
                {{ codeTimer > 0 ? `${codeTimer}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input type="password" v-model="forgetForm.newPassword" placeholder="请输入新密码"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="forgetDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleResetPassword" :loading="resetLoading">确认重置</el-button>
        </div>
      </el-dialog>

      <!-- 底部装饰 -->
      <div class="card-footer">
        <div class="divider">
          <span class="divider-line"></span>
          <span class="divider-text">安全可靠的健身管理平台</span>
          <span class="divider-line"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { adminLogin, getMemberPassword, registerMember, sendMemberResetCode, resetMemberPassword } from "@/api/allApi";

export default {

    data(){
      return{
        identity:'1',
        isRegisterMode: false,
        loading: false,
        ruleForm: {
          adminAccount: '',
          adminPassword: '',
          // 注册表单字段
          memberName: '',
          memberGender: '',
          memberAge: '',
          memberPhone: '',
          memberPassword: '',
          confirmPassword: ''
        },
        forgetDialogVisible: false,
        resetLoading: false,
        codeTimer: 0,
        codeTimerHandle: null,
        forgetForm: {
          phone: '',
          code: '',
          newPassword: ''
        },
        forgetRules: {
          phone: [
            { required: true, message: '请输入手机号', trigger: 'blur' },
            {
              pattern: /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/,
              message: '请输入合法的手机号',
              trigger: ['change', 'blur']
            }
          ],
          code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
          newPassword: [
            { required: true, message: '请输入新密码', trigger: 'blur' },
            { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
          ]
        },
        rules: {
          adminAccount: [{required: true, message: '请输入用户名', trigger: 'blur'}],
          adminPassword: [{required: true, message: '请输入密码', trigger: 'blur'}],
          // 注册表单验证规则
          memberName: [{required: true, message: '请输入姓名', trigger: 'blur'}],
          memberGender: [{required: true, message: '请选择性别', trigger: 'change'}],
          memberAge: [
            {required: true, message: '请输入年龄', trigger: 'blur'},
            {
              pattern: /^((1[0-5])|[1-9])?\d$/,
              message: '请输入合法的年龄（1-150岁）',
              trigger: 'change'
            }
          ],
          memberPhone: [
            {required: true, message: '请输入手机号', trigger: 'blur'},
            {
              pattern: /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/,
              message: '请输入合法的手机号',
              trigger: ['change','blur']
            }
          ],
          memberPassword: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {min: 6, message: '密码长度不能少于6位', trigger: 'blur'}
          ],
          confirmPassword: [
            {required: true, message: '请确认密码', trigger: 'blur'},
            {
              validator: (rule, value, callback) => {
                if (value !== this.ruleForm.memberPassword) {
                  callback(new Error('两次输入密码不一致'));
                } else {
                  callback();
                }
              },
              trigger: 'blur'
            }
          ]
        }
      }
    },
  methods: {
    startCodeTimer() {
      if (this.codeTimerHandle) {
        clearInterval(this.codeTimerHandle);
        this.codeTimerHandle = null;
      }
      this.codeTimer = 60;
      this.codeTimerHandle = setInterval(() => {
        this.codeTimer -= 1;
        if (this.codeTimer <= 0) {
          clearInterval(this.codeTimerHandle);
          this.codeTimerHandle = null;
          this.codeTimer = 0;
        }
      }, 1000);
    },
    handleSendCode() {
      this.$refs.forgetForm.validateField('phone', async (err) => {
        if (err) return;
        try {
          const res = await sendMemberResetCode({ memberPhone: this.forgetForm.phone });
          if (res?.data?.code === 200) {
            this.$message.success('验证码已发送');
            // A方案：开发态后端返回验证码，直接回填方便测试
            if (res?.data?.resetCode) {
              this.forgetForm.code = String(res.data.resetCode);
            }
            this.startCodeTimer();
          } else {
            this.$message.error(res?.data?.message || '发送失败');
          }
        } catch (e) {
          this.$message.error('发送失败');
        }
      });
    },
    handleResetPassword() {
      this.$refs.forgetForm.validate(async (valid) => {
        if (!valid) return;
        this.resetLoading = true;
        try {
          const res = await resetMemberPassword({
            memberPhone: this.forgetForm.phone,
            code: this.forgetForm.code,
            newPassword: this.forgetForm.newPassword
          });
          if (res?.data?.code === 200) {
            this.$message.success('密码重置成功，请用新密码登录');
            this.forgetDialogVisible = false;
            this.forgetForm.phone = '';
            this.forgetForm.code = '';
            this.forgetForm.newPassword = '';
          } else {
            this.$message.error(res?.data?.message || '重置失败');
          }
        } catch (e) {
          this.$message.error('重置失败');
        } finally {
          this.resetLoading = false;
        }
      });
    },
    submitForm() {
      const fields = this.isRegisterMode
        ? ['memberName', 'memberGender', 'memberAge', 'memberPhone', 'memberPassword', 'confirmPassword']
        : ['adminAccount', 'adminPassword'];

      this.$refs.ruleForm.validateField(fields, async (errorMessage) => {
        if (!errorMessage) {
          this.loading = true;
          try {
            if (this.isRegisterMode) {
              // 会员注册
              const res = await this.registerMember();
              if (res && res.code === 200) {
                this.$message({
                  message: '注册成功，请登录',
                  type: 'success'
                });
                this.isRegisterMode = false;
                this.identity = '1';
                this.resetRegisterForm();
              }
            } else if (this.identity === "2" || this.identity === "3") {
              // 管理员 (2) 或 教练 (3) 登录
              const res = await adminLogin({
                ...this.ruleForm,
                type: this.identity === "3" ? "coach" : "admin"
              });
              const response = { data: res.data };
              if (response.data != null && response.data.code !== 400) {
                window.localStorage.setItem('access-admin', JSON.stringify(response.data));
                // 如果是教练登录且没有绑定员工号，提示一下
                if (this.identity === "3" && !response.data.employeeNo) {
                   this.$message.warning("该账号未绑定教练信息，部分功能可能受限");
                }
                if (this.identity === "3") {
                  this.$router.replace({path: '/layout/coachBookingManage'});
                } else {
                  this.$router.replace({path: '/layout'});
                }
              } else {
                this.$message({
                  message: response.data?.message || '登录失败，请检查用户名和密码',
                  type: 'error'
                });
              }
            } else {
              // 会员登录 (identity === "1")
              const memberPhone = this.ruleForm.adminAccount;
              const memberPassword = this.ruleForm.adminPassword;
              const res = await getMemberPassword({
                memberPhone: memberPhone,
                memberPassword: memberPassword
              });

              if (res.data.code === 200) {
                window.localStorage.setItem('access-member', JSON.stringify(res.data));
                this.$router.replace({path: '/memberLayout'});
              } else {
                this.$message({
                  message: '账号或密码错误',
                  type: 'error'
                });
              }
            }
          } catch (err) {
            console.log(err.message);
            this.$message({
              message: '网络错误，请稍后重试',
              type: 'error'
            });
          } finally {
            this.loading = false;
          }
        } else {
          console.log('表单验证失败');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs.ruleForm.resetFields();
    },
    async registerMember() {
      try {
        const registerData = {
          memberName: this.ruleForm.memberName,
          memberGender: this.ruleForm.memberGender,
          memberAge: this.ruleForm.memberAge,
          memberPhone: this.ruleForm.memberPhone,
          memberPassword: this.ruleForm.memberPassword,
          memberIntegral: 0, // 新注册用户积分默认为0
          memberChange: 0,   // 新注册用户余额默认为0
          memberPower: 0     // 新注册用户等级默认为普通用户
        };

        const res = await registerMember(registerData);
        return res.data;

      } catch (error) {
        console.error('注册失败:', error);
        this.$message({
          message: '注册失败，请稍后重试',
          type: 'error'
        });
        return null;
      }
    },
    resetRegisterForm() {
      this.ruleForm.memberName = '';
      this.ruleForm.memberGender = '';
      this.ruleForm.memberAge = '';
      this.ruleForm.memberPhone = '';
      this.ruleForm.memberPassword = '';
      this.ruleForm.confirmPassword = '';
    }
  }
}

</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 动态背景形状 */
.background-shapes {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  left: -5%;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  top: 60%;
  right: -5%;
  animation-delay: 2s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  left: 10%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 30%;
  right: 20%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideIn 0.8s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-icon {
  width: 50px;
  height: 50px;
  margin-right: 15px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.logo-text h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 12px;
  font-weight: 400;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.welcome-text {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.text-primary {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.text-secondary {
  font-size: 14px;
  color: #666;
}

/* 身份选择器 */
.identity-selector {
  margin-bottom: 30px;
}

.selector-tabs {
  display: flex;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 12px;
  padding: 4px;
  gap: 4px;
}

.tab-item {
  flex: 1;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.tab-item:hover {
  background: rgba(255, 255, 255, 0.8);
  color: #333;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tab-item i {
  font-size: 16px;
}

/* 表单字段 */
.form-fields {
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  z-index: 2;
  color: #999;
  font-size: 18px;
  transition: color 0.3s ease;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__inner) {
  padding-left: 50px;
  height: 50px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.custom-input :deep(.el-input__inner):hover {
  border-color: rgba(102, 126, 234, 0.5);
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-text {
  position: relative;
  z-index: 2;
}

.btn-icon {
  position: relative;
  z-index: 2;
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-icon {
  transform: translateX(4px);
}

/* 卡片底部 */
.card-footer {
  margin-top: 30px;
  text-align: center;
}

.divider {
  display: flex;
  align-items: center;
  gap: 15px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.1), transparent);
}

.divider-text {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

/* 注册跳转链接 */
.register-link-section {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}
.register-hint {
  color: #909399;
}
.register-link {
  font-weight: 500;
  margin-left: 5px;
}
.divider-v {
  margin: 0 10px;
  color: #dcdfe6;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    margin: 20px;
  }

  .logo-section {
    margin-bottom: 30px;
  }

  .logo-wrapper {
    flex-direction: column;
    gap: 15px;
  }

  .logo-text h1 {
    font-size: 20px;
  }
}

/* 加载状态 */
.login-btn.is-loading {
  pointer-events: none;
}

.login-btn :deep(.el-icon-loading) {
  margin-right: 8px;
}
</style>
