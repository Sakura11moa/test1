import request from "@/utils/request";

// 管理员登录（保持旧名）
export const getAdminPassword = params =>{
    return request({
        url:'/getAdminPassword',
        method:'post',
        params:params
    })
}
// 新增统一命名 adminLogin，供新代码使用
export const adminLogin = params =>{
    return request({
        url:'/getAdminPassword',
        method:'post',
        params:params
    })
}

// 会员登录
export const getMemberPassword = params =>{
    return request({
        url:'/getMemberPassword',
        method:'post',
        params:params
    })
}
/*------------------------------------------------*/
// <查找所有会员信息
export const getAllMember = params =>{
    return request({
        url:'/getAllMember',
        method:'get',
        params:params
    })
}

// <查找所有会员信息
export const memberExport = params =>{
    return request({
        url:'/memberExport',
        method:'get',
        params:params
    })
}


//新增会员
export const addMember = params =>{
    return request({
        url:'/addMember',
        method:'post',
        params:params
    })
}

// 会员注册
export const registerMember = params =>{
    return request({
        url:'/registerMember',
        method:'post',
        params:params
    })
}

// 忘记密码：发送重置验证码（开发态会返回 resetCode）
export const sendMemberResetCode = params => {
    return request({
        url: '/member/sendResetCode',
        method: 'post',
        params: params
    })
}

// 忘记密码：校验验证码并重置密码
export const resetMemberPassword = params => {
    return request({
        url: '/member/resetPassword',
        method: 'post',
        params: params
    })
}

/*删除会员信息*/
export const deleteMember = params =>{
    return request({
        url:'/deleteMember',
        method:'delete',
        params:params
    })
}

/*修改会员信息*/
export const updateMember = params =>{
    return request({
        url:'/updateMember',
        method:'post',
        params:params
    })
}

/*修改会员信息*/
export const updateMemberByMemberNo = params =>{
    return request({
        url:'/updateMemberByMemberNo',
        method:'post',
        params:params
    })
}

/*查找会员表数据总条数*/
export const totalMember = params =>{
    return request({
        url:'/totalMember',
        method:'post',
        params:params
    })
}

// 模糊查询会员表
export const getByKeywordMember = params =>{
    return request({
        url:'/getByKeywordMember',
        method:'post',
        params:params
    })
}

// 模糊查询查找会员表数据总条数
export const totalMemberFuzzy = params =>{
    return request({
        url:'/totalMemberFuzzy',
        method:'post',
        params:params
    })
}

//*----------------------------------------------*/

// 查找所有员工信息
export const getAllEmployee = params =>{
    return request({
        url:'/getAllEmployee',
        method:'get',
        params:params
    })
}
//新增员工
export const addEmployee = params =>{
    return request({
        url:'/addEmployee',
        method:'post',
        params:params
    })
}

/*删除员工信息*/
export const deleteEmployee = params =>{
    return request({
        url:'/deleteEmployee',
        method:'delete',
        params:params
    })
}

/*修改员工信息*/
export const updateEmployee = params =>{
    return request({
        url:'/updateEmployee',
        method:'post',
        params:params
    })
}

/*查找员工表数据总条数*/
export const totalEmployee = params =>{
    return request({
        url:'/totalEmployee',
        method:'post',
        params:params
    })
}

// 模糊查询员工表
export const getByKeywordEmployee = params =>{
    return request({
        url:'/getByKeywordEmployee',
        method:'post',
        params:params
    })
}

// 模糊查询查找员工表数据总条数
export const totalEmployeeFuzzy = params =>{
    return request({
        url:'/totalEmployeeFuzzy',
        method:'post',
        params:params
    })
}

//*----------------------------------------------*/

// 查找所有器材信息
export const getAllEquipment = params =>{
    return request({
        url:'/getAllEquipment',
        method:'get',
        params:params
    })
}
//新增员工
export const addEquipment = params =>{
    return request({
        url:'/addEquipment',
        method:'post',
        params:params
    })
}

/*删除员工信息*/
export const deleteEquipment = params =>{
    return request({
        url:'/deleteEquipment',
        method:'delete',
        params:params
    })
}

/*修改员工信息*/
export const updateEquipment = params =>{
    return request({
        url:'/updateEquipment',
        method:'post',
        params:params
    })
}

/*查找员工表数据总条数*/
export const totalEquipment = params =>{
    return request({
        url:'/totalEquipment',
        method:'post',
        params:params
    })
}

