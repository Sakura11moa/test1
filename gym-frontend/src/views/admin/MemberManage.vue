<template>
  <div class="member-manage-container">
    <!-- 页面头部 -->
    <div class="page-header-section">
      <div class="header-content">
        <div class="page-title">
          <i class="el-icon-user-solid"></i>
          <h1>会员管理</h1>
          <span class="page-subtitle">管理系统内所有会员信息</span>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ total }}</span>
            <span class="stat-label">总会员数</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ activeMembers }}</span>
            <span class="stat-label">活跃会员</span>
          </div>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="left-actions">
          <el-button
            type="primary"
            icon="el-icon-download"
            @click="memberExport"
            class="action-btn"
          >
            导出数据
          </el-button>
          <el-button
            type="success"
            icon="el-icon-upload2"
            @click="memberImportDialog = true"
            class="action-btn"
          >
            导入数据
          </el-button>
          <el-button
            type="danger"
            icon="el-icon-delete"
            :disabled="selectedMembers.length === 0"
            @click="batchDelete"
            class="action-btn"
          >
            批量删除 ({{ selectedMembers.length }})
          </el-button>
        </div>

        <!-- 增强搜索区域 -->
        <div class="search-section">
          <div class="search-wrapper">
            <el-input
              v-model="KeywordMember"
              placeholder="搜索会员姓名、手机号或用户名..."
              class="search-input"
              clearable
              @keyup.enter="getByKeyword(KeywordMember)"
            >
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
            <el-button
              type="primary"
              icon="el-icon-search"
              @click="getByKeyword(KeywordMember)"
              class="search-btn"
            >
              搜索
            </el-button>
          </div>

          <!-- 高级筛选 -->
          <el-popover
            placement="bottom"
            width="300"
            trigger="click"
            v-model="showAdvancedFilter"
          >
            <div class="advanced-filter">
              <div class="filter-item">
                <label>会员等级:</label>
                <el-select v-model="filterLevel" placeholder="选择等级" clearable>
                  <el-option label="普通用户" value="0"></el-option>
                  <el-option label="体验VIP" value="1"></el-option>
                  <el-option label="包月VIP" value="2"></el-option>
                  <el-option label="包季VIP" value="3"></el-option>
                  <el-option label="包年VIP" value="4"></el-option>
                </el-select>
              </div>
              <div class="filter-item">
                <label>性别:</label>
                <el-select v-model="filterGender" placeholder="选择性别" clearable>
                  <el-option label="男" value="M"></el-option>
                  <el-option label="女" value="F"></el-option>
                </el-select>
              </div>
              <div class="filter-actions">
                <el-button size="mini" @click="resetFilters">重置</el-button>
                <el-button type="primary" size="mini" @click="applyFilters">应用</el-button>
              </div>
            </div>
            <el-button slot="reference" icon="el-icon-s-operation" class="filter-btn">
              高级筛选
            </el-button>
          </el-popover>
        </div>
      </div>
    </div>



      <!-- 数据表格 -->
      <div class="table-container">
        <el-table
          :data="tableData"
          stripe
          style="width: 100%;"
          :header-cell-style="{ background: 'linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-secondary) 100%)', color: '#fff', border: 'none' }"
          :row-style="{ border: 'none' }"
          @selection-change="handleSelectionChange"
          class="modern-table"
        >
          <el-table-column
            type="selection"
            width="50"
            align="center"
          >
          </el-table-column>
          <el-table-column
            type="index"
            :index="indexMethod"
            label="序号"
            width="60"
            align="center"
          >
          </el-table-column>

          <el-table-column label="姓名" width="120">
            <template slot-scope="scope">
              <span class="user-name">{{ scope.row.memberName }}</span>
            </template>
          </el-table-column>

          <el-table-column label="用户名" width="140">
            <template slot-scope="scope">
              <span class="user-username">@{{ scope.row.memberUsername }}</span>
            </template>
          </el-table-column>

          <el-table-column label="手机号" width="130" align="center">
            <template slot-scope="scope">
              <span class="info-item"><i class="el-icon-mobile-phone"></i> {{ scope.row.memberPhone }}</span>
            </template>
          </el-table-column>

          <el-table-column label="性别/年龄" width="120" align="center">
            <template slot-scope="scope">
              <span class="info-item">{{ scope.row.memberGender == 'F' ? '女' : '男' }} / {{ scope.row.memberAge }}岁</span>
            </template>
          </el-table-column>

          <el-table-column label="身高/体重" width="140" align="center">
            <template slot-scope="scope">
              <span class="info-item">{{ scope.row.memberHeight }}cm / {{ scope.row.memberWeight }}kg</span>
            </template>
          </el-table-column>

          <el-table-column label="等级" width="120" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="getMemberLevelType(scope.row.memberPower)"
                size="small"
                effect="light"
              >
                {{ getMemberLevelText(scope.row.memberPower) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="积分" width="100" align="center">
            <template slot-scope="scope">
              <span class="integral-value">{{ scope.row.memberIntegral }}</span>
            </template>
          </el-table-column>

          <el-table-column label="余额" width="120" align="center">
            <template slot-scope="scope">
              <span class="balance-value">¥{{ scope.row.memberChange }}</span>
            </template>
          </el-table-column>

          <el-table-column label="办卡时间" width="120" align="center">
            <template slot-scope="scope">
              <span class="card-time">{{ scope.row.cardTime | dataFormat }}</span>
            </template>
          </el-table-column>

          <el-table-column label="课时" width="100" align="center">
            <template slot-scope="scope">
              <span class="card-class">{{ scope.row.cardClass }} 课时</span>
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column
            label="操作"
            fixed="right"
            width="180"
            align="center"
          >
            <template slot-scope="scope">
              <div class="action-buttons">
                <el-tooltip content="编辑会员" placement="top">
                  <el-button
                    type="primary"
                    size="mini"
                    icon="el-icon-edit"
                    @click="editMember(scope.row)"
                    circle
                    plain
                  ></el-button>
                </el-tooltip>
                <el-tooltip content="续卡" placement="top">
                  <el-button
                    type="warning"
                    size="mini"
                    icon="el-icon-date"
                    @click="openRenewDialog(scope.row)"
                    circle
                    plain
                  ></el-button>
                </el-tooltip>
                <el-tooltip content="退课" placement="top">
                  <el-button
                    type="info"
                    size="mini"
                    icon="el-icon-refund"
                    @click="openRefundDialog(scope.row)"
                    circle
                    plain
                  ></el-button>
                </el-tooltip>
                <el-tooltip content="删除会员" placement="top">
                  <el-button
                    type="danger"
                    size="mini"
                    icon="el-icon-delete"
                    @click="deleteMember(scope.row.memberNo)"
                    circle
                    plain
                  ></el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <div class="pagination-info">
          <span>共 {{ total }} 条记录，当前显示第 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, total) }} 条</span>
        </div>
        <el-pagination
          background
          layout="prev, pager, next, sizes"
          @current-change="changePage"
          @size-change="handleSizeChange"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :current-page="currentPage"
          class="modern-pagination"
        >
        </el-pagination>
      </div>

      <el-dialog
          title="导入Excel文件"
          :visible.sync="memberImportDialog"
          width="30%"
          :before-close="handleClose"
          append-to-body>

        <el-upload
            class="upload-demo"
            action
            drag
            :http-request="uploadFile"
            ref="upload"
            :on-exceed="handleExceed"
            :before-upload="beforeUpload"
            :show-file-list="true"
            :headers="headers"
            multiple>
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">只能上传Excel文件</div>
        </el-upload>

        <span slot="footer" class="dialog-footer">
          <el-button @click="memberImportDialog = false">取 消</el-button>
          <el-button type="primary" @click="memberImport">确 定</el-button>
        </span>
      </el-dialog>


      <!-- 编辑会员弹窗 -->
      <el-dialog
        title="编辑会员信息"
        :visible.sync="dialogVisible"
        width="600px"
        class="modern-dialog"
        :close-on-click-modal="false"
        append-to-body
      >
        <div class="dialog-content">
          <div class="member-avatar-section">
            <div class="avatar-upload">
              <img :src="getUserAvatar(sizeForm)" class="member-avatar" alt="会员头像">
              <div class="avatar-overlay">
                <i class="el-icon-camera"></i>
                <span>更换头像</span>
              </div>
            </div>
            <div class="member-basic-info">
              <h3>{{ sizeForm.memberName || '会员姓名' }}</h3>
              <p>@{{ sizeForm.memberUsername }}</p>
            </div>
          </div>

          <el-form
            ref="editForm"
            :model="sizeForm"
            label-width="100px"
            class="edit-form"
          >
            <div class="form-row">
              <el-form-item label="姓名" prop="memberName">
                <el-input
                  v-model="sizeForm.memberName"
                  placeholder="请输入姓名"
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item label="用户名" prop="memberUsername">
                <el-input
                  v-model="sizeForm.memberUsername"
                  placeholder="请输入用户名"
                  clearable
                  disabled
                ></el-input>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="性别" prop="memberGender">
                <el-select v-model="sizeForm.memberGender" placeholder="请选择性别">
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
              <el-form-item label="年龄" prop="memberAge">
                <el-input
                  v-model="sizeForm.memberAge"
                  placeholder="请输入年龄"
                  type="number"
                ></el-input>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="手机号" prop="memberPhone">
                <el-input
                  v-model="sizeForm.memberPhone"
                  placeholder="请输入手机号"
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item label="会员等级" prop="memberPower">
                <el-select v-model="sizeForm.memberPower" placeholder="请选择会员等级">
                  <el-option label="普通用户" value="0">
                    <el-tag type="info" size="mini">普通用户</el-tag>
                  </el-option>
                  <el-option label="体验VIP" value="1">
                    <el-tag size="mini">体验VIP</el-tag>
                  </el-option>
                  <el-option label="包月VIP" value="2">
                    <el-tag type="success" size="mini">包月VIP</el-tag>
                  </el-option>
                  <el-option label="包季VIP" value="3">
                    <el-tag type="warning" size="mini">包季VIP</el-tag>
                  </el-option>
                  <el-option label="包年VIP" value="4">
                    <el-tag type="danger" size="mini">包年VIP</el-tag>
                  </el-option>
                </el-select>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="身高(cm)" prop="memberHeight">
                <el-input
                  v-model="sizeForm.memberHeight"
                  placeholder="请输入身高"
                  type="number"
                ></el-input>
              </el-form-item>
              <el-form-item label="体重(kg)" prop="memberWeight">
                <el-input
                  v-model="sizeForm.memberWeight"
                  placeholder="请输入体重"
                  type="number"
                ></el-input>
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="积分" prop="memberIntegral">
                <el-input-number
                  v-model="sizeForm.memberIntegral"
                  :min="0"
                  :precision="0"
                  controls-position="right"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="余额(元)" prop="memberChange">
                <el-input-number
                  v-model="sizeForm.memberChange"
                  :min="0"
                  :precision="2"
                  :step="0.01"
                  controls-position="right"
                ></el-input-number>
              </el-form-item>
            </div>
          </el-form>
        </div>

        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="medium">取消</el-button>
          <el-button
            type="primary"
            @click="updateMember"
            size="medium"
            :loading="updating"
          >
            保存修改
          </el-button>
        </div>
      </el-dialog>

      <!-- 续卡弹窗 -->
      <el-dialog
        title="会员续卡"
        :visible.sync="renewDialogVisible"
        width="500px"
        class="modern-dialog"
        :close-on-click-modal="false"
        append-to-body
      >
        <div class="renew-dialog-content" v-if="renewForm.memberNo">
          <!-- 会员信息卡片 -->
          <div class="member-info-card">
            <div class="member-avatar-small">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="member-details">
              <h4>{{ renewForm.memberName }}</h4>
              <p>手机号: {{ renewForm.memberPhone }}</p>
              <p>当前余额: ¥{{ renewForm.memberChange }}</p>
            </div>
          </div>

          <!-- 卡状态信息 -->
          <div class="card-status-info">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="status-item">
                  <span class="status-label">到期时间</span>
                  <span class="status-value">{{ renewForm.expireTime | dataFormat }}</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="status-item">
                  <span class="status-label">剩余天数</span>
                  <span class="status-value" :class="{ 'text-danger': renewForm.remainingDays <= 7, 'text-expired': renewForm.remainingDays <= 0 }">
                    {{ renewForm.remainingDays <= 0 ? '已过期' : renewForm.remainingDays + ' 天' }}
                  </span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="status-item">
                  <span class="status-label">卡状态</span>
                  <el-tag :type="renewForm.remainingDays > 0 ? 'success' : 'danger'" size="small">
                    {{ renewForm.remainingDays > 0 ? '有效' : '失效' }}
                  </el-tag>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 续卡表单 -->
          <el-form
            ref="renewFormRef"
            :model="renewForm"
            label-width="100px"
            class="renew-form"
            :rules="renewRules"
          >
            <el-form-item label="选择卡项" prop="cardTypeId">
              <el-select
                v-model="renewForm.cardTypeId"
                placeholder="请选择续卡类型"
                class="card-type-select"
                @change="handleCardTypeChange"
              >
                <el-option
                  v-for="card in cardTypeList"
                  :key="card.cardTypeId"
                  :label="`${card.name} - ¥${Number(card.price || 0).toFixed(2)}`"
                  :value="card.cardTypeId"
                >
                  <span style="float: left">{{ card.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">
                    ¥{{ Number(card.price || 0).toFixed(2) }} / {{ card.days || 0 }}天
                  </span>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="支付方式" prop="payChannel">
              <el-radio-group v-model="renewForm.payChannel">
                <el-radio label="BALANCE">余额支付</el-radio>
                <el-radio label="CASH">现金支付</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="续费时长">
              <div class="renew-summary">
                <span class="days-badge">+ {{ Number(selectedCardType?.days || 0) }} 天</span>
                <span class="price-badge">¥ {{ Number(selectedCardType?.price || 0).toFixed(2) }}</span>
              </div>
            </el-form-item>

            <el-form-item label="备注">
              <el-input
                v-model="renewForm.remark"
                type="textarea"
                :rows="2"
                placeholder="请输入备注信息（可选）"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>

        <div slot="footer" class="dialog-footer">
          <el-button @click="renewDialogVisible = false" size="medium">取消</el-button>
          <el-button
            type="warning"
            @click="submitRenewCard"
            size="medium"
            :loading="renewing"
            :disabled="!renewForm.cardTypeId"
          >
            确认续卡
          </el-button>
        </div>
      </el-dialog>

      <!-- 退课弹窗 -->
      <el-dialog
        title="会员退课"
        :visible.sync="refundDialogVisible"
        width="700px"
        class="modern-dialog"
        :close-on-click-modal="false"
        append-to-body
      >
        <div class="refund-dialog-content" v-if="refundForm.memberNo">
          <!-- 会员信息卡片 -->
          <div class="member-info-card">
            <div class="member-avatar-small">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="member-details">
              <h4>{{ refundForm.memberName }}</h4>
              <p>手机号: {{ refundForm.memberPhone }}</p>
            </div>
          </div>

          <!-- 课程选择 -->
          <el-form label-width="100px" class="refund-form">
            <el-form-item label="选择课程">
              <el-select
                v-model="refundForm.courseNo"
                placeholder="请选择要退的课程"
                class="course-select"
                @change="handleCourseChange"
              >
                <el-option
                  v-for="course in memberCoursePackages"
                  :key="course.courseNo"
                  :label="`${course.courseName} - 剩余${course.remainTimes}次 | 单价¥${course.unitPrice}`"
                  :value="course.courseNo"
                >
                  <span style="float: left">{{ course.courseName }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">
                    剩余 {{ course.remainTimes }} 次 | 单价 ¥{{ course.unitPrice }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>

            <!-- 已选课程信息 -->
            <div v-if="selectedCourse" class="selected-course-info">
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="info-item">
                    <span class="label">剩余次数</span>
                    <span class="value">{{ selectedCourse.remainTimes }} 次</span>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="info-item">
                    <span class="label">单价</span>
                    <span class="value">¥{{ selectedCourse.unitPrice }}</span>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="info-item">
                    <span class="label">待分摊金额</span>
                    <span class="value">¥{{ selectedCourse.deferredRemaining }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>

            <el-form-item label="退课次数" v-if="selectedCourse">
              <el-slider
                v-model="refundForm.refundTimes"
                :min="1"
                :max="refundForm.remainTimes"
                :step="1"
                show-stops
                @change="calcRefundAmount"
              >
              </el-slider>
              <div class="refund-times-display">
                退课 <span class="highlight">{{ refundForm.refundTimes }}</span> 次
              </div>
            </el-form-item>

            <el-form-item label="退款金额" v-if="selectedCourse">
              <div class="refund-amount-display">
                <span class="currency">¥</span>
                <span class="amount">{{ refundForm.refundAmount }}</span>
                <span class="channel">(退至会员余额)</span>
              </div>
            </el-form-item>

            <el-form-item label="退课原因">
              <el-input
                v-model="refundForm.refundReason"
                type="textarea"
                :rows="2"
                placeholder="请输入退课原因（可选）"
              ></el-input>
            </el-form-item>
          </el-form>

          <!-- 退款提示 -->
          <div class="refund-tip" v-if="selectedCourse && refundForm.refundTimes > 0">
            <i class="el-icon-info"></i>
            确认退课 {{ refundForm.refundTimes }} 节，共计 <span class="amount">¥{{ refundForm.refundAmount }}</span>，
            将退还至会员储值余额
          </div>
        </div>

        <div slot="footer" class="dialog-footer">
          <el-button @click="refundDialogVisible = false" size="medium">取消</el-button>
          <el-button
            type="primary"
            @click="submitRefund"
            size="medium"
            :loading="refundLoading"
            :disabled="!selectedCourse || refundForm.refundTimes <= 0"
          >
            确认退课
          </el-button>
        </div>
      </el-dialog>
    </el-card>

  </div>

</template>

<script>
import moment from 'moment';
import {
  deleteMember,
  getAllMember,
  getByKeywordMember,
  getCardTypes,
  getMemberCardStatus,
  memberExport,
  renewMemberCard,
  totalMember,
  totalMemberFuzzy,
  updateMember,
  getMemberCoursePackages,
  processCourseRefund
} from "@/api/allApi";
import axios from "axios";

export default {
  name:"MemberManage",
  data() {
    return {
      memberImportDialog: false,
      tableData: [],
      sizeForm: {},
      dialogVisible: false,
      updating: false,
      adminInfo: {}, // 管理员信息

      /* 分页 */
      pageSize: 10,
      currentPage: 1,
      total: 0,
      activeMembers: 0,

      /* 搜索和筛选 */
      KeywordMember: '',
      showAdvancedFilter: false,
      filterLevel: '',
      filterGender: '',

      /* 选择 */
      selectedMembers: [],

      /* 文件上传 */
      fileType: ["xls", "xlsx"],
      fileLimit: 1,
      headers: { "Content-Type": "multipart/form-data" },
      memberFile: {},
      fileList: [],

      /* 默认头像列表 */
      defaultAvatars: [
        'avatar.png',
        'temp.png'
      ],

      /* 续卡相关 */
      renewDialogVisible: false,
      renewing: false,
      cardTypeList: [], // 卡项列表
      renewForm: {
        memberNo: null,
        memberName: '',
        memberPhone: '',
        memberChange: 0,
        expireTime: null,
        remainingDays: 0,
        cardStatus: 1,
        cardTypeId: null,
        payChannel: 'BALANCE',
        remark: ''
      },
      renewRules: {
        cardTypeId: [
          { required: true, message: '请选择续卡卡项', trigger: 'change' }
        ],
        payChannel: [
          { required: true, message: '请选择支付方式', trigger: 'change' }
        ]
      },

      /* 退课相关 */
      refundDialogVisible: false,
      refundLoading: false,
      memberCoursePackages: [],
      selectedCourse: null,
      refundForm: {
        memberNo: null,
        memberName: '',
        memberPhone: '',
        courseNo: null,
        courseName: '',
        purchaseNo: null,
        unitPrice: 0,
        remainTimes: 0,
        refundTimes: 1,
        refundAmount: 0,
        refundReason: ''
      }
    }
  },
  computed: {
    indexMethod(){
      return (this.currentPage-1) *10 +1
    },
    // 计算属性：获取选中的卡项信息
    selectedCardType() {
      if (!this.renewForm.cardTypeId || this.cardTypeList.length === 0) {
        return null;
      }
      return this.cardTypeList.find(card => card.cardTypeId === this.renewForm.cardTypeId);
    }
  },
  filters:{
    dataFormat(value){
      if (!value) return '-'
      return moment(value).format("YYYY-MM-DD")
    }
  },
  created() {
    // 获取管理员信息
    this.adminInfo = JSON.parse(window.localStorage.getItem("access-admin")) || {};
  },
  mounted() {
    getAllMember({
      page:0,
      size:10
    }).then(res=>{
      if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
        this.tableData = res.data.data
      }
    }).catch(err=>{
      console.log(err.message)
    })

    totalMember().then(res=>{
      this.total = res.data.dataTotal
    })
  },
  methods: {
    /* 工具方法 */
    getUserAvatar(member) {
      // 这里可以根据会员信息返回对应的头像
      // 暂时使用默认头像
      return require(`@/assets/images/avatar.png`);
    },

    getMemberLevelType(level) {
      const types = {
        '0': 'info',
        '1': '',
        '2': 'success',
        '3': 'warning',
        '4': 'danger'
      };
      return types[level] || 'info';
    },

    getMemberLevelText(level) {
      const texts = {
        '0': '普通用户',
        '1': '体验VIP',
        '2': '包月VIP',
        '3': '包季VIP',
        '4': '包年VIP'
      };
      return texts[level] || '普通用户';
    },

    /* 选择处理 */
    handleSelectionChange(selection) {
      this.selectedMembers = selection;
    },

    /* 批量删除 */
    async batchDelete() {
      if (this.selectedMembers.length === 0) {
        this.$message.warning('请先选择要删除的会员');
        return;
      }

      try {
        await this.$confirm(
          `确定要删除选中的 ${this.selectedMembers.length} 个会员吗？此操作不可恢复。`,
          '批量删除确认',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        // 批量删除逻辑
        const deletePromises = this.selectedMembers.map(member =>
          this.$http.delete(`/member/${member.memberNo}`)
        );

        await Promise.all(deletePromises);

        this.$message.success('批量删除成功');
        this.refreshData();

      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('批量删除失败');
        }
      }
    },

    /* 筛选相关 */
    resetFilters() {
      this.filterLevel = '';
      this.filterGender = '';
      this.applyFilters();
    },

    applyFilters() {
      this.showAdvancedFilter = false;
      // 重新获取数据
      this.getAllMember();
    },

    /* 分页处理 */
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.getAllMember();
    },

    /* 刷新数据 */
    refreshData() {
      this.getAllMember();
      // 正确调用导入的 totalMember 函数
      totalMember().then(res=>{
        this.total = res.data.dataTotal
      }).catch(err => {
        console.error('获取会员总数失败:', err);
      });
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
    },
    editMember(value) {
      this.sizeForm = value
      this.sizeForm.memberPassword = ''
      this.dialogVisible = true
    },
    memberExport() {
      memberExport().then(res=>{
        window.open("http://localhost:9090/memberExport")
      }).catch(err=>{
        console.log(err.message)
      })
    },
    //上传文件之前
    beforeUpload(file){
      if (file.type != "" || file.type != null || file.type != undefined){
        //截取文件的后缀，判断文件类型
        const FileExt = file.name.replace(/.+\./, "").toLowerCase();
        //如果文件类型不在允许上传的范围内
        if(this.fileType.includes(FileExt)){
          return true;
        }
        else {
          this.$message.error("上传文件格式不正确!");
          return false;
        }
      }
    },
  //超出文件个数的回调
    handleExceed(){
      this.$message({
        type:'warning',
        message:'超出最大上传文件数量的限制！'
      });return
    },
  //上传文件的事件
    uploadFile(item){
      //上传文件的需要formdata类型;所以要转
      let FormDatas = new FormData()
      FormDatas.append('file',item.file);
      this.memberFile = FormDatas
    },
    memberImport() {
      axios({
        method: 'post',
        url: 'http://localhost:9090/memberImport',
        headers: this.headers,
        timeout: 30000,
        data: this.memberFile
      }).then(res => {
        alert(res.data.message)
      }),
          this.$refs.upload.clearFiles()

          totalMember().then(res=>{
            this.total = res.data.dataTotal
          })
          getAllMember({
            page: 0,
            size: 10
          }).then(res => {
            if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
              this.tableData = res.data.data
            }
          })
      this.memberImportDialog = false

    },

    deleteMember(memberNo){
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        //确定 发送ajax请求
        deleteMember({memberNo:memberNo}).then(res=>{
          if(res.data.code===200){
            totalMember().then(res=>{
              this.total = res.data.dataTotal
            })
            //刷新表格
            getAllMember({
              page:0,
              size:10
            }).then(res=>{
              if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
                this.tableData = res.data.data
              }
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
    async updateMember() {
      try {
        this.updating = true;

        const result = await updateMember({
          memberUsername: this.sizeForm.memberUsername,
          memberName: this.sizeForm.memberName,
          memberGender: this.sizeForm.memberGender,
          memberAge: this.sizeForm.memberAge,
          memberPhone: this.sizeForm.memberPhone,
          memberHeight: this.sizeForm.memberHeight,
          memberWeight: this.sizeForm.memberWeight,
          memberIntegral: this.sizeForm.memberIntegral,
          memberChange: this.sizeForm.memberChange,
          memberPower: this.sizeForm.memberPower,
          memberNo: this.sizeForm.memberNo,
        });

        if (result.data.code === 200) {
          this.$message.success('会员信息更新成功');
          this.refreshData();
          this.dialogVisible = false;
        } else {
          this.$message.error(result.data.message || '更新失败');
        }
      } catch (err) {
        console.log(err.message);
        this.$message.error('网络错误，请稍后重试');
      } finally {
        this.updating = false;
      }
    },
    changePage(params) {
      this.currentPage = (params-1) * this.pageSize
      this.getAllMember()
      this.currentPage = params
    },
    getByKeyword(KeywordMember) {
      totalMemberFuzzy({
        keyWord:KeywordMember
      }).then(res=>{
        this.total = res.data.dataTotal
      }),
      getByKeywordMember({
        keyWord:KeywordMember,
        page:0,
        size:10
      }).then(res=>{
        this.tableData = res.data
      }).catch(err=>{
        console.log(err.message)
      })

    },
    //获取数据方法
    getAllMember(){
      getAllMember({
        page:this.currentPage,
        size:this.pageSize
      }).then(res=>{
        if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
          this.tableData = res.data.data
        }
      }).catch(err=>{
        console.log(err.message)
      })
    },

    /* ========== 续卡相关方法 ========== */

    // 打开续卡弹窗
    async openRenewDialog(member) {
      // 重置表单
      this.renewForm = {
        memberNo: member.memberNo,
        memberName: member.memberName,
        memberPhone: member.memberPhone,
        memberChange: member.memberChange,
        expireTime: null,
        remainingDays: 0,
        cardStatus: 1,
        cardTypeId: null,
        payChannel: 'BALANCE',
        remark: ''
      };

      // 获取卡项列表
      if (this.cardTypeList.length === 0) {
        try {
          const res = await getCardTypes();
          console.log('卡项数据:', res); // 调试日志
          if (res.data && res.data.code === 200) {
            // 确保每条数据都有正确的字段
            this.cardTypeList = (res.data.data || []).map(card => ({
              ...card,
              cardTypeId: card.cardTypeId || card.id || 0,
              name: card.name || '未知卡项',
              price: card.price !== undefined ? Number(card.price) : 0,
              days: card.days !== undefined ? Number(card.days) : 0
            }));
          } else {
            this.$message.error('获取卡项列表失败');
          }
        } catch (err) {
          console.error('获取卡项列表失败:', err);
          this.$message.error('获取卡项列表失败');
        }
      }

      // 获取会员卡状态
      try {
        const statusRes = await getMemberCardStatus({ memberNo: member.memberNo });
        console.log('会员卡状态API返回:', statusRes.data);
        if (statusRes.data && statusRes.data.code === 200) {
          this.renewForm.expireTime = statusRes.data.expireTime || null;
          this.renewForm.remainingDays = statusRes.data.daysLeft || 0;
          this.renewForm.cardStatus = statusRes.data.cardStatus || 1;
          console.log('更新后的卡状态:', this.renewForm.expireTime, this.renewForm.remainingDays, this.renewForm.cardStatus);
        }
      } catch (err) {
        console.error('获取会员卡状态失败:', err);
      }

      this.renewDialogVisible = true;
    },

    // 卡项选择变化
    handleCardTypeChange(cardTypeId) {
      // 可以在这里添加逻辑，比如检查余额是否足够
      const selectedCard = this.selectedCardType;
      if (selectedCard && this.renewForm.payChannel === 'BALANCE') {
        if (this.renewForm.memberChange < selectedCard.price) {
          this.$message.warning('会员余额不足，请选择现金支付或先充值');
        }
      }
    },

    // 提交续卡
    async submitRenewCard() {
      const selectedCard = this.selectedCardType;
      if (!selectedCard) {
        this.$message.warning('请选择续卡卡项');
        return;
      }

      // 余额支付检查
      if (this.renewForm.payChannel === 'BALANCE') {
        if (this.renewForm.memberChange < selectedCard.price) {
          this.$message.warning('会员余额不足，请先充值或选择现金支付');
          return;
        }
      }

      try {
        await this.$confirm(
          `确定要为会员「${this.renewForm.memberName}」续卡「${selectedCard.name}」吗？\n续费金额：¥${selectedCard.price}，增加天数：${selectedCard.days}天`,
          '续卡确认',
          {
            confirmButtonText: '确定续卡',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        this.renewing = true;

        const res = await renewMemberCard({
          memberNo: this.renewForm.memberNo,
          cardTypeId: this.renewForm.cardTypeId,
          payChannel: this.renewForm.payChannel,
          remark: this.renewForm.remark,
          operatorAdminId: this.adminInfo.adminNo || null
        });

        if (res.data.code === 200) {
          this.$message.success('续卡成功！');

          // 关闭续卡弹窗
          this.renewDialogVisible = false;

          // 强制刷新会员列表数据
          await this.refreshMemberList();

          // 刷新统计数据
          this.refreshStats();
        } else {
          this.$message.error(res.data.message || '续卡失败');
        }
      } catch (err) {
        if (err !== 'cancel') {
          console.error('续卡失败:', err);
          this.$message.error(err.response?.data?.message || '续卡失败，请稍后重试');
        }
      } finally {
        this.renewing = false;
      }
    },

    // 专门刷新会员列表的方法
    async refreshMemberList() {
      try {
        // 获取最新数据
        const res = await getAllMember({
          page: this.currentPage,
          size: this.pageSize
        });

        if (res.data && res.data.code === 200 && Array.isArray(res.data.data) && res.data.data.length > 0) {
          this.tableData = res.data.data;
        } else {
          // 如果当前页没数据，回到第一页
          if (this.currentPage > 1) {
            this.currentPage = 1;
            const firstPageRes = await getAllMember({
              page: 0,
              size: this.pageSize
            });
            if (firstPageRes.data && firstPageRes.data.code === 200 && Array.isArray(firstPageRes.data.data)) {
              this.tableData = firstPageRes.data.data;
            } else {
              this.tableData = [];
            }
          } else {
            this.tableData = [];
          }
        }
      } catch (err) {
        console.error('刷新会员列表失败:', err);
        // 失败时尝试获取第一页数据
        try {
          const res = await getAllMember({
            page: 0,
            size: this.pageSize
          });
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.tableData = res.data.data;
          } else {
            this.tableData = [];
          }
          this.currentPage = 1;
        } catch (e) {
          console.error('重试获取会员列表失败:', e);
          this.tableData = [];
        }
      }
    },

    // 刷新统计数据
    async refreshStats() {
      try {
        const totalRes = await totalMember();
        if (totalRes.data) {
          this.total = totalRes.data.dataTotal || 0;
        }
      } catch (err) {
        console.error('获取会员总数失败:', err);
      }
    },

    /* ========== 退课相关方法 ========== */

    // 打开退课弹窗
    async openRefundDialog(member) {
      // 重置表单
      this.refundForm = {
        memberNo: member.memberNo,
        memberName: member.memberName,
        memberPhone: member.memberPhone,
        courseNo: null,
        courseName: '',
        purchaseNo: null,
        unitPrice: 0,
        remainTimes: 0,
        refundTimes: 1,
        refundAmount: 0,
        refundReason: ''
      };
      this.selectedCourse = null;
      this.memberCoursePackages = [];

      // 获取会员课程包列表
      try {
        const res = await getMemberCoursePackages({ memberNo: member.memberNo });
        if (res.data && Array.isArray(res.data)) {
          this.memberCoursePackages = res.data;
          if (this.memberCoursePackages.length === 0) {
            this.$message.warning('该会员没有可退的课程');
            return;
          }
        }
      } catch (err) {
        console.error('获取会员课程包失败:', err);
        this.$message.error('获取会员课程包失败');
        return;
      }

      this.refundDialogVisible = true;
    },

    // 课程选择变化
    handleCourseChange(courseNo) {
      const course = this.memberCoursePackages.find(c => c.courseNo === courseNo);
      if (course) {
        this.selectedCourse = course;
        this.refundForm.courseNo = course.courseNo;
        this.refundForm.courseName = course.courseName;
        this.refundForm.purchaseNo = course.purchaseNo;
        this.refundForm.unitPrice = course.unitPrice;
        this.refundForm.remainTimes = course.remainTimes;
        this.refundForm.refundTimes = 1;
        this.calcRefundAmount();
      }
    },

    // 计算退款金额
    calcRefundAmount() {
      if (this.selectedCourse && this.refundForm.refundTimes > 0) {
        this.refundForm.refundAmount = (this.refundForm.refundTimes * this.refundForm.unitPrice).toFixed(2);
      } else {
        this.refundForm.refundAmount = 0;
      }
    },

    // 提交退课
    async submitRefund() {
      if (!this.selectedCourse) {
        this.$message.warning('请选择要退的课程');
        return;
      }

      if (this.refundForm.refundTimes <= 0) {
        this.$message.warning('退课次数必须大于0');
        return;
      }

      if (this.refundForm.refundTimes > this.refundForm.remainTimes) {
        this.$message.warning('退课次数不能超过剩余次数');
        return;
      }

      try {
        await this.$confirm(
          `确定要为会员「${this.refundForm.memberName}」退课「${this.refundForm.courseName}」吗？\n退课节数：${this.refundForm.refundTimes}节，退款金额：¥${this.refundForm.refundAmount}\n退款将退至会员储值余额`,
          '退课确认',
          {
            confirmButtonText: '确定退课',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        this.refundLoading = true;

        const res = await processCourseRefund({
          memberNo: this.refundForm.memberNo,
          purchaseNo: this.refundForm.purchaseNo,
          courseNo: this.refundForm.courseNo,
          refundTimes: this.refundForm.refundTimes,
          refundAmount: this.refundForm.refundAmount,
          refundReason: this.refundForm.refundReason,
          operatorAdminId: this.adminInfo.adminNo || null
        });

        if (res.data.code === 200) {
          this.$message.success('退课成功！');

          // 关闭退课弹窗
          this.refundDialogVisible = false;

          // 刷新会员列表
          await this.refreshMemberList();

          // 刷新统计数据
          this.refreshStats();
        } else {
          this.$message.error(res.data.message || '退课失败');
        }
      } catch (err) {
        if (err !== 'cancel') {
          console.error('退课失败:', err);
          this.$message.error(err.response?.data?.message || '退课失败，请稍后重试');
        }
      } finally {
        this.refundLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.member-manage-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 页面头部 */
.page-header-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  background: var(--admin-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 32px;
  color: var(--admin-primary);
}

.page-subtitle {
  color: #666;
  font-size: 14px;
  margin-left: 44px;
}

.header-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.left-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 搜索区域 */
.search-section {
  display: flex;
  gap: 12px;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
  flex-wrap: wrap;
  min-width: 320px;
}

.search-wrapper {
  display: flex;
  gap: 8px;
  align-items: center;
  flex: 1;
  min-width: 280px;
}

.search-input {
  width: 300px;
}

.search-input :deep(.el-input__inner) {
  border-radius: 25px;
  border: 2px solid rgba(37, 99, 235, 0.2);
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__inner):focus {
  border-color: var(--admin-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.search-btn {
  border-radius: 25px;
  background: var(--admin-gradient);
  border: none;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateX(2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
}

.filter-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  background: var(--admin-primary);
  color: white;
}

/* 高级筛选弹窗 */
.advanced-filter {
  padding: 16px;
}

.filter-item {
  margin-bottom: 16px;
}

.filter-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

/* 表格容器 */
.table-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 24px;
}

.modern-table :deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

.modern-table :deep(.el-table th) {
  background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-secondary) 100%) !important;
  color: white !important;
  font-weight: 600;
  border: none !important;
  padding: 16px 12px;
}

.modern-table :deep(.el-table td) {
  border: none !important;
  padding: 16px 12px;
}

.modern-table :deep(.el-table--striped .el-table__row--striped td) {
  background: rgba(37, 99, 235, 0.02) !important;
}

.modern-table :deep(.el-table tr:hover td) {
  background: rgba(37, 99, 235, 0.05) !important;
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(37, 99, 235, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.user-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.user-username {
  font-size: 12px;
  color: #666;
}

/* 基本信息 */
.basic-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.info-item i {
  color: var(--admin-primary);
  font-size: 14px;
}

/* 会员状态 */
.member-status {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.status-label {
  color: #666;
  min-width: 32px;
}

.integral-value {
  color: #E6A23C;
  font-weight: 600;
}

.balance-value {
  color: #67C23A;
  font-weight: 600;
}

/* 开卡信息 */
.card-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.card-time,
.card-class {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.card-time i,
.card-class i {
  color: var(--admin-primary);
  font-size: 14px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.modern-pagination :deep(.el-pagination) {
  margin: 0;
}

.modern-pagination :deep(.el-pagination.is-background .el-pager li:not(.disabled).active) {
  background: var(--admin-gradient);
  border: none;
}

.modern-pagination :deep(.el-pagination.is-background .el-pager li:not(.disabled):hover) {
  background: rgba(37, 99, 235, 0.1);
}

/* 现代化弹窗 */
.modern-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-secondary) 100%);
  color: white;
  margin: 0;
  padding: 20px 24px;
  border-bottom: none;
}

.modern-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
}

.modern-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

.modern-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.dialog-content {
  max-height: 600px;
  overflow-y: auto;
}

/* 会员头像区域 */
.member-avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(37, 99, 235, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(37, 99, 235, 0.1);
}

.avatar-upload {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  transition: all 0.3s ease;
}

.avatar-upload:hover {
  transform: scale(1.05);
}

.member-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(37, 99, 235, 0.2);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(37, 99, 235, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  font-size: 12px;
}

.avatar-upload:hover .avatar-overlay {
  opacity: 1;
}

.member-basic-info h3 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 18px;
}

.member-basic-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 编辑表单 */
.edit-form {
  margin-top: 24px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-row .el-form-item {
  flex: 1;
}

.form-row :deep(.el-input__inner),
.form-row :deep(.el-select) {
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.form-row :deep(.el-input__inner):focus,
.form-row :deep(.el-select:focus) {
  border-color: var(--admin-primary);
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

/* 弹窗底部 */
.modern-dialog :deep(.el-dialog__footer) {
  padding: 20px 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(0, 0, 0, 0.02);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
  }

  .action-bar {
    flex-direction: column;
    gap: 16px;
  }

  .search-section {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .member-avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .pagination-wrapper {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}

/* 滚动条样式 */
.table-container::-webkit-scrollbar,
.dialog-content::-webkit-scrollbar {
  width: 6px;
}

.table-container::-webkit-scrollbar-track,
.dialog-content::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.table-container::-webkit-scrollbar-thumb,
.dialog-content::-webkit-scrollbar-thumb {
  background: var(--admin-gradient);
  border-radius: 3px;
}

/* ========== 续卡弹窗样式 ========== */

.renew-dialog-content {
  padding: 0;
}

/* 会员信息卡片 */
.member-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(64, 158, 255, 0.05) 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.member-avatar-small {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-secondary) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.member-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #333;
}

.member-details p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

/* 卡状态信息 */
.card-status-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.status-item {
  text-align: center;
}

.status-label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.status-value {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.status-value.text-danger {
  color: #f56c6c;
}

.status-value.text-expired {
  color: #909399;
  font-weight: 700;
}

/* 续卡表单 */
.renew-form {
  margin-top: 10px;
}

.card-type-select {
  width: 100%;
}

.renew-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.1) 0%, rgba(230, 162, 60, 0.05) 100%);
  border-radius: 8px;
  border: 1px solid rgba(230, 162, 60, 0.2);
}

.days-badge {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.price-badge {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

/* 续卡弹窗头部 */
.renew-dialog-content .el-dialog__header {
  background: linear-gradient(135deg, #e6a23c 0%, #f5a623 100%);
}

.renew-dialog-content .el-dialog__title {
  color: white;
}

.renew-dialog-content .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

/* ========== 退课弹窗样式 ========== */

.refund-dialog-content {
  padding: 0;
}

.refund-form {
  margin-top: 10px;
}

.course-select {
  width: 100%;
}

.selected-course-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.info-item {
  text-align: center;
}

.info-item .label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.info-item .value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.refund-times-display {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

.refund-times-display .highlight {
  color: #409eff;
  font-size: 18px;
  font-weight: 600;
  margin: 0 4px;
}

.refund-amount-display {
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.1) 0%, rgba(103, 194, 58, 0.05) 100%);
  border-radius: 8px;
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.refund-amount-display .currency {
  font-size: 18px;
  color: #67c23a;
}

.refund-amount-display .amount {
  font-size: 32px;
  font-weight: 700;
  color: #67c23a;
  margin: 0 4px;
}

.refund-amount-display .channel {
  font-size: 12px;
  color: #999;
}

.refund-tip {
  margin-top: 16px;
  padding: 12px 16px;
  background: rgba(64, 158, 255, 0.1);
  border-radius: 8px;
  border: 1px solid rgba(64, 158, 255, 0.2);
  color: #409eff;
  font-size: 13px;
}

.refund-tip .amount {
  font-weight: 600;
  color: #f56c6c;
}

/* 退课弹窗头部 */
.refund-dialog-content .el-dialog__header {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
}

.refund-dialog-content .el-dialog__title {
  color: white;
}

.refund-dialog-content .el-dialog__headerbtn .el-dialog__close {
  color: white;
}
</style>