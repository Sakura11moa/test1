<template>
  <section class="add-employee-container a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-user-add"></i>
          新增员工
        </h2>
        <p class="page-subtitle">录入新员工基本信息、分配岗位职责及账号权限</p>
      </div>
      <div class="action-area">
        <el-button size="small" icon="el-icon-back" @click="goBack" class="back-btn">返回列表</el-button>
      </div>
    </header>

    <div class="form-wrapper">
      <el-form ref="sizeForm" :model="sizeForm" label-position="top" class="modern-form">
        <div class="form-grid">
          <!-- 基本信息区域 -->
          <div class="form-section">
            <h3 class="section-title"><i class="el-icon-info"></i> 基本身份信息</h3>
            <div class="input-group">
              <el-form-item label="员工姓名">
                <el-input v-model="sizeForm.employeeName" placeholder="请输入姓名"></el-input>
              </el-form-item>
              <el-form-item label="性别">
                <el-select v-model="sizeForm.employeeGender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="M">
                    <i class="el-icon-male"></i> 男
                  </el-option>
                  <el-option label="女" value="F">
                    <i class="el-icon-female"></i> 女
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="年龄">
                <el-input v-model="sizeForm.employeeAge" type="number" placeholder="输入年龄"></el-input>
              </el-form-item>
              <el-form-item label="手机号码">
                <el-input v-model="sizeForm.employeePhone" placeholder="11位手机号"></el-input>
              </el-form-item>
            </div>
          </div>

          <!-- 岗位与权限区域 -->
          <div class="form-section">
            <h3 class="section-title"><i class="el-icon-s-management"></i> 岗位与账号配置</h3>
            <div class="input-group">
              <el-form-item label="担任职务">
                <el-select v-model="sizeForm.employeeJob" placeholder="请选择职务" style="width: 100%">
                  <el-option label="教练" value="1"></el-option>
                  <el-option label="前台" value="2"></el-option>
                  <el-option label="保洁" value="3"></el-option>
                  <el-option label="经理" value="4"></el-option>
                </el-select>
              </el-form-item>

              <!-- 教练账号配置 -->
              <transition name="el-fade-in-linear">
                <div v-if="String(sizeForm.employeeJob) === '1'" class="account-setup">
                  <el-form-item label="登录账号">
                    <el-input v-model="sizeForm.account" placeholder="用于教练端登录"></el-input>
                  </el-form-item>
                  <el-form-item label="初始密码">
                    <el-input v-model="sizeForm.password" placeholder="请输入初始密码" show-password></el-input>
                  </el-form-item>
                </div>
              </transition>

              <el-form-item label="备注信息">
                <el-input 
                  type="textarea" 
                  v-model="sizeForm.employeeMessage" 
                  placeholder="如有特殊说明请在此填写..."
                  :rows="3"
                ></el-input>
              </el-form-item>
            </div>
          </div>
        </div>

        <div class="form-footer">
          <el-button type="primary" class="submit-btn" @click="onSubmit" icon="el-icon-check">确认新增员工</el-button>
          <el-button @click="goBack" class="cancel-btn">取消</el-button>
        </div>
      </el-form>
    </div>
  </section>
</template>

<script>
import {addEmployee} from "@/api/allApi";

export default {
  name: "AddEmployee",
  data() {
    return {
      sizeForm: {
        employeeName: '',
        employeeGender: '',
        employeeAge: '',
        employeePhone: '',
        employeeJob: '',
        employeeMessage: '',
        account: '',
        password: ''
      }
    };
  },
  methods: {
    onSubmit() {
      if (!this.sizeForm.employeeName || !this.sizeForm.employeePhone) {
        return this.$message.warning("请完善基本信息");
      }
      addEmployee({
        employeeName: this.sizeForm.employeeName,
        employeeGender: this.sizeForm.employeeGender,
        employeeAge: this.sizeForm.employeeAge,
        employeePhone: this.sizeForm.employeePhone,
        employeeJob: this.sizeForm.employeeJob,
        employeeMessage: this.sizeForm.employeeMessage,
        account: this.sizeForm.account,
        password: this.sizeForm.password
      }).then(res=>{
        if(res.data.code===200){
          this.$message.success(res.data.message);
          this.goBack();
        }else{
          this.$message.error(res.data.message);
        }
      }).catch(err=>{
        console.log(err);
        this.$message.error("提交异常");
      })
    },
    goBack() {
      this.$router.back();
    }
  }
};
</script>

<style scoped>
.add-employee-container {
  padding: 30px;
  width: 100%;
  box-sizing: border-box;
  animation: fadeIn 0.5s ease-out;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 700;
  background: var(--admin-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i { color: var(--admin-primary); }

.page-subtitle {
  margin: 0;
  color: var(--admin-text-dim);
  font-size: 14px;
}

.form-wrapper {
  max-width: 1000px;
  margin: 0 auto;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.form-section {
  background: rgba(255, 255, 255, 0.4);
  padding: 24px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--admin-text-main);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--admin-border);
}

.section-title i { color: var(--admin-primary); }

.account-setup {
  background: rgba(37, 99, 235, 0.04);
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 18px;
  border: 1px dashed var(--admin-primary);
}

.form-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 30px;
  border-top: 1px solid var(--admin-border);
}

.submit-btn {
  padding: 12px 40px !important;
  border-radius: 12px !important;
  font-weight: 600 !important;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.2) !important;
}

.cancel-btn {
  padding: 12px 30px !important;
  border-radius: 12px !important;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 900px) {
  .form-grid { grid-template-columns: 1fr; }
}
</style>