// 模糊查询员工表
export const getByKeywordEquipment = params =>{
    return request({
        url:'/getByKeywordEquipment',
        method:'post',
        params:params
    })
}

// 模糊查询查找员工表数据总条数
export const totalEquipmentFuzzy = params =>{
    return request({
        url:'/totalEquipmentFuzzy',
        method:'post',
        params:params
    })
}

//*----------------------------------------------*/

// 查找所有场地信息
export const getAllVenue = params =>{
    return request({
        url:'/getAllVenue',
        method:'get',
        params:params
    })
}
//新增场地
export const addVenue = params =>{
    return request({
        url:'/addVenue',
        method:'post',
        params:params
    })
}

/*删除场地信息*/
export const deleteVenue = params =>{
    return request({
        url:'/deleteVenue',
        method:'delete',
        params:params
    })
}

/*修改场地信息*/
export const updateVenue = params =>{
    return request({
        url:'/updateVenue',
        method:'post',
        params:params
    })
}

/*查找场地表数据总条数*/
export const totalVenue = params =>{
    return request({
        url:'/totalVenue',
        method:'post',
        params:params
    })
}

// 模糊查询场地表
export const getByKeywordVenue = params =>{
    return request({
        url:'/getByKeywordVenue',
        method:'post',
        params:params
    })
}

// 模糊查询查找场地表数据总条数
export const totalVenueFuzzy = params =>{
    return request({
        url:'/totalVenueFuzzy',
        method:'post',
        params:params
    })
}

// 会员预约场地（成功后将场地状态改为 已预订=3）
// 已废弃，勿用：后端已切换到 v3 预约流程，请使用 `bookVenueV3`
// export const bookVenue = params =>{
//     return request({
//         url:'/bookVenue',
//         method:'post',
//         params:params
//     })
// }

// 查询我的预约记录（场地预约单）
export const getMyVenueBookings = params =>{
    return request({
        url:'/getMyVenueBookings',
        method:'get',
        params:params
    })
}

// 取消预约（成功后将场地状态恢复为 可用=1）
// 已废弃，勿用：后端已切换到 v3 预约流程，请使用 `cancelPendingBooking`（待审批撤回）或新的取消接口
// export const cancelVenueBooking = params =>{
//     return request({
//         url:'/cancelVenueBooking',
//         method:'post',
//         params:params
//     })
// }


//*----------------------------------------------*/

// 查询可用时间点 (v3.0)
export const getAvailableStartTimes = params => {
    return request({
        url: '/getAvailableStartTimes',
        method: 'get',
        params: params
    })
}

// --- 管理端预约看板接口 (v1.0) ---
// 获取全量预约记录
export const getAllBookings = params => {
    return request({
        url: '/admin/getAllBookings',
        method: 'get',
        params: params
    })
}

// --- 管理端审批接口 (v3.1) ---
// 获取所有待审批预约
export const getPendingBookings = params => {
    return request({
        url: '/admin/getPendingBookings',
        method: 'get',
        params: params
    })
}

// 审批通过
export const approveBooking = params => {
    return request({
        url: '/admin/approveBooking',
        method: 'post',
        params: params // Query Params
    })
}

// 审批驳回
export const rejectBooking = params => {
    return request({
        url: '/admin/rejectBooking',
        method: 'post',
        params: params // Query Params
    })
}

// --- 教练端接口 (v3.2) ---
// 获取教练负责的待审批预约
export const getPendingBookingsByCoach = params => {
    return request({
        url: '/coach/getPendingBookings',
        method: 'get',
        params: params // employeeNo
    })
}

// 教练审批通过
export const coachApproveBooking = params => {
    return request({
        url: '/coach/approveBooking',
        method: 'post',
        params: params // bookingNo, employeeNo
    })
}

// 教练审批驳回
export const coachRejectBooking = params => {
    return request({
        url: '/coach/rejectBooking',
        method: 'post',
        params: params // bookingNo, employeeNo, reason
    })
}

// 教练核销完成
export const coachFinishBooking = params => {
    return request({
        url: '/coach/finishBooking',
        method: 'post',
        params: params // bookingNo, employeeNo
    })
}

// 教练标记爽约
export const coachMarkNoShow = params => {
    return request({
        url: '/coach/markNoShow',
        method: 'post',
        params: params // bookingNo, employeeNo
    })
}

// 会员端撤回待审批预约 (v3.1)
export const cancelPendingBooking = params => {
    return request({
        url: '/member/cancelPendingBooking',
        method: 'post',
        params: params // Query Params: bookingNo, memberNo
    })
}

