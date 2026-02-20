<template>
  <section class="register-manage a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-tickets"></i>
          报名表管理
        </h2>
        <p class="page-subtitle">管理课程报名记录，支持新增报名与关键字检索</p>
      </div>

      <div class="action-area">
        <el-button type="primary" icon="el-icon-plus" size="small" class="primary-btn" @click="addRegisterPage">
          新增报名
        </el-button>

        <div class="search">
          <el-input
            placeholder="搜索课程/教练/会员..."
            v-model="KeywordRegister"
            size="small"
            class="search-input"
            clearable
            @keyup.enter.native="getByKeyword(KeywordRegister)"
          >
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button type="primary" size="small" class="search-btn" icon="el-icon-search" @click="getByKeyword(KeywordRegister)">
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

        <el-table-column prop="courseName" label="课程名称" min-width="150" />

        <el-table-column prop="courseTime" label="开课时间" width="160" align="center" />

        <el-table-column prop="courseDuration" label="时长" width="90" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.courseDuration }} 分</span>
          </template>
        </el-table-column>

        <el-table-column prop="employeeName" label="教练姓名" width="120" />

        <el-table-column prop="employeePhone" label="教练手机号" width="130" align="center" />

        <el-table-column prop="memberName" label="会员姓名" width="120" />

        <el-table-column prop="memberPhone" label="会员手机号" width="130" align="center" />

        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              type="danger"
              size="mini"
              icon="el-icon-delete"
              circle
              plain
              @click="deleteRegister(scope.row.registerNo)"
            ></el-button>
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

    <el-dialog title="添加报名" :visible.sync="dialogVisible2" width="420px" append-to-body>
      <el-form ref="form" :model="sizeForm" label-position="top" size="small">
        <el-form-item label="课程" prop="courseNo">
          <el-select v-model="sizeForm.courseNo" placeholder="请选择课程" style="width: 100%" filterable>
            <el-option v-for="item in courseList" :key="item.courseNo" :label="item.courseName" :value="item.courseNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员手机号" prop="memberPhone">
          <el-input v-model="sizeForm.memberPhone" placeholder="请输入会员手机号" clearable></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="addRegister">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
import moment from 'moment';
import {
  getAllCourseRegister,
  getAllRegister,
  addRegister,
  deleteRegister,
  totalRegister,
  getByKeywordRegister,
  totalRegisterFuzzy
} from "@/api/allApi";

export default {
  name:"RegisterManage",
  data() {
    return {
      dialogVisible2:false,
      tableData: [],
      sizeForm:{},
      /*分页*/
      pageSize:10,
      currentPage:1,
      total:0,
      KeywordRegister:'',
      courseList:[]
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
    getAllRegister({
      page:0,
      size:10
    }).then(res=>{
      this.tableData = res.data
    }).catch(err=>{
      console.log(err.message)
    })

    totalRegister().then(res=>{
      this.total = res.data.dataTotal
    })

    getAllCourseRegister({}).then(res=>{
      this.courseList = res.data
    }).catch(err=>{
      console.log(err.message)
    })
  },
  methods: {
    goBack() {
      this.$router.back();
    },
    addRegisterPage(){
      this.sizeForm = {}
      this.dialogVisible2 = true
    },
    addRegister(){
      addRegister({
        courseNo:this.sizeForm.courseNo,
        memberPhone:this.sizeForm.memberPhone,
      }).then(res=>{
        if(res.data.code===200){
          getAllRegister({ page:0, size:10 }).then(res=>{
            this.tableData = res.data
          })
          this.$message.success(res.data.message)
        }else{
          this.$message.error(res.data.message)
        }
      }).catch(err=>{
        console.log(err.message)
        this.$message.error('添加失败')
      })
      this.sizeForm = {}
      this.dialogVisible2 = false
    },

    deleteRegister(registerNo){
      this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteRegister({registerNo:registerNo}).then(res=>{
          if(res.data.code===200){
            getAllRegister({ page:0, size:10 }).then(res=>{
              this.tableData = res.data
            })
            this.$message.success('删除成功')
          }else{
            this.$message.error(res.data.message)
          }
        }).catch(err=>{
          console.log(err.message)
          this.$message.error('删除失败')
        })
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消删除' });
      });
    },
    changePage(params) {
      this.currentPage = (params-1) * this.pageSize
      this.getAllRegister()
      this.currentPage = params
    },
    getByKeyword(KeywordRegister) {
      totalRegisterFuzzy({ keyWord:KeywordRegister }).then(res=>{
        this.total = res.data.dataTotal
      })
      getByKeywordRegister({ keyWord:KeywordRegister, page:0, size:10 }).then(res=>{
        this.tableData = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    },
    getAllRegister(){
      getAllRegister({ page:this.currentPage, size:this.pageSize }).then(res=>{
        this.tableData = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    }
  },
}
</script>

<style scoped>
.register-manage {
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
  gap: 12px;
  align-items: center;
  justify-content: flex-end;
  flex: 1;
  flex-wrap: wrap;
}

.primary-btn {
  border-radius: 8px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
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
  height: 58px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.course-cell,
.person-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-name,
.person-name {
  font-weight: 700;
  color: var(--admin-text-main);
}

.course-meta,
.person-phone {
  font-size: 12px;
  color: var(--admin-text-dim);
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-meta i,
.person-phone i {
  color: var(--admin-primary);
}

.sep {
  color: var(--admin-text-muted);
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
  .search-input {
    width: 100%;
  }
}
</style>
