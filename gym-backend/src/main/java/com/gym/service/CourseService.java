package com.gym.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.gym.entity.*;
import com.gym.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程 服务类
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private CourseStockMapper courseStockMapper;

    @Resource
    private CoursePurchaseMapper coursePurchaseMapper;

    @Resource
    private CourseStockLogMapper courseStockLogMapper;

    @Resource
    private FinanceService financeService;

    @Resource
    private AccrualFinanceService accrualFinanceService;

    @Resource
    private DeferredRevenueMapper deferredRevenueMapper;

    @Resource
    private CourseRefundMapper courseRefundMapper;

    @Resource
    private ReceivableTransferMapper receivableTransferMapper;

    public List<Course> getAllCourse(int page, int size) {
        return courseMapper.getAllCourse(page, size);
    }

    public List<Course> getAllCourseRegister() {
        return courseMapper.getAllCourseRegister();
    }

    public Map<String, Object> addCourse(Course course) {
        Map<String, Object> resultMap = new HashMap<>();

        int result = courseMapper.addCourse(course);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "添加成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "添加失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateCourse(Course course) {
        Map<String, Object> resultMap = new HashMap<>();

        int result = courseMapper.updateCourse(course);
        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "修改失败");
        }

        return resultMap;
    }

    public Map<String, Object> deleteCourse(int courseNo) {
        Map<String, Object> resultMap = new HashMap<>();
        int result = courseMapper.deleteCourse(courseNo);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "删除成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "删除失败");
        }

        return resultMap;
    }

    public Common totalCourse() {
        return courseMapper.totalCourse();
    }

    public List<Course> getByKeywordCourse(String keyWord, int page, int size) {
        return courseMapper.getByKeywordCourse(keyWord, page, size);
    }

    public Common totalCourseFuzzy(String keyWord) {
        return courseMapper.totalCourseFuzzy(keyWord);
    }

    public List<Course> getMyPurchasedCourses(String memberNo) {
        return courseStockMapper.findCoursesWithPositiveStock(Integer.valueOf(memberNo));
    }

    @Transactional
    public Map<String, Object> purchaseCourse(Integer memberNo, Integer courseNo, Integer quantity) {
        Map<String, Object> resultMap = new HashMap<>();

        // 1) 参数校验
        if (memberNo == null || courseNo == null || quantity == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "购买失败，参数不能为空");
            return resultMap;
        }

        if (quantity <= 0 || quantity > 99) {
            resultMap.put("code", 400);
            resultMap.put("message", "购买失败，购买数量必须在1-99之间");
            return resultMap;
        }

        // 2) 查询课程单价
        Course course = courseMapper.getCourseByNo(courseNo);
        if (course == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "购买失败，课程不存在");
            return resultMap;
        }

        java.math.BigDecimal unitPrice = java.math.BigDecimal.valueOf(course.getCoursePrice());
        java.math.BigDecimal totalAmount = unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));

        // 3) 校验余额
        Double currentChange = memberMapper.getMemberChange(memberNo);
        if (currentChange == null) currentChange = 0.0;
        java.math.BigDecimal balance = java.math.BigDecimal.valueOf(currentChange);
        if (balance.compareTo(totalAmount) < 0) {
            resultMap.put("code", 400);
            resultMap.put("message", "购买失败，余额不足");
            resultMap.put("needAmount", totalAmount);
            resultMap.put("balance", balance);
            return resultMap;
        }

        // 4) 扣除余额
        int deducted = memberMapper.updateMemberChangeByMemberNo(memberNo, totalAmount.doubleValue());
        if (deducted <= 0) {
            resultMap.put("code", 500);
            resultMap.put("message", "购买失败，余额扣减异常");
            return resultMap;
        }

        // 5) 写购买记录
        CoursePurchase purchase = new CoursePurchase();
        purchase.setMemberNo(memberNo);
        purchase.setCourseNo(courseNo);
        purchase.setQuantity(quantity);
        purchase.setUnitPrice(unitPrice);
        purchase.setTotalAmount(totalAmount);
        purchase.setStatus("PAID");
        purchase.setCreateTime(java.time.LocalDateTime.now());

        int insertPurchase = coursePurchaseMapper.insert(purchase);
        if (insertPurchase <= 0) {
            resultMap.put("code", 500);
            resultMap.put("message", "购买失败，订单写入异常");
            return resultMap;
        }

        // 4) 更新库存（无 warning 方式：先尝试更新，若影响行数为0则插入）
        int updateStock = courseStockMapper.addStock(memberNo, courseNo, quantity);
        if (updateStock == 0) {
            // 库存行不存在，插入新行
            int insertStock = courseStockMapper.upsertStock(memberNo, courseNo, quantity, quantity);
            if (insertStock <= 0) {
                resultMap.put("code", 500);
                resultMap.put("message", "购买失败，库存更新异常");
                return resultMap;
            }
        }

        // 5) 查询更新后的库存用于日志
        CourseStock updatedStock = courseStockMapper.getByMemberAndCourse(memberNo, courseNo);
        int afterRemain = (updatedStock != null) ? updatedStock.getRemainTimes() : quantity;

        // 6) 写库存日志
        CourseStockLog stockLog = new CourseStockLog();
        stockLog.setMemberNo(memberNo);
        stockLog.setCourseNo(courseNo);
        stockLog.setChangeTimes(quantity);
        stockLog.setAfterRemain(afterRemain);
        stockLog.setBizType("PURCHASE");
        stockLog.setBizNo(String.valueOf(purchase.getPurchaseNo()));
        stockLog.setRemark("购买课程，增加" + quantity + "次");
        stockLog.setCreateTime(java.time.LocalDateTime.now());

        int insertLog = courseStockLogMapper.insert(stockLog);
        if (insertLog <= 0) {
            resultMap.put("code", 500);
            resultMap.put("message", "购买失败，日志写入异常");
            return resultMap;
        }

        // 7) 【权责发生制】购课记为预收账款，不直接算收入
        // 课程收入在预约完成（上课）时才确认
        Double after = memberMapper.getMemberChange(memberNo);
        if (after == null) after = 0.0;

        String purchaseNo = String.valueOf(purchase.getPurchaseNo());
        accrualFinanceService.recordCoursePurchaseDeferred(
                memberNo,
                purchaseNo,
                totalAmount,
                quantity
        );

        resultMap.put("code", 200);
        resultMap.put("message", "购买成功，课程次数已生效");
        resultMap.put("purchaseNo", purchase.getPurchaseNo());
        resultMap.put("totalAmount", totalAmount);
        resultMap.put("balanceAfter", after);
        return resultMap;
    }

    /**
     * 查询会员的课程包列表（包含库存和递延收益信息）
     * 用于管理员退课选择
     */
    public List<Map<String, Object>> getMemberCoursePackages(Integer memberNo) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取会员所有购课记录（合并相同课程的购买记录）
        List<CoursePurchase> purchases = coursePurchaseMapper.listByMember(memberNo);
        if (purchases == null || purchases.isEmpty()) {
            return result;
        }

        // 按课程分组，汇总购买数量和获取最新单价
        Map<Integer, Map<String, Object>> courseSummary = new HashMap<>();
        for (CoursePurchase purchase : purchases) {
            Integer courseNo = purchase.getCourseNo();
            if (!courseSummary.containsKey(courseNo)) {
                Map<String, Object> summary = new HashMap<>();
                summary.put("courseNo", courseNo);
                summary.put("totalQuantity", 0);
                summary.put("unitPrice", purchase.getUnitPrice());
                summary.put("purchaseNo", purchase.getPurchaseNo());
                courseSummary.put(courseNo, summary);
            }
            Map<String, Object> summary = courseSummary.get(courseNo);
            summary.put("totalQuantity", (Integer) summary.get("totalQuantity") + purchase.getQuantity());
            // 使用最新的单价
            summary.put("unitPrice", purchase.getUnitPrice());
            summary.put("purchaseNo", purchase.getPurchaseNo());
        }

        // 获取每门课程的库存信息
        for (Map.Entry<Integer, Map<String, Object>> entry : courseSummary.entrySet()) {
            Integer courseNo = entry.getKey();
            Map<String, Object> summary = entry.getValue();

            CourseStock stock = courseStockMapper.getByMemberAndCourse(memberNo, courseNo);
            Course course = courseMapper.getCourseByNo(courseNo);

            if (course != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("courseNo", courseNo);
                item.put("courseName", course.getCourseName());
                item.put("coursePrice", course.getCoursePrice());
                item.put("unitPrice", summary.get("unitPrice")); // 购买时的单价
                item.put("totalQuantity", summary.get("totalQuantity")); // 总购买次数
                item.put("remainTimes", stock != null ? stock.getRemainTimes() : 0); // 剩余次数
                item.put("usedTimes", stock != null ?
                    ((Integer) summary.get("totalQuantity") - stock.getRemainTimes()) : 0); // 已用次数
                item.put("version", stock != null ? stock.getVersion() : 0);

                // 获取对应的递延收益记录（未完成的）
                DeferredRevenue deferred = deferredRevenueMapper.getBySource("PURCHASE", String.valueOf(summary.get("purchaseNo")));
                if (deferred != null) {
                    item.put("deferredId", deferred.getId());
                    item.put("deferredAmount", deferred.getDeferredAmount());
                    item.put("recognizedAmount", deferred.getRecognizedAmount());
                    item.put("deferredRemaining", deferred.getDeferredAmount().subtract(deferred.getRecognizedAmount()));
                } else {
                    item.put("deferredId", null);
                    item.put("deferredAmount", BigDecimal.ZERO);
                    item.put("recognizedAmount", BigDecimal.ZERO);
                    item.put("deferredRemaining", BigDecimal.ZERO);
                }

                // 只有剩余次数>0 或者有待分摊金额的才返回
                Integer remainTimes = (Integer) item.get("remainTimes");
                if (remainTimes == null) remainTimes = 0;
                if (remainTimes > 0 || ((BigDecimal) item.get("deferredRemaining")).compareTo(BigDecimal.ZERO) > 0) {
                    result.add(item);
                }
            }
        }

        return result;
    }

    /**
     * 退课处理（事务内）
     * 1. 校验并扣减库存（含乐观锁）
     * 2. 减少递延收益
     * 3. 增加会员余额
     * 4. 记录内部划转
     * 5. 记录退课明细
     */
    @Transactional
    public Map<String, Object> processCourseRefund(Integer memberNo, Long purchaseNo, Integer courseNo,
                                                    Integer refundTimes, BigDecimal refundAmount,
                                                    String refundReason, Integer operatorAdminId) {
        Map<String, Object> resultMap = new HashMap<>();

        // 1) 参数校验
        if (memberNo == null || courseNo == null || refundTimes == null || refundAmount == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "退课失败，参数不完整");
            return resultMap;
        }

        if (refundTimes <= 0) {
            resultMap.put("code", 400);
            resultMap.put("message", "退课失败，退课次数必须大于0");
            return resultMap;
        }

        // 2) 获取库存信息并校验
        CourseStock stock = courseStockMapper.getByMemberAndCourse(memberNo, courseNo);
        if (stock == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "退课失败，未找到课程库存");
            return resultMap;
        }

        if (stock.getRemainTimes() < refundTimes) {
            resultMap.put("code", 400);
            resultMap.put("message", "退课失败，库存不足，当前剩余 " + stock.getRemainTimes() + " 次");
            return resultMap;
        }

        // 3) 扣减库存（乐观锁）
        int updated = courseStockMapper.deductStockForRefund(memberNo, courseNo, refundTimes, stock.getVersion());
        if (updated == 0) {
            resultMap.put("code", 409);
            resultMap.put("message", "退课失败，库存已被其他操作修改，请重试");
            return resultMap;
        }

        // 4) 获取对应的递延收益记录并减少
        String purchaseNoStr = String.valueOf(purchaseNo);
        DeferredRevenue deferred = deferredRevenueMapper.getBySource("PURCHASE", purchaseNoStr);
        if (deferred != null && deferred.getStatus() == 1) {
            // 减少递延金额
            int deferredUpdated = deferredRevenueMapper.reduceDeferredAmount(deferred.getId(), refundAmount);
            if (deferredUpdated == 0) {
                // 递延收益更新失败，回滚库存
                courseStockMapper.addStock(memberNo, courseNo, refundTimes);
                resultMap.put("code", 500);
                resultMap.put("message", "退课失败，递延收益更新异常");
                return resultMap;
            }
        }

        // 5) 增加会员余额
        Double currentChange = memberMapper.getMemberChange(memberNo);
        if (currentChange == null) currentChange = 0.0;
        int addBalance = memberMapper.updateMemberChangeByMemberNo(memberNo, -refundAmount.doubleValue()); // 注意：这里传负数是增加余额
        if (addBalance == 0) {
            resultMap.put("code", 500);
            resultMap.put("message", "退课失败，余额更新异常");
            return resultMap;
        }

        // 6) 记录内部划转（课程→余额）
        ReceivableTransfer transfer = new ReceivableTransfer();
        transfer.setMemberNo(memberNo);
        transfer.setFromSourceType("COURSE");
        transfer.setFromSourceNo(purchaseNoStr);
        transfer.setToSourceType("RECHARGE"); // 退回到充值余额
        transfer.setAmount(refundAmount);
        transfer.setStatus(1);
        transfer.setRemark("退课返还-课程费→储值余额");
        transfer.setCreateTime(java.time.LocalDateTime.now());
        receivableTransferMapper.insert(transfer);

        // 7) 记录退课明细
        CourseRefund courseRefund = new CourseRefund();
        courseRefund.setMemberNo(memberNo);
        courseRefund.setCourseNo(courseNo);
        courseRefund.setDeferredId(deferred != null ? deferred.getId() : 0);
        courseRefund.setOriginalPurchaseNo(purchaseNoStr);
        courseRefund.setRefundTimes(refundTimes);
        courseRefund.setRefundAmount(refundAmount);
        courseRefund.setRefundChannel("BALANCE"); // 退至余额
        courseRefund.setRefundReason(refundReason);
        courseRefund.setOperatorId(operatorAdminId);
        courseRefund.setStatus(2); // 已完成
        courseRefund.setCreateTime(java.time.LocalDateTime.now());
        courseRefundMapper.insert(courseRefund);

        // 8) 记录库存日志
        CourseStock updatedStock = courseStockMapper.getByMemberAndCourse(memberNo, courseNo);
        CourseStockLog stockLog = new CourseStockLog();
        stockLog.setMemberNo(memberNo);
        stockLog.setCourseNo(courseNo);
        stockLog.setChangeTimes(-refundTimes);
        stockLog.setAfterRemain(updatedStock != null ? updatedStock.getRemainTimes() : 0);
        stockLog.setBizType("REFUND");
        stockLog.setBizNo(String.valueOf(courseRefund.getId()));
        stockLog.setRemark("退课，扣除" + refundTimes + "次，退款" + refundAmount + "元至余额");
        stockLog.setCreateTime(java.time.LocalDateTime.now());
        courseStockLogMapper.insert(stockLog);

        // 9) 记录财务流水
        Double afterChange = memberMapper.getMemberChange(memberNo);
        financeService.addLedger(
                "COURSE_REFUND",
                String.valueOf(courseRefund.getId()),
                memberNo,
                "IN",
                refundAmount,
                "BALANCE",
                BigDecimal.valueOf(afterChange),
                operatorAdminId,
                "退课返还-课程费→储值余额"
        );

        resultMap.put("code", 200);
        resultMap.put("message", "退课成功");
        resultMap.put("refundId", courseRefund.getId());
        resultMap.put("refundAmount", refundAmount);
        resultMap.put("balanceAfter", afterChange);
        return resultMap;
    }

}