// 获取预约时段选项（v3.2 推荐算法版）
export const getTimeOptions = params => {
    return request({
        url: '/booking/timeOptions',
        method: 'get',
        params: params // memberNo, courseNo, venueNo, date
    })
}

// 提交灵活预约 (v3.2)
export const bookVenueV3 = data => {
    return request({
        url: '/bookVenueV3',
        method: 'post',
        data: data
    })
}

// 根据日期和时段获取可用场地列表
export const getAvailableVenuesBySlot = params => {
    return request({
        url: '/getAvailableVenuesBySlot',
        method: 'get',
        params: params
    })
}

// 评价预约课程
export const evaluateBooking = data => {
    return request({
        url: '/evaluateBooking',
        method: 'post',
        data: data
    })
}

// ========== 退课相关 API ==========

// 获取会员课程包列表（管理员退课用）
export const getMemberCoursePackages = params => {
    return request({
        url: '/admin/member/courses',
        method: 'get',
        params: params
    })
}

// 退课处理
export const processCourseRefund = params => {
    return request({
        url: '/admin/course/refund',
        method: 'post',
        params: params
    })
}

// 查找所有课程信息
export const getAllCourse = params =>{
    return request({
        url:'/getAllCourse',
        method:'get',
        params:params
    })
}

// 查询会员已购买的课程列表
export const getPurchasedCourses = params =>{
    return request({
        url:'/getPurchasedCourses',
        method:'get',
        params:params
    })
}

// 购买课程
export const purchaseCourse = params =>{
    return request({
        url:'/purchaseCourse',
        method:'post',
        params:params
    })
}

// 查找所有器材信息
export const getAllCourseRegister = params =>{
    return request({
        url:'/getAllCourseRegister',
        method:'get',
        params:params
    })
}


//新增课程
export const addCourse = params =>{
    return request({
        url:'/addCourse',
        method:'post',
        data:params
    })
}

/*删除课程信息*/
export const deleteCourse = params =>{
    return request({
        url:'/deleteCourse',
        method:'delete',
        params:params
    })
}

/*修改课程信息*/
export const updateCourse = params =>{
    return request({
        url:'/updateCourse',
        method:'post',
        params:params
    })
}

/*查找课程表数据总条数*/
export const totalCourse = params =>{
    return request({
        url:'/totalCourse',
        method:'post',
        params:params
    })
}

// 模糊查询课程表
export const getByKeywordCourse = params =>{
    return request({
        url:'/getByKeywordCourse',
        method:'post',
        params:params
    })
}

// 模糊查询查找课程表数据总条数
export const totalCourseFuzzy = params =>{
    return request({
        url:'/totalCourseFuzzy',
        method:'post',
        params:params
    })
}

/*----------------------------------*/

// 查询注报名表数据总条数
export const getAllRegister = params =>{
    return request({
        url:'/getAllRegister',
        method:'get',
        params:params
    })
}

// 查询注报名表数据总条数
export const getRegisterByMemberNo = params =>{
    return request({
        url:'/getRegisterByMemberNo',
        method:'get',
        params:params
    })
}

//报名表课程
export const addRegister = params =>{
    return request({
        url:'/addRegister',
        method:'post',
        params:params
    })
}

/*删除报名信息*/
export const deleteRegister = params =>{
    return request({
        url:'/deleteRegister',
        method:'delete',
        params:params
    })
}

/*修改报名信息*/
export const updateRegister = params =>{
    return request({
        url:'/updateRegister',
        method:'post',
        params:params
    })
}

/*查找报名数据总条数*/
export const totalRegister = params =>{
    return request({
        url:'/totalRegister',
        method:'post',
        params:params
    })
}

// 模糊查询报名表
export const getByKeywordRegister = params =>{
    return request({
        url:'/getByKeywordRegister',
        method:'post',
        params:params
    })
}

// 模糊查询查找课程表数据总条数
export const totalRegisterFuzzy = params =>{
    return request({
        url:'/totalRegisterFuzzy',
        method:'post',
        params:params
    })
}

// ------------------------------------------


export const getRechargeByMemberNo = params =>{
    return request({
        url:'/getRechargeByMemberNo',
        method:'get',
        params:params
    })
}

export const getMemberIntegral = params =>{
    return request({
        url:'/getMemberIntegral',
        method:'get',
        params:params
    })
}

export const getMemberChange = params =>{
    return request({
        url:'/getMemberChange',
        method:'get',
        params:params
    })
}

