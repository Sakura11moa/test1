<template>
  <section class="add-member-container a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-user"></i>
          新增会员
        </h2>
        <p class="page-subtitle">快速录入会员基础信息、身体指标与账户权益</p>
      </div>
      <div class="action-area">
        <el-button size="small" icon="el-icon-back" @click="goBack" class="back-btn">返回列表</el-button>
      </div>
    </header>

    <div class="form-wrapper">
      <el-form ref="sizeForm" :model="sizeForm" label-position="top" size="small" :rules="rules" class="modern-form">
        <div class="form-grid">
          <div class="form-section">
            <h3 class="section-title"><i class="el-icon-info"></i> 基本信息</h3>
            <div class="input-group">
              <el-form-item label="姓名" prop="memberName">
                <el-input v-model="sizeForm.memberName" clearable placeholder="请输入会员姓名"></el-input>
              </el-form-item>
              <el-form-item label="性别" prop="memberGender">
                <el-select v-model="sizeForm.memberGender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="M"></el-option>
                  <el-option label="女" value="F"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="年龄" prop="memberAge">
                <el-input v-model="sizeForm.memberAge" clearable placeholder="请输入年龄"></el-input>
              </el-form-item>
              <el-form-item label="手机号" prop="memberPhone">
                <el-input v-model="sizeForm.memberPhone" clearable placeholder="请输入手机号"></el-input>
              </el-form-item>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title"><i class="el-icon-s-data"></i> 身体指标</h3>
            <div class="input-group">
              <el-form-item label="身高(cm)" prop="memberHeight">
                <el-input v-model="sizeForm.memberHeight" clearable placeholder="例如 175"></el-input>
              </el-form-item>
              <el-form-item label="体重(kg)" prop="memberWeight">
                <el-input v-model="sizeForm.memberWeight" clearable placeholder="例如 65"></el-input>
              </el-form-item>
              <el-form-item label="购买时长(课时)" prop="cardClass">
                <el-input v-model="sizeForm.cardClass" clearable placeholder="例如 10"></el-input>
              </el-form-item>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title"><i class="el-icon-wallet"></i> 账户权益</h3>
            <div class="input-group">
              <el-form-item label="积分" prop="memberIntegral">
                <el-input v-model="sizeForm.memberIntegral" clearable placeholder="请输入积分"></el-input>
              </el-form-item>
              <el-form-item label="余额(元)" prop="memberChange">
                <el-input v-model="sizeForm.memberChange" clearable placeholder="请输入余额"></el-input>
              </el-form-item>
              <el-form-item label="会员等级" prop="memberPower">
                <el-select v-model="sizeForm.memberPower" placeholder="请选择会员等级" style="width: 100%">
                  <el-option label="普通用户" value="0"></el-option>
                  <el-option label="体验VIP" value="1"></el-option>
                  <el-option label="包月VIP" value="2"></el-option>
                  <el-option label="包季VIP" value="3"></el-option>
                  <el-option label="包年VIP" value="4"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </div>
        </div>

        <div class="form-footer">
          <el-button type="primary" class="submit-btn" icon="el-icon-check" @click="onSubmit">确认新增会员</el-button>
          <el-button class="reset-btn" icon="el-icon-refresh" @click="resetForm('sizeForm')">重置</el-button>
        </div>
      </el-form>
    </div>
  </section>
</template>

<script>
import {addMember} from "@/api/allApi";

export default {
  name: "AddMembers",
  data() {
    let isInteger =(rule, value, callback) => {
      if (!value) {
        return callback(new Error('输入不可以为空'));
      }
      setTimeout(() => {
        if (!Number(value)) {
          callback(new Error('请输入正整数'));
        } else {
          const re = /^[0-9]*[1-9][0-9]*$/;
          const rsCheck = re.test(value);
          if (!rsCheck) {
            callback(new Error('请输入正整数'));
          } else {
            callback();
          }
        }
      }, 0);
    }

    return {
      sizeForm: {
        memberName: '',
        memberGender: '',
        memberAge: '',
        memberPhone: '',
        memberHeight: '',
        memberWeight: '',
        cardClass: '',
        memberIntegral:'',
        memberChange:'',
        memberPower:'',
      },
      rules: {
        memberName: [{required:true,message:'请输入姓名',trigger:'blur'}],
        memberGender: [{required:true,message:'请选择性别',trigger:'blur'}],
        memberAge: [
          {required:true,message:'请输入年龄',trigger:'blur'},
          {
            required: true,
            pattern: /^((1[0-5])|[1-9])?\d$/,
            message: '请输入合法的年龄！',
            trigger: 'change'
          }
        ],
        memberPhone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          {
            required: true,
            pattern: /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/,
            message: '请输入合法的手机号！',
            trigger: ['change','blur']
          }
        ],
        memberWeight: [
          {
            pattern:   /^(0(\.\d{1}){0,1}|[1-8]\d{1,3}(\.\d{1}){0,1}|9\d{1,2}(\.\d{1}){0,1}|999(\.0){0,1}|.{0})$/,
            message: '请输入合法的体重!',
            trigger: 'change'
          }
        ],
        memberPower:[{required:true,message:'请选择会员等级',trigger:'blur'}],
        memberHeight: [
          {
            pattern:    /^(0{1}|[1-9]\d{0,3}|.{0})$/,
            message: '请输入合法的身高!',
            trigger: 'change'
          }
        ],
        cardClass: [{required:true,message:'请输入购买时长',trigger:'blur'}],
        memberIntegral:[{required:true,message:'请输入积分',trigger:'blur'}, { validator: isInteger, trigger: 'blur' }],
        memberChange:[ {required:true,message:'请输入余额',trigger:'blur'},{ validator: isInteger, trigger: 'blur' }],
      },
    };
  },
  methods: {
    onSubmit() {
      this.$refs.sizeForm.validate((valid) => {
        if (!valid) return;

        addMember({
          memberName: this.sizeForm.memberName,
          memberGender: this.sizeForm.memberGender,
          memberAge: this.sizeForm.memberAge,
          memberPhone: this.sizeForm.memberPhone,
          memberHeight: this.sizeForm.memberHeight,
          memberWeight: this.sizeForm.memberWeight,
          cardClass:this.sizeForm.cardClass,
          memberIntegral:this.sizeForm.memberIntegral,
          memberChange:this.sizeForm.memberChange,
          memberPower:this.sizeForm.memberPower
        }).then(res=>{
          if(res.data.code===200){
            this.$message.success(res.data.message);
            this.goBack();
          }else{
            this.$message.error(res.data.message);
          }
        }).catch(err=>{
          console.log(err)
          this.$message.error('提交失败');
        })
      });
    },
    goBack() {
      this.$router.back();
    },
    resetForm(sizeForm) {
      this.$refs[sizeForm].resetFields();
    }
  }
};
</script>

<style scoped>
.add-member-container {
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
  max-width: 1100px;
  margin: 0 auto;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
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

.reset-btn {
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
