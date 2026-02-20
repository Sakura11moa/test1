<template>
  <section class="equipment-manage a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-cpu"></i>
          器材管理
        </h2>
        <p class="page-subtitle">管理器材位置、运行状态与备注信息</p>
      </div>

      <div class="action-area">
        <el-button type="primary" icon="el-icon-plus" size="small" class="primary-btn" @click="addEquipmentPage">
          新增器材
        </el-button>

        <div class="search">
          <el-input
            placeholder="搜索器材名称 / 位置..."
            v-model="KeywordEquipment"
            size="small"
            class="search-input"
            clearable
            @keyup.enter.native="getByKeyword(KeywordEquipment)"
          >
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button type="primary" size="small" class="search-btn" icon="el-icon-search" @click="getByKeyword(KeywordEquipment)">
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

        <el-table-column label="器材" min-width="220">
          <template slot-scope="scope">
            <div class="eq-info-cell">
              <div class="eq-name">{{ scope.row.equipmentName }}</div>
              <div class="eq-loc"><i class="el-icon-location-outline"></i> {{ scope.row.equipmentLocation }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="140" align="center">
          <template slot-scope="scope">
            <el-tag
              size="mini"
              :type="String(scope.row.equipmentState) === '1' ? 'success' : 'danger'"
              effect="dark"
            >
              {{ String(scope.row.equipmentState) === '1' ? '正常使用' : '非正常使用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="备注" min-width="180">
          <template slot-scope="scope">
            <el-tooltip effect="dark" placement="top" :content="scope.row.equipmentMessage || '无'">
              <div class="ellipsis">{{ scope.row.equipmentMessage || '-' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              @click="editEquipment(scope.row)"
              type="primary"
              size="mini"
              icon="el-icon-edit"
              circle
              plain
              title="编辑"
            ></el-button>
            <el-button
              @click="deleteEquipment(scope.row.equipmentNo)"
              type="danger"
              size="mini"
              icon="el-icon-delete"
              circle
              plain
              title="删除"
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

    <el-dialog title="添加器材" :visible.sync="dialogVisible2" width="30%" append-to-body>
      <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
        <el-form-item label="器材名称">
          <el-input v-model="sizeForm.equipmentName"></el-input>
        </el-form-item>
        <el-form-item label="器材位置">
          <el-input v-model="sizeForm.equipmentLocation"></el-input>
        </el-form-item>
        <el-form-item label="器材状态">
          <el-select v-model="sizeForm.equipmentState" placeholder="请选择器材状态" style="width: 100%">
            <el-option label="正常使用" value="1"></el-option>
            <el-option label="非正常使用" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="sizeForm.equipmentMessage"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="addEquipment">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="修改器材信息" :visible.sync="dialogVisible" width="30%" append-to-body>
      <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
        <el-form-item label="器材名称">
          <el-input v-model="sizeForm.equipmentName"></el-input>
        </el-form-item>
        <el-form-item label="器材位置">
          <el-input v-model="sizeForm.equipmentLocation"></el-input>
        </el-form-item>
        <el-form-item label="器材状态">
          <el-select v-model="sizeForm.equipmentState" placeholder="请选择器材状态" style="width: 100%">
            <el-option label="正常使用" value="1"></el-option>
            <el-option label="非正常使用" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="sizeForm.equipmentMessage"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateEquipment">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
import moment from 'moment';
import {addEquipment, getAllEquipment} from "@/api/allApi";
import {deleteEquipment} from "@/api/allApi";
import {updateEquipment} from "@/api/allApi";
import {totalEquipment} from "@/api/allApi";
import {getByKeywordEquipment} from "@/api/allApi";
import {totalEquipmentFuzzy} from "@/api/allApi";

export default {
  name:"EquipmentManage",
  data() {
    return {
      dialogFormVisible: false,
      tableData: [],
      sizeForm:{},
      dialogVisible: false,
      dialogVisible2: false,
      /*分页*/
      pageSize:10,
      currentPage:1,
      total:0,
      searchValue:'',
      KeywordEquipment:''
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
    getAllEquipment({
      page:0,
      size:10
    }).then(res=>{
      this.tableData = res.data
    }).catch(err=>{
      console.log(err.message)
    })

    totalEquipment().then(res=>{
      this.total = res.data.dataTotal
    })
  },
  methods: {
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
    editEquipment(value) {
      this.sizeForm = value
      this.dialogVisible = true
    },
    addEquipmentPage(){
      this.dialogVisible2 = true
      this.sizeForm = {}
    },
    addEquipment(){
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      addEquipment({
        equipmentName:this.sizeForm.equipmentName,
        equipmentLocation:this.sizeForm.equipmentLocation,
        equipmentState:this.sizeForm.equipmentState,
        equipmentMessage:this.sizeForm.equipmentMessage,
      }).then(res=>{
        if(res.data.code===200){
          getAllEquipment({
            page:currentPage,
            size:pageSize
          }).then(res=>{
            this.tableData = res.data
          })
          alert(res.data.message)
        }else{
          alert(res.data.message)
        }
      }).catch(err=>{
        console.log(err.message)
      })
      this.sizeForm = {},
      this.dialogVisible2 = false

    },

    deleteEquipment(equipmentNo){
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      this.$confirm('此操作将永久删除该信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEquipment({equipmentNo:equipmentNo}).then(res=>{
          if(res.data.code===200){
            getAllEquipment({
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
    updateEquipment() {
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      updateEquipment({
        equipmentName:this.sizeForm.equipmentName,
        equipmentLocation:this.sizeForm.equipmentLocation,
        equipmentState:this.sizeForm.equipmentState,
        equipmentMessage:this.sizeForm.equipmentMessage,
        equipmentNo:this.sizeForm.equipmentNo
      }).then(res=>{
        if(res.data.code===200){
          getAllEquipment({
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
      this.getAllEquipment()
      this.currentPage = params
    },
    getByKeyword(KeywordEquipment) {
      totalEquipmentFuzzy({
        keyWord:KeywordEquipment
      }).then(res=>{
        this.total = res.data.dataTotal
      }),
          getByKeywordEquipment({
            keyWord:KeywordEquipment,
            page:0,
            size:10
          }).then(res=>{
            this.tableData = res.data
          }).catch(err=>{
            console.log(err.message)
          })

    },
    getAllEquipment(){
      getAllEquipment({
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
.equipment-manage {
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

.eq-info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.eq-name {
  font-weight: 700;
  color: var(--admin-text-main);
}

.eq-loc {
  font-size: 12px;
  color: var(--admin-text-dim);
  display: flex;
  align-items: center;
  gap: 6px;
}

.eq-loc i {
  color: var(--admin-primary);
}

.ellipsis {
  max-width: 360px;
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
  .search-input {
    width: 100%;
  }
}
</style>
