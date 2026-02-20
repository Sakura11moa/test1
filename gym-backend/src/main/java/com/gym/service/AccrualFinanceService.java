package com.gym.service;

import com.gym.entity.*;
import com.gym.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权责发生制财务服务
 *
 * 核心逻辑：
 * 1. 收到现金 → 预收账款（负债），不立即确认收入
 * 2. 提供服务（上课/过一天）→ 从预收账款转为收入
 *
 * 业务类型：
 * - RECHARGE: 充值 → 预收账款-储值余额
 * - CARD_RENEW: 续卡 → 预收账款-会籍费（现金/余额）
 * - PURCHASE: 购课 → 预收账款-课程费
 * - COURSE_COMPLETE: 课程完成 → 预收转收入
 * - CARD_AMORTIZE: 会员卡分摊 → 预收转收入
 */
@Service
public class AccrualFinanceService {

    @Resource
    private DeferredRevenueMapper deferredRevenueMapper;

    @Resource
    private RevenueRecognitionMapper revenueRecognitionMapper;

    @Resource
    private ReceivableTransferMapper receivableTransferMapper;

    @Resource
    private FinanceService financeService;

    @Resource
    private CourseRefundMapper courseRefundMapper;

    @Resource
    private BalanceSnapshotMapper balanceSnapshotMapper;

    private static final String BIZ_TYPE_CARD = "CARD";
    private static final String BIZ_TYPE_COURSE = "COURSE";

    // ==================== 充值：预收账款-储值余额 ====================

    /**
     * 记录充值预收账款
     * 充值时，钱先进入预收账款（储值余额），不算收入
     */
    @Transactional
    public Map<String, Object> recordRechargeDeferred(Integer memberNo, String rechargeNo,
                                                       BigDecimal amount, String channel) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查是否已处理（幂等）
        DeferredRevenue existing = deferredRevenueMapper.getBySource("RECHARGE", rechargeNo);
        if (existing != null) {
            result.put("code", 200);
            result.put("message", "充值预收已记录");
            result.put("deferredId", existing.getId());
            return result;
        }

        // 2. 创建递延收益记录
        DeferredRevenue deferred = new DeferredRevenue();
        deferred.setMemberNo(memberNo);
        deferred.setSourceType("RECHARGE");
        deferred.setSourceNo(rechargeNo);
        deferred.setSourceAmount(amount);
        deferred.setDeferredAmount(amount);
        deferred.setRecognizedAmount(BigDecimal.ZERO);
        deferred.setBizType("BALANCE"); // 储值余额类型
        deferred.setSourceChannel(channel); // 资金来源渠道
        deferred.setTotalPeriods(1);
        deferred.setRecognizedPeriods(0);
        deferred.setStatus(1); // 进行中
        deferred.setRemark("充值-预收账款-储值余额");

        deferredRevenueMapper.insert(deferred);

        // 3. 记录财务流水（IN方向表示收到现金，但记为预收，不算收入）
        String bizNo = "D_" + deferred.getId();
        financeService.addLedger(
                "RECHARGE",
                rechargeNo,
                memberNo,
                "IN",  // 收到现金
                amount,
                channel,
                null,  // 不需要记录余额变化
                null,
                "预收账款-储值余额"
        );