export const getTotalMoney = params =>{
    return request({
        url:'/getTotalMoney',
        method:'get',
        params:params
    })
}

export const addRechargeByMemberNo = params =>{
    return request({
        url:'/addRechargeByMemberNo',
        method:'post',
        params:params
    })
}
//更新member表的余额
export const updateMemberChange = params =>{
    return request({
        url:'/updateMemberChange',
        method:'post',
        params:params
    })
}

//购买会员后更新member表的积分
export const updateMemberIntegral = params =>{
    return request({
        url:'/updateMemberIntegral',
        method:'post',
        params:params
    })
}

//购买会员后修改用户VIP权限
export const updateMemberPower = params =>{
    return request({
        url:'/updateMemberPower',
        method:'post',
        params:params
    })
}


//购买会员后修改用户VIP权限
export const getMemberPower = params =>{
    return request({
        url:'/getMemberPower',
        method:'get',
        params:params
    })
}


//获取会员基本信息
export const getMemberByMemberNo = params =>{
    return request({
        url:'/getMemberByMemberNo',
        method:'get',
        params:params
    })
}

//获取会员基本信息
export const updateMemberPassword = params =>{
    return request({
        url:'/updateMemberPassword',
        method:'post',
        params:params
    })
}


// 签到
export const addCheckIn = params =>{
    return request({
        url:'/addCheckIn',
        method:'post',
        params:params
    })
}


// 签到
export const updateMemberChangeByMemberNo = params =>{
    return request({
        url:'/updateMemberChangeByMemberNo',
        method:'post',
        params:params
    })
}

// ========== 会员卡相关 API ==========

// 获取会员卡状态（到期日、剩余天数、状态）
export const getMemberCardStatus = params => {
    return request({
        url: '/member/cardStatus',
        method: 'get',
        params: params
    })
}

// 获取卡项列表
export const getCardTypes = params => {
    return request({
        url: '/admin/cardTypes',
        method: 'get',
        params: params
    })
}

// 续卡接口
export const renewMemberCard = data => {
    return request({
        url: '/admin/renewMemberCard',
        method: 'post',
        data: data
    })
}

// ========== 财务流水相关 API ==========

// 获取财务流水列表
export const getLedgerList = params => {
    return request({
        url: '/admin/ledger/list',
        method: 'get',
        params: params
    })
}

// 导出财务流水 Excel
export const exportLedgerExcel = params => {
    return request({
        url: '/admin/ledger/export',
        method: 'get',
        params: params,
        responseType: 'blob'
    })
}

// 获取财务报表（日报/月报）
export const getFinanceReport = params => {
    return request({
        url: '/admin/finance/report',
        method: 'get',
        params: params
    })
}

// ========== 权责发生制财务 API ==========

// 获取会员递延收益概览
export const getMemberDeferredOverview = params => {
    return request({
        url: '/admin/accrual/member/overview',
        method: 'get',
        params: params
    })
}

// 获取会员递延收益详情
export const getMemberDeferredDetails = params => {
    return request({
        url: '/admin/accrual/member/deferred',
        method: 'get',
        params: params
    })
}

// 获取会员收入确认记录
export const getMemberRevenueRecognitions = params => {
    return request({
        url: '/admin/accrual/member/recognized',
        method: 'get',
        params: params
    })
}

// 手动触发会员卡分摊
export const amortizeCardRevenue = data => {
    return request({
        url: '/admin/accrual/amortize',
        method: 'post',
        data: data
    })
}

// 会员端：获取自己的递延收益概览
export const getMyDeferredOverview = params => {
    return request({
        url: '/member/accrual/overview',
        method: 'get',
        params: params
    })
}

// 会员端：获取自己的收入确认记录
export const getMyRevenueRecognitions = params => {
    return request({
        url: '/member/accrual/recognized',
        method: 'get',
        params: params
    })
}

// ========== 收付实现制财务 API (现金流水) ==========

// 获取现金收款流水列表
export const getCashFlowList = params => {
    return request({
        url: '/admin/cash/flow',
        method: 'get',
        params: params
    })
}

// 获取现金收款汇总（日/月报）
export const getCashSummary = params => {
    return request({
        url: '/admin/cash/summary',
        method: 'get',
        params: params
    })
}

// 获取权责发生制报表概览（本月确认收入、月末预收负债余额等）
export const getAccrualReportOverview = params => {
    return request({
        url: '/admin/accrual/report/overview',
        method: 'get',
        params: params
    })
}