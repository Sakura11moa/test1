<template>
  <section class="course-manage a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-reading"></i>
          课程管理
        </h2>
        <p class="page-subtitle">管理课程排期、价格、积分与教练配置</p>
      </div>

      <div class="action-area">
        <el-button type="primary" icon="el-icon-plus" size="small" class="primary-btn" @click="addCoursePage">
          新增课程
        </el-button>

        <div class="search">
          <el-input
            placeholder="搜索课程名称 / 教练..."
            v-model="KeywordCourse"
            size="small"
            class="search-input"
            clearable
            @keyup.enter.native="getByKeyword(KeywordCourse)"
          >
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button type="primary" size="small" class="search-btn" icon="el-icon-search" @click="getByKeyword(KeywordCourse)">
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

        <el-table-column label="开课时间" width="160" align="center">
          <template slot-scope="scope">
            <span class="text-main">{{ scope.row.courseTime | dataFormat }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="courseDuration" label="课程时长" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" effect="plain" type="info">{{ scope.row.courseDuration }} 分钟</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="employeeNameCoach" label="授课教练" width="120" align="center" />

        <el-table-column prop="employeePhoneCoach" label="教练电话" width="130" align="center">
          <template slot-scope="scope">
            <span class="text-dim">{{ scope.row.employeePhoneCoach || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="coursePrice" label="课程售价" width="100" align="center">
          <template slot-scope="scope">
            <span class="price-text">¥ {{ Number(scope.row.coursePrice || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="courseIntegral" label="积分" width="80" align="center" />

        <el-table-column prop="courseDesc" label="课程描述" min-width="180">
          <template slot-scope="scope">
            <el-tooltip effect="dark" placement="top" :content="scope.row.courseDesc || '无'">
              <div class="ellipsis">{{ scope.row.courseDesc || '-' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button @click="editCourse(scope.row)" type="primary" size="mini" icon="el-icon-edit" circle plain></el-button>
            <el-button @click="deleteCourse(scope.row.courseNo)" type="danger" size="mini" icon="el-icon-delete" circle plain></el-button>
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

    <el-dialog title="添加课程" :visible.sync="dialogVisible2" width="30%" append-to-body>
      <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
        <el-form-item label="课程名称">
          <el-input v-model="sizeForm.courseName"></el-input>
        </el-form-item>
        <el-form-item label="开课时间">
          <el-date-picker
            v-model="sizeForm.courseTime"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            :picker-options="pickerOptions"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="课程时长">
          <el-input v-model="sizeForm.courseDuration"></el-input>
        </el-form-item>
        <el-form-item label="教练工号">
          <el-input v-model="sizeForm.employeeNo"></el-input>
        </el-form-item>
        <el-form-item label="经理工号">
          <el-input v-model="sizeForm.managerNo"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="sizeForm.coursePrice"></el-input>
        </el-form-item>
        <el-form-item label="积分">
          <el-input v-model="sizeForm.courseIntegral"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="sizeForm.courseDesc"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="addCourse">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="修改课程信息" :visible.sync="dialogVisible" width="30%" append-to-body>
      <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
        <el-form-item label="课程名称">
          <el-input v-model="sizeForm.courseName"></el-input>
        </el-form-item>
        <el-form-item label="开课时间">
          <el-date-picker
            v-model="sizeForm.courseTime"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            :picker-options="pickerOptions"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="课程时长">
          <el-input v-model="sizeForm.courseDuration"></el-input>
        </el-form-item>
        <el-form-item label="教练">
          <el-input v-model="sizeForm.employeeNo"></el-input>
        </el-form-item>
        <el-form-item label="项目经理">
          <el-input v-model="sizeForm.managerNo"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="sizeForm.coursePrice"></el-input>
        </el-form-item>
        <el-form-item label="积分">
          <el-input v-model="sizeForm.courseIntegral"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="sizeForm.courseDesc"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateCourse">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
import moment from 'moment';
import {
  addCourse,
  deleteCourse,
  getAllCourse,
  getByKeywordCourse,
  totalCourse,
  totalCourseFuzzy,
  updateCourse
} from "@/api/allApi";

export default {
  name: "CourseManage",
  data() {
    return {
      dialogFormVisible: false,
      tableData: [],
      sizeForm: {},
      dialogVisible: false,
      dialogVisible2: false,
      /*分页*/
      pageSize: 10,
      currentPage: 1,
      total: 0,
      searchValue: '',
      KeywordCourse: '',
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date());
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }]
      },
    }
  },
  computed: {
    indexMethod() {
      return (this.currentPage - 1) * 10 + 1
    }
  },
  filters: {
    dataFormat(value) {
      return moment(value).format("YYYY-MM-DD HH:mm:ss")
    }
  },
  mounted() {
    getAllCourse({
      page: 0,
      size: 10
    }).then(res => {
      this.tableData = res.data
    }).catch(err => {
      console.log(err.message)
    })

    totalCourse().then(res => {
      this.total = res.data.dataTotal
    })
  },
  methods: {
    onSubmit() {
      this.dialogFormVisible = false
      this.clear()
    },
    clear() {
      this.form = {
        username: '',
        password: '',
        name: '',
        gender: '',
        phone: '',
      }
    },
    cancel() {
      this.dialogFormVisible = false,
          this.form = {
            username: '',
            password: '',
            name: '',
            gender: '',
            phone: '',
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
          .catch(_ => {
          });
    },
    editCourse(value) {
      this.sizeForm = value
      this.dialogVisible = true
    },
    addCoursePage() {
      this.dialogVisible2 = true
      this.sizeForm = {}
    },
    addCourse() {
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      addCourse({
        courseName: this.sizeForm.courseName,
        courseTime: this.sizeForm.courseTime,
        courseDuration: this.sizeForm.courseDuration,
        employeeNo: this.sizeForm.employeeNo,
        managerNo:this.sizeForm.managerNo,
        courseIntegral:this.sizeForm.courseIntegral,
        coursePrice:this.sizeForm.coursePrice,
        courseDesc:this.sizeForm.courseDesc
      }).then(res => {
        if (res.data.code === 200) {
          getAllCourse({
            page: currentPage,
            size: pageSize
          }).then(res => {
            this.tableData = res.data
          })
          alert(res.data.message)
        } else {
          alert(res.data.message)
        }
      }).catch(err => {
        console.log(err.message)
      })
      this.sizeForm = {}, this.dialogVisible2 = false

    },

    deleteCourse(courseNo) {
      this.$confirm('此操作将永久删除该信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCourse({courseNo: courseNo}).then(res => {
          if (res.data.code === 200) {
            getAllCourse({
              page: 0,
              size: 10
            }).then(res => {
              this.tableData = res.data
            })
          } else {
            alert(res.data.message)
          }
        }).catch(err => {
          console.log(err.message)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    updateCourse() {
      updateCourse({
        courseNo: this.sizeForm.courseNo,
        courseName: this.sizeForm.courseName,
        courseTime: this.sizeForm.courseTime,
        courseDuration: this.sizeForm.courseDuration,
        employeeNo: this.sizeForm.employeeNo,
        managerNo:this.sizeForm.managerNo,
        courseIntegral:this.sizeForm.courseIntegral,
        coursePrice:this.sizeForm.coursePrice,
        courseDesc:this.sizeForm.courseDesc
      }).then(res => {
        if (res.data.code === 200) {
          getAllCourse({
            page: 0,
            size: 10
          }).then(res => {
            this.tableData = res.data
          })
        } else {
          alert(res.data.message)
        }
      }).catch(err => {
        console.log(err.message)
      })
      this.dialogVisible = false
    },
    changePage(params) {
      this.currentPage = (params - 1) * this.pageSize
      this.getAllCourse()
      this.currentPage = params
    },
    getByKeyword(KeywordCourse) {
      totalCourseFuzzy({
        keyWord: KeywordCourse
      }).then(res => {
        this.total = res.data.dataTotal
      }),
          getByKeywordCourse({
            keyWord: KeywordCourse,
            page: 0,
            size: 10
          }).then(res => {
            this.tableData = res.data
          }).catch(err => {
            console.log(err.message)
          })

    },
    getAllCourse() {
      getAllCourse({
        page: this.currentPage,
        size: this.pageSize
      }).then(res => {
        this.tableData = res.data
      }).catch(err => {
        console.log(err.message)
      })
    }
  },
}
</script>

<style scoped>
.course-manage {
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
  height: 55px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.course-cell,
.schedule-cell,
.coach-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-name {
  font-weight: 700;
  color: var(--admin-text-main);
}

.course-desc {
  font-size: 12px;
  color: var(--admin-text-dim);
}

.ellipsis {
  display: inline-block;
  max-width: 320px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-duration,
.coach-phone {
  font-size: 12px;
  color: var(--admin-text-dim);
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-duration i,
.coach-phone i {
  color: var(--admin-primary);
}

.course-time,
.coach-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--admin-text-main);
}

.price-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price {
  font-weight: 800;
  color: var(--admin-primary);
}

.integral {
  font-size: 12px;
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
  .search-input {
    width: 100%;
  }
}
</style>
