<template>
  <section class="employee-manage a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-s-custom"></i>
          员工管理
        </h2>
        <p class="page-subtitle">管理员工信息与岗位配置</p>
      </div>

      <div class="action-area">
        <div class="search">
          <el-input
            placeholder="搜索姓名 / 工号 / 手机号..."
            v-model="KeywordEmployee"
            size="small"
            class="search-input"
            clearable
            @keyup.enter.native="getByKeyword(KeywordEmployee)"
          >
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button
            type="primary"
            size="small"
            class="search-btn"
            icon="el-icon-search"
            @click="getByKeyword(KeywordEmployee)"
          >
            搜索
          </el-button>
        </div>
      </div>
    </header>

    <div class="table-card">
      <el-table
        :data="tableData"
        stripe
        size="mini"
        style="width: 100%"
        class="modern-table"
      >
        <el-table-column type="index" :index="indexMethod" label="序号" width="55" align="center" />

        <el-table-column prop="employeeNo" label="员工工号" width="100" align="center" />
        
        <el-table-column prop="employeeName" label="姓名" width="120" />

        <el-table-column label="性别" width="80" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.employeeGender === 'F' ? 'danger' : 'primary'" effect="plain">
              {{ scope.row.employeeGender === 'F' ? '女' : '男' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="employeeAge" label="年龄" width="80" align="center" />

        <el-table-column prop="employeePhone" label="手机号" width="130" align="center">
          <template slot-scope="scope">
            <span class="text-main"><i class="el-icon-mobile-phone"></i> {{ scope.row.employeePhone }}</span>
          </template>
        </el-table-column>

        <el-table-column label="岗位" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" effect="dark" :type="jobTagType(scope.row.employeeJob)">
              {{ jobText(scope.row.employeeJob) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="入职时间" width="120" align="center">
          <template slot-scope="scope">
            <span class="date-text">{{ scope.row.employeeTime | dataFormat }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="employeeMessage" label="备注信息" min-width="180">
          <template slot-scope="scope">
            <el-tooltip effect="dark" placement="top" :content="scope.row.employeeMessage || '无'">
              <div class="ellipsis">{{ scope.row.employeeMessage || '-' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        background
        layout="prev, pager, next"
        @current-change="changePage"
        :total="total"
      >
      </el-pagination>
    </div>

    <el-dialog title="修改员工信息" :visible.sync="dialogVisible" width="30%" append-to-body>
      <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
        <el-form-item label="姓名">
          <el-input v-model="sizeForm.employeeName"></el-input>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="sizeForm.employeeAge"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="sizeForm.employeeGender" placeholder="请选择性别">
            <el-option label="男" value="M"></el-option>
            <el-option label="女" value="F"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="sizeForm.employeePhone"></el-input>
        </el-form-item>
        <el-form-item label="职务" prop="region">
          <el-select v-model="sizeForm.employeeJob" placeholder="请选择职务">
            <el-option label="教练" value="1"></el-option>
            <el-option label="经理" value="4"></el-option>
            <el-option label="前台" value="2"></el-option>
            <el-option label="保洁" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="sizeForm.employeeMessage"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateEmployee">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
import moment from 'moment';
import {
  deleteEmployee,
  getAllEmployee,
  getByKeywordEmployee,
  totalEmployee,
  totalEmployeeFuzzy,
  updateEmployee
} from "@/api/allApi";

export default {
  name:"EmployeeManage",
  data() {
    return {
      dialogFormVisible: false,
      tableData: [],
      sizeForm:{},
      dialogVisible: false,
      /*分页*/
      pageSize:10,
      currentPage:1,
      total:0,
      searchValue:'',
      KeywordEmployee:''
    }
  },
  computed: {
    indexMethod(){
      return (this.currentPage-1) *10 +1
    }
  },
  filters:{
    dataFormat(value){
      return moment(value).format("YYYY-MM-DD")
    }
  },
  mounted() {
    getAllEmployee({
      page:0,
      size:10
    }).then(res=>{
      this.tableData = res.data
    }).catch(err=>{
      console.log(err.message)
    })

    totalEmployee().then(res=>{
      this.total = res.data.dataTotal
    })
  },
  methods: {
    jobText(job) {
      const j = String(job);
      const map = { '1': '教练', '2': '前台', '3': '保洁', '4': '经理' };
      return map[j] || '未知';
    },
    jobTagType(job) {
      const j = String(job);
      const map = { '1': 'success', '2': 'primary', '3': 'info', '4': 'warning' };
      return map[j] || 'info';
    },
    onSubmit() {
      this.dialogFormVisible = false
      this.clear()
    },
    clear() {
      this.form={
        username: '',
        password:'',
        name:'',
        gender:'',
        phone:'',
      }
    },
    cancel(){
      this.dialogFormVisible = false,
          this.form={
            username: '',
            password:'',
            name:'',
            gender:'',
            phone:'',
          }
    },
    goBack() {
      this.$router.back();
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
    },
    editEmployee(value) {
      this.sizeForm = value
      this.sizeForm.employeeJob = value.employeeJob.toString()
      this.dialogVisible = true
    },

    deleteEmployee(employeeNo){
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      this.$confirm('此操作将永久删除该信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEmployee({employeeNo:employeeNo}).then(res=>{
          if(res.data.code===200){
            getAllEmployee({
              page:currentPage,
              size:pageSize
            }).then(res=>{
              this.tableData = res.data
            })
          }else{
            alert(res.data.message)
          }
        }).catch(err=>{
          console.log(err.message)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    updateEmployee() {
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      updateEmployee({
        employeeName:this.sizeForm.employeeName,
        employeeAge:this.sizeForm.employeeAge,
        employeeGender:this.sizeForm.employeeGender,
        employeePhone:this.sizeForm.employeePhone,
        employeeJob:this.sizeForm.employeeJob,
        employeeMessage:this.sizeForm.employeeMessage,
        employeeNo:this.sizeForm.employeeNo
      }).then(res=>{
        if(res.data.code===200){
          getAllEmployee({
            page:currentPage,
            size:pageSize
          }).then(res=>{
            this.tableData = res.data
          })
        }else{
          alert(res.data.message)
        }
      }).catch(err=>{
        console.log(err.message)
      })
      this.dialogVisible = false
    },
    changePage(params) {
      this.currentPage = (params-1) * this.pageSize
      this.getAllEmployee()
      this.currentPage = params
    },
    getByKeyword(KeywordEmployee) {
      totalEmployeeFuzzy({
        keyWord:KeywordEmployee
      }).then(res=>{
        this.total = res.data.dataTotal
      }),
          getByKeywordEmployee({
            keyWord:KeywordEmployee,
            page:0,
            size:10
          }).then(res=>{
            this.tableData = res.data
          }).catch(err=>{
            console.log(err.message)
          })

    },
    getAllEmployee(){
      getAllEmployee({
        page:this.currentPage,
        size:this.pageSize
      }).then(res=>{
        this.tableData = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    }
  },
}
</script>

<style scoped>
.employee-manage {
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  background: var(--admin-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  color: var(--admin-primary);
}

.page-subtitle {
  margin: 0;
  color: var(--admin-text-dim);
  font-size: 14px;
}

.action-area {
  display: flex;
  justify-content: flex-end;
  flex: 1;
}

.search {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 280px;
}

.search-input :deep(.el-input__inner) {
  border-radius: 20px;
  border: 1px solid var(--admin-border);
}

.search-btn {
  border-radius: 20px !important;
}

.table-card {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 4px;
}

.modern-table :deep(.el-table__header th) {
  background: rgba(37, 99, 235, 0.05) !important;
  color: var(--admin-primary) !important;
  font-weight: 700;
  height: 50px;
}

.modern-table :deep(.el-table__row) {
  height: 55px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.emp-info-cell,
.emp-contact-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.emp-name {
  font-weight: 700;
  color: var(--admin-text-main);
}

.emp-meta {
  font-size: 12px;
  color: var(--admin-text-dim);
}

.emp-phone {
  font-size: 13px;
  color: var(--admin-text-main);
  display: flex;
  align-items: center;
  gap: 6px;
}

.emp-phone i {
  color: var(--admin-primary);
}

.emp-basic {
  font-size: 12px;
  color: var(--admin-text-dim);
  display: flex;
  align-items: center;
}

.sep {
  margin: 0 6px;
  color: var(--admin-text-muted);
}

.badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--admin-primary);
  font-weight: 600;
}

.date-text {
  color: var(--admin-text-dim);
  font-weight: 600;
}

.ellipsis {
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--admin-text-dim);
}

/* 弹窗样式统一 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: var(--admin-gradient);
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: white !important;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white !important;
}

@media (max-width: 1024px) {
  .action-area {
    width: 100%;
    justify-content: flex-start;
  }

  .search-input {
    width: 100%;
  }
}
</style>