        result.put("code", 200);
        result.put("message", "充值预收账款记录成功");
        result.put("deferredId", deferred.getId());
        return result;
    }

    // ==================== 续卡：预收账款-会籍费 ====================

    /**
     * 记录现金续卡的预收账款
     * 现金续卡：钱收到 → 预收账款-会籍费，按月分摊
     */
    @Transactional
    public Map<String, Object> recordCashRenewalDeferred(Integer memberNo, String renewalNo,
                                                          BigDecimal amount, int days,
                                                          LocalDate newExpireTime, Integer operatorAdminId,
                                                          LocalDate startDate) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查是否已处理
        DeferredRevenue existing = deferredRevenueMapper.getBySource("CARD_RENEW", renewalNo);
        if (existing != null) {
            result.put("code", 200);
            result.put("message", "续卡预收已记录");
            result.put("deferredId", existing.getId());
            return result;
        }

        // 2. 计算分摊期数（月数）
        int months = days / 30;
        if (months < 1) months = 1;

        // 3. 计算每月分摊金额
        BigDecimal monthlyAmount = amount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        // 4. 创建递延收益记录
        DeferredRevenue deferred = new DeferredRevenue();
        deferred.setMemberNo(memberNo);
        deferred.setSourceType("CARD_RENEW");
        deferred.setSourceNo(renewalNo);
        deferred.setSourceAmount(amount);
        deferred.setDeferredAmount(amount);
        deferred.setRecognizedAmount(BigDecimal.ZERO);
        deferred.setBizType(BIZ_TYPE_CARD); // 会籍费
        deferred.setSourceChannel("CASH"); // 现金续卡
        deferred.setTotalPeriods(months);
        deferred.setRecognizedPeriods(0);
        deferred.setStatus(1); // 进行中
        deferred.setStartDate(startDate);
        deferred.setEndDate(newExpireTime);
        deferred.setRemark("现金续卡-预收账款-会籍费");

        deferredRevenueMapper.insert(deferred);

        // 5. 记录财务流水
        financeService.addLedger(
                "CARD_RENEW",
                renewalNo,
                memberNo,
                "IN",  // 收到现金
                amount,
                "CASH",
                null,
                operatorAdminId,
                "预收账款-会籍费(" + months + "个月,每月" + monthlyAmount + ")"
        );

        result.put("code", 200);
        result.put("message", "现金续卡预收账款记录成功");
        result.put("deferredId", deferred.getId());
        result.put("monthlyAmount", monthlyAmount);
        result.put("totalMonths", months);
        return result;
    }

    /**
     * 记录余额续卡的预收账款内部划转
     * 余额续卡：储值余额 → 会籍费，不产生新收入，只是内部划转
     */
    @Transactional
    public Map<String, Object> recordBalanceRenewalTransfer(Integer memberNo, String renewalNo,
                                                             BigDecimal amount, int days,
                                                             String fromRechargeNo,
                                                             LocalDate newExpireTime,
                                                             LocalDate startDate) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查是否已处理
        DeferredRevenue existing = deferredRevenueMapper.getBySource("CARD_RENEW", renewalNo);
        if (existing != null) {
            result.put("code", 200);
            result.put("message", "余额续卡已记录");
            result.put("deferredId", existing.getId());
            return result;
        }

        // 2. 计算分摊期数
        int months = days / 30;
        if (months < 1) months = 1;

        BigDecimal monthlyAmount = amount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        // 3. 创建递延收益记录
        DeferredRevenue deferred = new DeferredRevenue();
        deferred.setMemberNo(memberNo);
        deferred.setSourceType("CARD_RENEW");
        deferred.setSourceNo(renewalNo);
        deferred.setSourceAmount(amount);
        deferred.setDeferredAmount(amount);
        deferred.setRecognizedAmount(BigDecimal.ZERO);
        deferred.setBizType(BIZ_TYPE_CARD);
        deferred.setSourceChannel("BALANCE"); // 余额续卡
        deferred.setTotalPeriods(months);
        deferred.setRecognizedPeriods(0);
        deferred.setStatus(1);
        deferred.setStartDate(startDate);
        deferred.setEndDate(newExpireTime);
        deferred.setRemark("余额续卡-预收账款内部划转");

        deferredRevenueMapper.insert(deferred);

        // 4. 记录内部划转
        ReceivableTransfer transfer = new ReceivableTransfer();
        transfer.setMemberNo(memberNo);
        transfer.setFromSourceType("RECHARGE");
        transfer.setFromSourceNo(fromRechargeNo);
        transfer.setToSourceType("CARD_RENEW");
        transfer.setToRenewalNo(renewalNo);
        transfer.setAmount(amount);
        transfer.setStatus(1);
        transfer.setRemark("储值余额转会籍费");

        receivableTransferMapper.insert(transfer);

        // 5. 记录财务流水（NONE方向，内部调账）
        financeService.addLedger(
                "CARD_RENEW",
                renewalNo,
                memberNo,
                "NONE",  // 内部调账
                amount,
                "BALANCE",
                null,
                null,
                "预收账款内部划转-储值余额→会籍费"
        );

        result.put("code", 200);
        result.put("message", "余额续卡内部划转记录成功");
        result.put("deferredId", deferred.getId());
        result.put("monthlyAmount", monthlyAmount);
        return result;
    }

    // ==================== 购课：预收账款-课程费 ====================

    /**
     * 记录购课的预收账款
     * 购课时：余额扣款 → 预收账款-课程费，上课时才确认收入
     */
    @Transactional
    public Map<String, Object> recordCoursePurchaseDeferred(Integer memberNo, String purchaseNo,
                                                              BigDecimal amount, int quantity) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查是否已处理
        DeferredRevenue existing = deferredRevenueMapper.getBySource("PURCHASE", purchaseNo);
        if (existing != null) {
            result.put("code", 200);
            result.put("message", "购课预收已记录");
            result.put("deferredId", existing.getId());
            return result;
        }

        // 2. 创建递延收益记录
        DeferredRevenue deferred = new DeferredRevenue();
        deferred.setMemberNo(memberNo);
        deferred.setSourceType("PURCHASE");
        deferred.setSourceNo(purchaseNo);
        deferred.setSourceAmount(amount);
        deferred.setDeferredAmount(amount);
        deferred.setRecognizedAmount(BigDecimal.ZERO);
        deferred.setBizType(BIZ_TYPE_COURSE);
        deferred.setSourceChannel("BALANCE"); // 购课用余额支付
        deferred.setTotalPeriods(quantity); // 按次数分摊
        deferred.setRecognizedPeriods(0);
        deferred.setStatus(1);
        deferred.setStartDate(LocalDate.now()); // 设置生效日期（当天）
        deferred.setRemark("购课-预收账款-课程费");

        deferredRevenueMapper.insert(deferred);

        // 3. 记录财务流水
        financeService.addLedger(
                "PURCHASE",
                purchaseNo,
                memberNo,
                "NONE",  // 内部调账
                amount,
                "BALANCE",
                null,
                null,
                "预收账款-课程费(共" + quantity + "次)"
        );

        result.put("code", 200);
        result.put("message", "购课预收账款记录成功");
        result.put("deferredId", deferred.getId());
        result.put("amountPerClass", amount.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP));
        return result;
    }

    // ==================== 课程完成：确认收入 ====================

    /**
     * 课程完成时确认收入
     * 从预收账款-课程费转为收入
     */
    @Transactional
    public Map<String, Object> recognizeCourseRevenue(Integer memberNo, Long deferredId,
                                                       String bookingNo, BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取递延记录
        DeferredRevenue deferred = deferredRevenueMapper.getById(deferredId);
        if (deferred == null) {
            result.put("code", 400);
            result.put("message", "递延记录不存在");
            return result;
        }

        // 2. 检查是否已全部确认
        if (deferred.getRecognizedAmount().compareTo(deferred.getSourceAmount()) >= 0) {
            result.put("code", 400);
            result.put("message", "该笔预收已全部确认为收入");
            return result;
        }

        // 3. 更新递延记录
        BigDecimal newRecognizedAmount = deferred.getRecognizedAmount().add(amount);
        int newRecognizedPeriods = deferred.getRecognizedPeriods() + 1;

        deferredRevenueMapper.updateRecognizedAmount(
                deferredId,
                newRecognizedAmount,
                newRecognizedPeriods
        );

        // 4. 记录收入确认
        RevenueRecognition recognition = new RevenueRecognition();
        recognition.setMemberNo(memberNo);
        recognition.setDeferredId(deferredId);
        recognition.setBizType(BIZ_TYPE_COURSE);
        recognition.setRecognitionType("COURSE_COMPLETE");
        recognition.setAmount(amount);
        recognition.setPeriod(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))); // 使用月份格式
        recognition.setRelatedBizNo(bookingNo);
        recognition.setRemark("课程完成-确认收入");

        revenueRecognitionMapper.insert(recognition);

        // 5. 记录财务流水（DEFERRED表示预收转收入）
        String bizNo = "R_" + recognition.getId();
        financeService.addLedger(
                "COURSE_RECOGNIZE",
                bookingNo,
                memberNo,
                "DEFERRED",  // 预收转收入
                amount,
                "BALANCE",
                null,
                null,
                "已实现收入-课程核销"
        );

        result.put("code", 200);
        result.put("message", "课程收入确认成功");
        result.put("recognitionId", recognition.getId());
        return result;
    }

    /**
     * 根据预约单确认课程收入（便捷方法）
     * 如果预约关联了购课单号(purchaseNo)，则从正确的购课预收确认收入
     * 否则使用旧逻辑：查找该会员最近的一笔购课预收
     */
    @Transactional
    public Map<String, Object> recognizeCourseByBooking(Integer memberNo, String bookingNo,
                                                         BigDecimal amountPerClass) {
        // 1. 如果 bookingNo 对应 purchaseNo，从正确的购课预收确认
        if (bookingNo != null) {
            try {
                Integer bookingNoInt = Integer.parseInt(bookingNo);
                VenueBooking booking = null;
                // 注意：这里可能会循环依赖，延迟注入或在 Controller 层传入 purchaseNo
                // 暂时使用 fallback 逻辑
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // Fallback: 查找该会员最近的一笔购课预收（旧的逻辑）
        List<DeferredRevenue> list = deferredRevenueMapper.getPendingList(memberNo);
        for (DeferredRevenue d : list) {
            if ("PURCHASE".equals(d.getSourceType()) && d.getStatus() == 1) {
                return recognizeCourseRevenue(memberNo, d.getId(), bookingNo, amountPerClass);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", "未找到待确认的课程预收");
        return result;
    }

    /**
     * 根据预约单确认课程收入（增强版，支持指定购课单号）
     */
    @Transactional
    public Map<String, Object> recognizeCourseByBookingWithPurchase(Integer memberNo, String bookingNo,
                                                                      Integer purchaseNo,
                                                                      BigDecimal amountPerClass) {
        // 1. 如果指定了 purchaseNo，优先从对应的购课预收确认
        if (purchaseNo != null) {
            DeferredRevenue deferred = deferredRevenueMapper.getBySource("PURCHASE", String.valueOf(purchaseNo));
            if (deferred != null && deferred.getStatus() == 1) {
                return recognizeCourseRevenue(memberNo, deferred.getId(), bookingNo, amountPerClass);
            }
        }

        // Fallback: 查找该会员最近的一笔购课预收
        return recognizeCourseByBooking(memberNo, bookingNo, amountPerClass);
    }

    // ==================== 会员卡按月分摊 ====================

    /**
     * 按月分摊会员卡收入（供定时任务调用）
     */
    @Transactional
    public Map<String, Object> amortizeCardRevenue(String yearMonth) {
        Map<String, Object> result = new HashMap<>();
        int count = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 1. 获取所有进行中的会员卡递延记录
        List<DeferredRevenue> activeList = deferredRevenueMapper.getActiveList(BIZ_TYPE_CARD);

        for (DeferredRevenue deferred : activeList) {
            // 检查是否已全部分摊
            if (deferred.getRecognizedAmount().compareTo(deferred.getSourceAmount()) >= 0) {
                continue;
            }

            // 检查该月是否已分摊过（通过检查相关biz_no是否已处理）
            List<RevenueRecognition> existing = revenueRecognitionMapper.getByDeferredId(deferred.getId());
            boolean alreadyRecognized = false;
            for (RevenueRecognition r : existing) {
                if (yearMonth.equals(r.getPeriod())) {
                    alreadyRecognized = true;
                    break;
                }
            }
            if (alreadyRecognized) {
                continue;
            }

            // 计算本次分摊金额
            BigDecimal amountPerMonth = deferred.getSourceAmount()
                    .divide(BigDecimal.valueOf(deferred.getTotalPeriods()), 2, RoundingMode.HALF_UP);

            // 最后一个月取剩余金额（避免舍入误差）
            BigDecimal remaining = deferred.getSourceAmount().subtract(deferred.getRecognizedAmount());
            if (remaining.compareTo(amountPerMonth) < 0) {
                amountPerMonth = remaining;
            }

            // 更新递延记录
            BigDecimal newRecognizedAmount = deferred.getRecognizedAmount().add(amountPerMonth);
            int newRecognizedPeriods = deferred.getRecognizedPeriods() + 1;

            deferredRevenueMapper.updateRecognizedAmount(
                    deferred.getId(),
                    newRecognizedAmount,
                    newRecognizedPeriods
            );

            // 记录收入确认
            RevenueRecognition recognition = new RevenueRecognition();
            recognition.setMemberNo(deferred.getMemberNo());
            recognition.setDeferredId(deferred.getId());
            recognition.setBizType(BIZ_TYPE_CARD);
            recognition.setRecognitionType("MONTHLY");
            recognition.setAmount(amountPerMonth);
            recognition.setPeriod(yearMonth);
            recognition.setRelatedBizNo(deferred.getSourceNo());
            recognition.setRemark("会员卡按月分摊确认收入");

            revenueRecognitionMapper.insert(recognition);

            // 记录财务流水
            String bizNo = "R_" + recognition.getId();
            financeService.addLedger(
                    "CARD_RECOGNIZE",
                    deferred.getSourceNo(),
                    deferred.getMemberNo(),
                    "DEFERRED",
                    amountPerMonth,
                    "BALANCE",
                    null,
                    null,
                    "已实现收入-会员卡分摊(" + yearMonth + ")"
            );

            count++;
            totalAmount = totalAmount.add(amountPerMonth);
        }

        result.put("code", 200);
        result.put("message", "会员卡分摊完成");
        result.put("count", count);
        result.put("totalAmount", totalAmount);
        return result;
    }

    // ==================== 查询方法 ====================

    /**
     * 查询会员的递延收益概览（memberNo为空时查询全部）
     */
    public Map<String, Object> getMemberDeferredOverview(Integer memberNo) {
        Map<String, Object> result = new HashMap<>();

        // 递延收益
        BigDecimal cardDeferred = deferredRevenueMapper.getTotalDeferredAmount(memberNo, BIZ_TYPE_CARD);
        BigDecimal courseDeferred = deferredRevenueMapper.getTotalDeferredAmount(memberNo, BIZ_TYPE_COURSE);
        BigDecimal balanceDeferred = deferredRevenueMapper.getTotalDeferredAmount(memberNo, "BALANCE");

        // 已确认收入（按业务类型分类）
        BigDecimal totalRevenue = revenueRecognitionMapper.getTotalByMember(memberNo);
        BigDecimal courseRecognized = revenueRecognitionMapper.getTotalByMemberAndBizType(memberNo, BIZ_TYPE_COURSE);
        BigDecimal cardRecognized = revenueRecognitionMapper.getTotalByMemberAndBizType(memberNo, BIZ_TYPE_CARD);
        BigDecimal balanceRecognized = revenueRecognitionMapper.getTotalByMemberAndBizType(memberNo, "BALANCE");

        result.put("memberNo", memberNo);
        result.put("cardDeferred", cardDeferred != null ? cardDeferred : BigDecimal.ZERO);
        result.put("courseDeferred", courseDeferred != null ? courseDeferred : BigDecimal.ZERO);
        result.put("balanceDeferred", balanceDeferred != null ? balanceDeferred : BigDecimal.ZERO);
        result.put("totalDeferred", (cardDeferred != null ? cardDeferred : BigDecimal.ZERO)
                .add(courseDeferred != null ? courseDeferred : BigDecimal.ZERO)
                .add(balanceDeferred != null ? balanceDeferred : BigDecimal.ZERO));

        result.put("totalRecognized", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        result.put("courseRecognized", courseRecognized != null ? courseRecognized : BigDecimal.ZERO);
        result.put("cardRecognized", cardRecognized != null ? cardRecognized : BigDecimal.ZERO);
        result.put("balanceRecognized", balanceRecognized != null ? balanceRecognized : BigDecimal.ZERO);

        return result;
    }

    /**
     * 查询递延收益详情（memberNo为空时查询全部）
     */
    public List<DeferredRevenue> getMemberDeferredDetails(Integer memberNo) {
        return deferredRevenueMapper.getByMemberNo(memberNo);
    }

    /**
     * 查询收入确认记录（memberNo为空时查询全部）
     */
    public List<RevenueRecognition> getMemberRevenueRecognitions(Integer memberNo) {
        return revenueRecognitionMapper.getByMemberNo(memberNo);
    }

    /**
     * 权责经营报表：获取完整数据（界面三核心数据）
     * @param yearMonth 查询月份，格式：yyyy-MM，默认为当前月
     */
    public Map<String, Object> getAccrualBusinessReport(String yearMonth) {
        Map<String, Object> result = new HashMap<>();

        // 默认当前月
        if (yearMonth == null || yearMonth.isEmpty()) {
            yearMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // 计算月份第一天和最后一天
        LocalDate date = LocalDate.parse(yearMonth + "-01");
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        String monthStart = firstDayOfMonth.toString();
        String monthEnd = lastDayOfMonth.toString() + " 23:59:59";

        // ========== 1. 核心指标卡片 ==========
        // F3.1.1 本月确认收入 = 会籍按月摊销收入 + 课程核销收入
        BigDecimal monthCardRecognized = revenueRecognitionMapper.getTotalByPeriodAndBizType(yearMonth, BIZ_TYPE_CARD);
        BigDecimal monthCourseRecognized = revenueRecognitionMapper.getTotalByPeriodAndBizType(yearMonth, BIZ_TYPE_COURSE);
        BigDecimal monthRecognizedIncome = (monthCardRecognized != null ? monthCardRecognized : BigDecimal.ZERO)
                .add(monthCourseRecognized != null ? monthCourseRecognized : BigDecimal.ZERO);

        // F3.1.2 月末预收负债余额 = 储值余额 + 未耗会籍 + 未耗课程
        BigDecimal balanceDeferred = deferredRevenueMapper.getTotalDeferredAmount(null, "BALANCE"); // 储值余额
        BigDecimal cardDeferred = deferredRevenueMapper.getTotalDeferredAmount(null, BIZ_TYPE_CARD); // 未耗会籍
        BigDecimal courseDeferred = deferredRevenueMapper.getTotalDeferredAmount(null, BIZ_TYPE_COURSE); // 未耗课程

        BigDecimal totalDeferredBalance = (balanceDeferred != null ? balanceDeferred : BigDecimal.ZERO)
                .add(cardDeferred != null ? cardDeferred : BigDecimal.ZERO)
                .add(courseDeferred != null ? courseDeferred : BigDecimal.ZERO);

        // F3.1.3 本月退课总金额（非现金，退还储值余额）
        BigDecimal monthRefundAmount = courseRefundMapper.getTotalRefundAmountByPeriod(monthStart, monthEnd);
        if (monthRefundAmount == null) monthRefundAmount = BigDecimal.ZERO;

        // F3.1.4 本月新增会籍负债 = 按生效日期统计本月新增会籍预收
        // 按生效日期统计：1月30日续卡但2月1日生效，则计入2月新增
        BigDecimal monthNewCardDeferred = deferredRevenueMapper.getNewCardLiabilityByEffectiveDate(yearMonth);
        if (monthNewCardDeferred == null) monthNewCardDeferred = BigDecimal.ZERO;

        // F3.1.5 本月新增课程负债 = 本月余额购课（课程只能余额购买）
        BigDecimal monthNewCourseDeferred = deferredRevenueMapper.getNewCourseLiabilityByStartDate(yearMonth);
        if (monthNewCourseDeferred == null) monthNewCourseDeferred = BigDecimal.ZERO;

        // ========== 2. 储值余额变动摘要 ==========
        // F3.2.1 期初储值余额 = 从快照表获取上月末储值余额
        // 计算上月末日期
        LocalDate lastMonthEnd = date.minusDays(1);
        BalanceSnapshot snapshot = balanceSnapshotMapper.getBySnapshotDate(lastMonthEnd);
        
        BigDecimal beginningBalance;
        if (snapshot != null && snapshot.getBalanceAmount() != null) {
            // 如果有快照数据，直接使用
            beginningBalance = snapshot.getBalanceAmount();
        } else {
            // 如果没有快照数据，使用简化公式计算（兼容旧数据）
            BigDecimal monthRecharge = financeService.getMonthRechargeTotal(yearMonth);
            BigDecimal monthBalanceUsed = (monthNewCardDeferred != null ? monthNewCardDeferred : BigDecimal.ZERO)
                    .add(monthNewCourseDeferred != null ? monthNewCourseDeferred : BigDecimal.ZERO);
            beginningBalance = (balanceDeferred != null ? balanceDeferred : BigDecimal.ZERO)
                    .subtract(monthRecharge != null ? monthRecharge : BigDecimal.ZERO)
                    .add(monthBalanceUsed)
                    .add(monthRefundAmount);
            if (beginningBalance.compareTo(BigDecimal.ZERO) < 0) beginningBalance = BigDecimal.ZERO;
        }

        // F3.2.2 本月充值流入
        BigDecimal monthRecharge = financeService.getMonthRechargeTotal(yearMonth);
        if (monthRecharge == null) monthRecharge = BigDecimal.ZERO;

        // F3.2.3 本月余额续卡（内部划转）- 来自余额的会籍续卡
        BigDecimal monthBalanceRenew = deferredRevenueMapper.getNewCardLiabilityByStartDateAndChannel(yearMonth, "BALANCE");
        if (monthBalanceRenew == null) monthBalanceRenew = BigDecimal.ZERO;

        // F3.2.4 本月余额购课（内部划转）
        // 已获取：monthNewCourseDeferred

        // F3.2.5 本月退课返还（+退课返还）
        // 已获取：monthRefundAmount

        // F3.2.6 月末储值余额
        // 已获取：balanceDeferred

        Map<String, Object> balanceChangeSummary = new HashMap<>();
        balanceChangeSummary.put("beginningBalance", beginningBalance);
        balanceChangeSummary.put("rechargeIn", monthRecharge != null ? monthRecharge : BigDecimal.ZERO);
        balanceChangeSummary.put("balanceRenew", monthBalanceRenew != null ? monthBalanceRenew : BigDecimal.ZERO);
        balanceChangeSummary.put("balanceCourse", monthNewCourseDeferred != null ? monthNewCourseDeferred : BigDecimal.ZERO);
        balanceChangeSummary.put("refundIn", monthRefundAmount);
        balanceChangeSummary.put("endingBalance", balanceDeferred != null ? balanceDeferred : BigDecimal.ZERO);

        // ========== 3. 预收负债构成 ==========
        // F3.3.1 总预收负债余额（同F3.1.2）
        // F3.3.2 储值余额
        // F3.3.3 未耗会籍（含现金/余额来源拆分）
        // F3.3.4 未耗课程

        // 未耗会籍 - 现金来源
        BigDecimal cardDeferredCash = deferredRevenueMapper.getTotalDeferredAmountByChannel(null, BIZ_TYPE_CARD, "CASH");
        // 未耗会籍 - 余额来源
        BigDecimal cardDeferredBalance = deferredRevenueMapper.getTotalDeferredAmountByChannel(null, BIZ_TYPE_CARD, "BALANCE");

        // 未耗课程（课程只能余额购买，没有现金来源）
        BigDecimal courseDeferredAmount = courseDeferred != null ? courseDeferred : BigDecimal.ZERO;

        Map<String, Object> deferredComposition = new HashMap<>();
        deferredComposition.put("totalDeferred", totalDeferredBalance);
        deferredComposition.put("balance", balanceDeferred != null ? balanceDeferred : BigDecimal.ZERO);

        // 未耗会籍（含来源拆分）
        Map<String, Object> cardDeferredMap = new HashMap<>();
        BigDecimal cardTotal = (cardDeferredCash != null ? cardDeferredCash : BigDecimal.ZERO)
                .add(cardDeferredBalance != null ? cardDeferredBalance : BigDecimal.ZERO);
        cardDeferredMap.put("total", cardTotal);
        cardDeferredMap.put("cash", cardDeferredCash != null ? cardDeferredCash : BigDecimal.ZERO);
        cardDeferredMap.put("balance", cardDeferredBalance != null ? cardDeferredBalance : BigDecimal.ZERO);
        deferredComposition.put("cardDeferred", cardDeferredMap);

        // 未耗课程
        Map<String, Object> courseDeferredMap = new HashMap<>();
        courseDeferredMap.put("total", courseDeferredAmount);
        courseDeferredMap.put("cash", BigDecimal.ZERO); // 课程无现金来源
        courseDeferredMap.put("balance", courseDeferredAmount);
        deferredComposition.put("courseDeferred", courseDeferredMap);

        // ========== 4. 退课统计 ==========
        Map<String, Object> refundSummary = new HashMap<>();
        refundSummary.put("monthRefundAmount", monthRefundAmount);
        // 查询本月退课次数
        List<CourseRefund> monthRefunds = courseRefundMapper.getByPeriod(monthStart, monthEnd);
        refundSummary.put("monthRefundCount", monthRefunds != null ? monthRefunds.size() : 0);

        // ========== 5. 到期时间分布 F3.3.5 ==========
        Map<String, Object> maturityDistribution = calculateMaturityDistribution();

        // ========== 6. 收入确认明细列表 F3.4 ==========
        List<RevenueRecognition> recognitionDetails = revenueRecognitionMapper.getByPeriod(yearMonth);

        // ========== 7. 退课明细列表 F3.5.5 ==========
        List<CourseRefund> refundDetails = monthRefunds;

        // ========== 组装返回结果 ==========
        result.put("yearMonth", yearMonth);

        // 核心指标
        result.put("monthRecognizedIncome", monthRecognizedIncome);
        result.put("monthEndDeferredBalance", totalDeferredBalance);
        result.put("monthRefundAmount", monthRefundAmount);
        result.put("monthNewCardLiability", monthNewCardDeferred != null ? monthNewCardDeferred : BigDecimal.ZERO);
        result.put("monthNewCourseLiability", monthNewCourseDeferred != null ? monthNewCourseDeferred : BigDecimal.ZERO);

        // 储值变动
        result.put("balanceChangeSummary", balanceChangeSummary);

        // 预收构成
        result.put("deferredComposition", deferredComposition);

        // 退课统计
        result.put("refundSummary", refundSummary);
        result.put("refundCount", monthRefunds != null ? monthRefunds.size() : 0);

        // 到期时间分布
        result.put("maturityDistribution", maturityDistribution);

        // 明细列表
        result.put("recognitionDetails", recognitionDetails);
        result.put("refundDetails", refundDetails);

        // 兼容旧版
        result.put("monthRecognized", monthRecognizedIncome);
        result.put("monthCardRecognized", monthCardRecognized != null ? monthCardRecognized : BigDecimal.ZERO);
        result.put("monthCourseRecognized", monthCourseRecognized != null ? monthCourseRecognized : BigDecimal.ZERO);
        result.put("totalDeferred", totalDeferredBalance);

        return result;
    }

    /**
     * 计算到期时间分布
     * F3.3.5: 按会籍+课程的剩余有效期分组：1个月内、1-3个月、3-6个月、6个月以上
     */
    private Map<String, Object> calculateMaturityDistribution() {
        Map<String, Object> distribution = new HashMap<>();

        List<DeferredRevenue> activeList = deferredRevenueMapper.getMaturityDistribution();

        BigDecimal withinOneMonth = BigDecimal.ZERO;
        BigDecimal oneToThreeMonths = BigDecimal.ZERO;
        BigDecimal threeToSixMonths = BigDecimal.ZERO;
        BigDecimal overSixMonths = BigDecimal.ZERO;

        LocalDate today = LocalDate.now();

        if (activeList != null) {
            for (DeferredRevenue dr : activeList) {
                if (dr.getEndDate() == null) continue;

                // 待确认金额
                BigDecimal pendingAmount = dr.getDeferredAmount()
                        .subtract(dr.getRecognizedAmount() != null ? dr.getRecognizedAmount() : BigDecimal.ZERO);

                // 剩余天数
                long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(today, dr.getEndDate());

                if (remainingDays <= 30) {
                    withinOneMonth = withinOneMonth.add(pendingAmount);
                } else if (remainingDays <= 90) {
                    oneToThreeMonths = oneToThreeMonths.add(pendingAmount);
                } else if (remainingDays <= 180) {
                    threeToSixMonths = threeToSixMonths.add(pendingAmount);
                } else {
                    overSixMonths = overSixMonths.add(pendingAmount);
                }
            }
        }

        BigDecimal total = withinOneMonth.add(oneToThreeMonths).add(threeToSixMonths).add(overSixMonths);

        distribution.put("withinOneMonth", withinOneMonth);
        distribution.put("oneToThreeMonths", oneToThreeMonths);
        distribution.put("threeToSixMonths", threeToSixMonths);
        distribution.put("overSixMonths", overSixMonths);
        distribution.put("total", total);

        return distribution;
    }
}
