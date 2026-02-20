<template>
  <section class="venue-manage a-card">
    <header class="page-header">
      <div class="title-area">
        <h2 class="page-title">
          <i class="el-icon-office-building"></i>
          场地管理
        </h2>
        <p class="page-subtitle">管理场地信息、状态与开放时间</p>
      </div>

      <div class="action-area">
        <el-button type="primary" icon="el-icon-plus" size="small" class="primary-btn" @click="addVenuePage">
          新增场地
        </el-button>

        <div class="search">
          <el-input
            placeholder="搜索场地名称 / 位置..."
            v-model="KeywordVenue"
            size="small"
            class="search-input"
            clearable
            @keyup.enter.native="getByKeyword(KeywordVenue)"
          >
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button type="primary" size="small" class="search-btn" icon="el-icon-search" @click="getByKeyword(KeywordVenue)">
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
          style="width: 100%;"
          class="modern-table">
        <el-table-column type="index" :index="indexMethod" label="序号" width="55" align="center" />
        
        <el-table-column prop="venueName" label="场地名称" min-width="120" />
        
        <el-table-column label="场地类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" effect="plain" type="primary">
              <template v-if="scope.row.venueType == '1'">健身房</template>
              <template v-else-if="scope.row.venueType == '2'">瑜伽室</template>
              <template v-else-if="scope.row.venueType == '3'">游泳池</template>
              <template v-else-if="scope.row.venueType == '4'">篮球场</template>
              <template v-else-if="scope.row.venueType == '5'">羽毛球场</template>
              <template v-else>其他</template>
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="venueLocation" label="场地位置" min-width="150" />
        
        <el-table-column prop="venueCapacity" label="容纳人数" width="90" align="center">
          <template slot-scope="scope">
            <span class="text-main">{{ scope.row.venueCapacity }} 人</span>
          </template>
        </el-table-column>

        <el-table-column label="场地状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.venueState == '1'" type="success" size="mini" effect="dark">可用</el-tag>
            <el-tag v-else-if="scope.row.venueState == '2'" type="warning" size="mini" effect="dark">维护中</el-tag>
            <el-tag v-else-if="scope.row.venueState == '3'" type="info" size="mini" effect="dark">已预订</el-tag>
            <el-tag v-else type="danger" size="mini" effect="dark">不可用</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="openTime" label="开放时间" width="120" align="center" />

        <el-table-column label="已预约时段" min-width="150">
          <template slot-scope="scope">
            <div class="ellipsis">{{ scope.row.bookedSlots || '暂无' }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="venueMessage" label="备注信息" min-width="150">
          <template slot-scope="scope">
            <el-tooltip effect="dark" placement="top" :content="scope.row.venueMessage || '无'">
              <div class="ellipsis">{{ scope.row.venueMessage || '-' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button @click="editVenue(scope.row)" type="primary" size="mini" icon="el-icon-edit" circle plain></el-button>
            <el-button @click="deleteVenue(scope.row.venueNo)" type="danger" size="mini" icon="el-icon-delete" circle plain></el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex;justify-content: center;margin-top: 10px">
        <el-pagination
            background
            layout="prev, pager, next"
            @current-change="changePage"
            :total="total">
        </el-pagination>
      </div>


      <el-dialog
          title="添加场地"
          :visible.sync="dialogVisible2"
          width="40%"
          append-to-body
      >
        <el-form ref="form" :model="sizeForm" label-width="100px" size="mini">
          <el-form-item label="场地名称">
            <el-input v-model="sizeForm.venueName"></el-input>
          </el-form-item>
          <el-form-item label="场地类型">
            <el-select v-model="sizeForm.venueType" placeholder="请选择场地类型">
              <el-option label="健身房" value="1"></el-option>
              <el-option label="瑜伽室" value="2"></el-option>
              <el-option label="游泳池" value="3"></el-option>
              <el-option label="篮球场" value="4"></el-option>
              <el-option label="羽毛球场" value="5"></el-option>
              <el-option label="其他" value="6"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="场地位置">
            <el-input v-model="sizeForm.venueLocation"></el-input>
          </el-form-item>
          <el-form-item label="容纳人数">
            <el-input v-model="sizeForm.venueCapacity" type="number"></el-input>
          </el-form-item>
          <el-form-item label="场地状态">
            <el-select v-model="sizeForm.venueState" placeholder="请选择场地状态">
              <el-option label="可用" value="1"></el-option>
              <el-option label="维护中" value="2"></el-option>
              <el-option label="已预订" value="3"></el-option>
              <el-option label="不可用" value="4"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="开放时间">
            <el-input v-model="sizeForm.openTime" placeholder="例如: 08:00-22:00"></el-input>
          </el-form-item>
          <el-form-item label="备注信息">
            <el-input v-model="sizeForm.venueMessage" type="textarea" :rows="3"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible2 = false">取 消</el-button>
          <el-button type="primary" @click="addVenue">确 定</el-button>
        </span>
      </el-dialog>

      <el-dialog
          title="修改场地信息"
          :visible.sync="dialogVisible"
          width="40%"
          append-to-body
      >
        <el-form ref="form" :model="sizeForm" label-width="100px" size="mini">
          <el-form-item label="场地名称">
            <el-input v-model="sizeForm.venueName"></el-input>
          </el-form-item>
          <el-form-item label="场地类型">
            <el-select v-model="sizeForm.venueType" placeholder="请选择场地类型">
              <el-option label="健身房" value="1"></el-option>
              <el-option label="瑜伽室" value="2"></el-option>
              <el-option label="游泳池" value="3"></el-option>
              <el-option label="篮球场" value="4"></el-option>
              <el-option label="羽毛球场" value="5"></el-option>
              <el-option label="其他" value="6"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="场地位置">
            <el-input v-model="sizeForm.venueLocation"></el-input>
          </el-form-item>
          <el-form-item label="容纳人数">
            <el-input v-model="sizeForm.venueCapacity" type="number"></el-input>
          </el-form-item>
          <el-form-item label="场地状态">
            <el-select v-model="sizeForm.venueState" placeholder="请选择场地状态">
              <el-option label="可用" value="1"></el-option>
              <el-option label="维护中" value="2"></el-option>
              <el-option label="已预订" value="3"></el-option>
              <el-option label="不可用" value="4"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="开放时间">
            <el-input v-model="sizeForm.openTime" placeholder="例如: 08:00-22:00"></el-input>
          </el-form-item>
          <el-form-item label="备注信息">
            <el-input v-model="sizeForm.venueMessage" type="textarea" :rows="3"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="updateVenue">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </section>
</template>

<script>
import moment from 'moment';
import {addVenue, getAllVenue, deleteVenue, updateVenue, totalVenue, getByKeywordVenue, totalVenueFuzzy} from "@/api/allApi";

export default {
  name:"VenueManage",
  data() {
    return {
      dialogFormVisible: false,
      tableData: [],
      sizeForm:{

      },
      dialogVisible: false,
      dialogVisible2: false,
      /*分页*/
      pageSize:10,
      currentPage:1,
      total:0,
      searchValue:'',
      KeywordVenue:''
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
    this.getAllVenueData()
    this.getTotalVenue()
  },
  methods: {
    goBack() {
      this.$router.back();
    },
    editVenue(value) {
      this.sizeForm = value
      this.dialogVisible = true
    },
    addVenuePage(){
      this.sizeForm = {}
      this.dialogVisible2 = true
    },
    addVenue(){
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      addVenue({
        venueName:this.sizeForm.venueName,
        venueType:this.sizeForm.venueType,
        venueLocation:this.sizeForm.venueLocation,
        venueCapacity:this.sizeForm.venueCapacity,
        venueState:this.sizeForm.venueState,
        openTime:this.sizeForm.openTime,
        venueMessage:this.sizeForm.venueMessage,
      }).then(res=>{
        if(res.data.code===200){
          //刷新表格
          this.getAllVenueData()
          this.$message.success(res.data.message)
        }else{
          this.$message.error(res.data.message)
        }
      }).catch(err=>{
        console.log(err.message)
        this.$message.error('添加失败')
      })
      this.sizeForm = {},
      this.dialogVisible2 = false
    },

    deleteVenue(venueNo){
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      this.$confirm('此操作将永久删除该场地信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        //确定 发送ajax请求
        deleteVenue({venueNo:venueNo}).then(res=>{
          if(res.data.code===200){
            //刷新表格
            this.getAllVenueData()
            this.getTotalVenue()
            this.$message.success('删除成功')
          }else{
            this.$message.error(res.data.message)
          }
        }).catch(err=>{
          console.log(err.message)
          this.$message.error('删除失败')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    updateVenue() {
      let currentPage = this.currentPage
      let pageSize = this.pageSize
      updateVenue({
        venueName:this.sizeForm.venueName,
        venueType:this.sizeForm.venueType,
        venueLocation:this.sizeForm.venueLocation,
        venueCapacity:this.sizeForm.venueCapacity,
        venueState:this.sizeForm.venueState,
        openTime:this.sizeForm.openTime,
        venueMessage:this.sizeForm.venueMessage,
        venueNo:this.sizeForm.venueNo
      }).then(res=>{
        if(res.data.code===200){
          //刷新表格
          this.getAllVenueData()
          this.$message.success(res.data.message)
        }else{
          this.$message.error(res.data.message)
        }
      }).catch(err=>{
        console.log(err.message)
        this.$message.error('修改失败')
      })
      this.dialogVisible = false
    },
    changePage(params) {
      this.currentPage = (params-1) * this.pageSize
      this.getAllVenueData()
      this.currentPage = params
    },
    getByKeyword(KeywordVenue) {
      totalVenueFuzzy({
        keyWord:KeywordVenue
      }).then(res=>{
        this.total = res.data.dataTotal
      }),
          getByKeywordVenue({
            keyWord:KeywordVenue,
            page:0,
            size:10
          }).then(res=>{
            this.tableData = res.data
          }).catch(err=>{
            console.log(err.message)
          })

    },
    //获取数据方法
    getAllVenueData(){
      getAllVenue({
        page:this.currentPage,
        size:this.pageSize
      }).then(res=>{
        this.tableData = res.data
      }).catch(err=>{
        console.log(err.message)
      })
    },
    getTotalVenue(){
      totalVenue().then(res=>{
        this.total = res.data.dataTotal
      })
    }
  },
}
</script>


<style scoped>
.venue-manage {
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
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
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
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
}

.search-input {
  width: 240px;
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
  .page-header {
    flex-direction: column;
    gap: 20px;
  }
  .action-area {
    width: 100%;
    align-items: flex-start;
  }
  .search-input {
    width: 100%;
  }
}
</style>
