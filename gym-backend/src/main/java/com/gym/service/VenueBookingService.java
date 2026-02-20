package com.gym.service;

import com.gym.entity.*;
import com.gym.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场地预约 服务类
 */
@Service
public class VenueBookingService {

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private VenueBookingMapper venueBookingMapper;

    @Resource
    private CourseStockMapper courseStockMapper;

    @Resource
    private CourseStockLogMapper courseStockLogMapper;

    @Resource
    private VenueSlotLockMapper venueSlotLockMapper;

    @Resource
    private BookingAuditLogMapper bookingAuditLogMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private FinanceService financeService;

    @Resource
    private AccrualFinanceService accrualFinanceService;

    @Resource
    private CoursePurchaseMapper coursePurchaseMapper;

    // v3.0 预约配置
    private static final int DURATION_MINUTES = 60; // 统一修改为60分钟
    private static final int TIME_GRANULARITY_MINUTES = 30; // 统一修改为30分钟起点粒度
    private static final LocalTime DEFAULT_OPEN = LocalTime.of(6, 0);
    private static final LocalTime DEFAULT_CLOSE = LocalTime.of(22, 0);
    // 修复：单日预约时长上限（2小时 = 120分钟）
    private static final int MAX_DAY_TOTAL_MINUTES = 120;

    public List<String> getAvailableStartTimes(String dateStr, Integer venueNo) {
        if (dateStr == null || venueNo == null) {
            return new ArrayList<>();
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        Venue venue = null;
        try {
            venue = venueMapper.getVenueByNo(venueNo);
        } catch (Exception ignore) {
        }

        LocalTime openTime = DEFAULT_OPEN;
        LocalTime closeTime = DEFAULT_CLOSE;
        if (venue != null && venue.getOpenTime() != null && venue.getOpenTime().contains("-")) {
            try {
                String[] parts = venue.getOpenTime().split("-");
                openTime = LocalTime.parse(parts[0].trim());
                closeTime = LocalTime.parse(parts[1].trim());
            } catch (Exception ignore) {
            }
        }

        List<String> result = new ArrayList<>();
        LocalTime current = openTime;
        LocalTime lastPossibleStart = closeTime.minusMinutes(DURATION_MINUTES);

        while (!current.isAfter(lastPossibleStart)) {
            LocalDateTime start = LocalDateTime.of(date, current);
            LocalDateTime end = start.plusMinutes(DURATION_MINUTES);
            if (!isSlotOccupied(venueNo, start, end)) {
                result.add(current.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            current = current.plusMinutes(TIME_GRANULARITY_MINUTES);
        }

        return result;
    }

    /**
     * 获取可用时间段及智能推荐Top3
     */
    public Map<String, Object> getTimeOptions(Integer memberNo, Integer courseNo, Integer venueNo, String dateStr) {
        Map<String, Object> resultMap = new HashMap<>();
        LocalDate date = LocalDate.parse(dateStr);
        Venue venue = venueMapper.getVenueByNo(venueNo);
        
        LocalTime openTime = DEFAULT_OPEN;
        LocalTime closeTime = DEFAULT_CLOSE;
        if (venue != null && venue.getOpenTime() != null && venue.getOpenTime().contains("-")) {
            try {
                String[] parts = venue.getOpenTime().split("-");
                openTime = LocalTime.parse(parts[0].trim());
                closeTime = LocalTime.parse(parts[1].trim());
            } catch (Exception e) {
                System.err.println("解析场地营业时间失败: " + venue.getOpenTime());
            }
        }

        List<com.gym.vo.BookingTimeOptionVO> allOptions = new ArrayList<>();
        LocalTime current = openTime;
        LocalTime lastPossibleStart = closeTime.minusMinutes(DURATION_MINUTES);

        while (!current.isAfter(lastPossibleStart)) {
            LocalDateTime start = LocalDateTime.of(date, current);
            LocalDateTime end = start.plusMinutes(DURATION_MINUTES);

            if (!isSlotOccupied(venueNo, start, end)) {
                com.gym.vo.BookingTimeOptionVO option = new com.gym.vo.BookingTimeOptionVO();
                option.setStartTime(current.format(DateTimeFormatter.ofPattern("HH:mm")));
                option.setEndTime(end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                
                int score = 100;
                int pendingCount = venueBookingMapper.findOverlappingBookingsExcl(venueNo, start, end, -1).size();
                score -= (pendingCount * 10);
                
                if (current.isAfter(LocalTime.of(17, 59)) && current.isBefore(LocalTime.of(20, 1))) {
                    score += 20;
                }

                option.setScore(score);
                if (score >= 110) {
                    option.setReason("热门推荐时段");
                } else if (pendingCount == 0) {
                    option.setReason("空闲度高，极速审批");
                } else {
                    option.setReason("可选时段");
                }
                allOptions.add(option);
            }
            current = current.plusMinutes(TIME_GRANULARITY_MINUTES);
        }

        List<com.gym.vo.BookingTimeOptionVO> recommends = new ArrayList<>(allOptions);
        recommends.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        if (recommends.size() > 3) {
            recommends = recommends.subList(0, 3);
        }

        List<String> available = new ArrayList<>();
        for (com.gym.vo.BookingTimeOptionVO opt : allOptions) {
            available.add(opt.getStartTime());
        }

        List<Map<String, Object>> recommend = new ArrayList<>();
        for (com.gym.vo.BookingTimeOptionVO opt : recommends) {
            Map<String, Object> r = new HashMap<>();
            r.put("startTime", opt.getStartTime());
            r.put("reason", opt.getReason());
            recommend.add(r);
        }

        resultMap.put("code", 200);
        resultMap.put("available", available);
        resultMap.put("recommend", recommend);
        return resultMap;
    }

    private boolean isSlotOccupied(Integer venueNo, LocalDateTime start, LocalDateTime end) {
        List<VenueBooking> bookings = venueBookingMapper.findOverlappingBookingsExcl(venueNo, start, end, -1);
        if (bookings != null && !bookings.isEmpty()) return true;
        List<VenueSlotLock> locks = venueSlotLockMapper.findOverlappingLocks(venueNo, start, end);
        return locks != null && !locks.isEmpty();
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bookVenueV3(int memberNo, int courseNo, int venueNo, String dateStr, String startTimeStr) {
        Map<String, Object> resultMap = new HashMap<>();
        LocalDate date = LocalDate.parse(dateStr);
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = startDateTime.plusMinutes(DURATION_MINUTES);

        // ========== 会员卡有效期校验 ==========
        Member member = memberMapper.getMemberByMemberNo(memberNo);
        if (member == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，会员不存在");
            return resultMap;
        }
        // 校验会员卡状态：card_status != 1 表示非有效状态
        if (member.getCardStatus() == null || member.getCardStatus() != 1) {
            String statusMsg = (member.getCardStatus() != null && member.getCardStatus() == 2) ? "已停卡" : "已过期";
            resultMap.put("code", 403);
            resultMap.put("message", "预约失败，" + statusMsg + "，请联系管理员续卡");
            return resultMap;
        }
        // 校验会员卡到期时间
        if (member.getExpireTime() == null || member.getExpireTime().isBefore(LocalDateTime.now())) {
            resultMap.put("code", 403);
            resultMap.put("message", "预约失败，会员卡已到期，请联系管理员续卡");
            return resultMap;
        }
        // ========== 会员卡有效期校验结束 ==========

        LocalTime openTime = DEFAULT_OPEN;
        LocalTime closeTime = DEFAULT_CLOSE;
        try {
            Venue venue = venueMapper.getVenueByNo(venueNo);
            if (venue != null && venue.getOpenTime() != null && venue.getOpenTime().contains("-")) {
                String[] parts = venue.getOpenTime().split("-");
                openTime = LocalTime.parse(parts[0].trim());
                closeTime = LocalTime.parse(parts[1].trim());
            }
        } catch (Exception ignore) {}

        if (startTime.isBefore(openTime) || endDateTime.toLocalTime().isAfter(closeTime)) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，不在营业时间内 (" + openTime + "-" + closeTime + ")");
            return resultMap;
        }

        if (isSlotOccupied(venueNo, startDateTime, endDateTime)) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，该时段已被占用或锁定");
            return resultMap;
        }

        List<VenueBooking> memberBookings = venueBookingMapper.findMemberOverlappingBookingsExcl(memberNo, startDateTime, endDateTime, -1);
        if (memberBookings != null && !memberBookings.isEmpty()) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，您在该时段已有其他预约行程");
            return resultMap;
        }

        // 修复：单日预约时长上限校验（限制单日累计≤2小时）
        Integer existingMinutes = venueBookingMapper.getMemberDayTotalMinutes(memberNo, dateStr);
        if (existingMinutes != null && existingMinutes + DURATION_MINUTES > MAX_DAY_TOTAL_MINUTES) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，您当日已预约" + existingMinutes + "分钟，单日累计上限为" + MAX_DAY_TOTAL_MINUTES + "分钟");
            return resultMap;
        }

        CourseStock stock = courseStockMapper.getByMemberAndCourse(memberNo, courseNo);
        if (stock == null || stock.getRemainTimes() <= 0) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，课程剩余次数不足");
            return resultMap;
        }

        VenueBooking booking = new VenueBooking();
        booking.setMemberNo(memberNo);
        booking.setCourseNo(courseNo);
        booking.setVenueNo(venueNo);
        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        booking.setBookingDate(date);
        booking.setTimeSlot(startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "-" + endDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))); 
        booking.setStatus("0");
        booking.setDurationMinutes(DURATION_MINUTES);
        booking.setTimeoutAt(LocalDateTime.now().plusHours(24));
        booking.setVersion(0);

        int insert = venueBookingMapper.addVenueBooking(booking);
        if (insert > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "预约已提交，请等待审批");
            resultMap.put("bookingNo", booking.getBookingNo());
        } else {
            throw new RuntimeException("预约写入失败");
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> approveBooking(int bookingNo, int adminId) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "审批失败，记录不存在");
            return resultMap;
        }
        if (!com.gym.constant.BookingStatus.PENDING.equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "审批失败，当前状态不是待审批");
            return resultMap;
        }
        if (booking.getConsumeLogNo() != null) {
            resultMap.put("code", 200);
            resultMap.put("message", "已审批，无需重复操作");
            return resultMap;
        }

        int deducted = courseStockMapper.deductStock(booking.getMemberNo(), booking.getCourseNo());
        if (deducted <= 0) {
            // 自动驳回逻辑
            venueBookingMapper.updateBookingStatus(bookingNo, com.gym.constant.BookingStatus.PENDING, com.gym.constant.BookingStatus.REJECTED, adminId, "库存不足，系统自动驳回", booking.getVersion());
            
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("2");
            auditLog.setOperatorType("ADMIN");
            auditLog.setOperatorId(adminId);
            auditLog.setAction("REJECT");
            auditLog.setRemark("审批时发现库存不足，自动驳回");
            bookingAuditLogMapper.insert(auditLog);

            resultMap.put("code", 400);
            resultMap.put("message", "审批失败，该会员课程次数不足，已自动驳回");
            return resultMap;
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, com.gym.constant.BookingStatus.PENDING, com.gym.constant.BookingStatus.APPROVED, adminId, null, booking.getVersion());
        if (updated > 0) {
            CourseStock updatedStock = courseStockMapper.getByMemberAndCourse(booking.getMemberNo(), booking.getCourseNo());
            CourseStockLog log = new CourseStockLog();
            log.setMemberNo(booking.getMemberNo());
            log.setCourseNo(booking.getCourseNo());
            log.setChangeTimes(-1);
            log.setAfterRemain(updatedStock.getRemainTimes());
            log.setBizType("BOOK");
            log.setBizNo(String.valueOf(bookingNo));
            log.setRemark("管理员审批通过扣减");
            log.setCreateTime(LocalDateTime.now());
            courseStockLogMapper.insert(log);

            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("1");
            auditLog.setOperatorType("ADMIN");
            auditLog.setOperatorId(adminId);
            auditLog.setAction("APPROVE");
            bookingAuditLogMapper.insert(auditLog);

            venueBookingMapper.updateConsumeLogNo(bookingNo, log.getLogNo());
            resultMap.put("code", 200);
            resultMap.put("message", "审批成功");
        } else {
            throw new RuntimeException("状态更新冲突");
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rejectBooking(int bookingNo, int adminId, String reason) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null || !"0".equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "驳回失败，记录不存在或状态已变更");
            return resultMap;
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, "0", "2", adminId, reason, booking.getVersion());
        if (updated > 0) {
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("2");
            auditLog.setOperatorType("ADMIN");
            auditLog.setOperatorId(adminId);
            auditLog.setAction("REJECT");
            auditLog.setRemark(reason);
            bookingAuditLogMapper.insert(auditLog);
            resultMap.put("code", 200);
            resultMap.put("message", "已驳回预约");
        } else {
            throw new RuntimeException("状态更新冲突");
        }
        return resultMap;
    }

    public List<VenueBooking> getPendingBookingsByCoach(Integer employeeNo) {
        return venueBookingMapper.getPendingBookingsByCoach(employeeNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> coachApproveBooking(int bookingNo, int employeeNo) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "审批失败，记录不存在");
            return resultMap;
        }
        
        Course course = courseMapper.getCourseByNo(booking.getCourseNo());
        if (course == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "审批失败，课程不存在");
            return resultMap;
        }
        // 教练端权限校验：仅允许审批自己名下课程；若课程未绑定教练(employeeNo为空)，允许教练审批（避免历史数据/未绑定导致403）
        if (course.getEmployeeNo() != null && !course.getEmployeeNo().equals(employeeNo)) {
            resultMap.put("code", 403);
            resultMap.put("message", "审批失败，您无权处理该预约");
            return resultMap;
        }

        if (!com.gym.constant.BookingStatus.PENDING.equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "当前状态不可审批");
            return resultMap;
        }

        int deducted = courseStockMapper.deductStock(booking.getMemberNo(), booking.getCourseNo());
        if (deducted <= 0) {
            // 自动驳回逻辑
            venueBookingMapper.updateBookingStatus(bookingNo, com.gym.constant.BookingStatus.PENDING, com.gym.constant.BookingStatus.REJECTED, null, "库存不足，系统自动驳回", booking.getVersion());
            
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("2");
            auditLog.setOperatorType("COACH");
            auditLog.setOperatorId(employeeNo);
            auditLog.setAction("REJECT");
            auditLog.setRemark("教练审批时发现库存不足，自动驳回");
            bookingAuditLogMapper.insert(auditLog);

            resultMap.put("code", 400);
            resultMap.put("message", "审批失败，该会员课程次数不足，已自动驳回");
            return resultMap;
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, "0", "1", null, null, booking.getVersion());
        if (updated > 0) {
            CourseStock updatedStock = courseStockMapper.getByMemberAndCourse(booking.getMemberNo(), booking.getCourseNo());
            CourseStockLog log = new CourseStockLog();
            log.setMemberNo(booking.getMemberNo());
            log.setCourseNo(booking.getCourseNo());
            log.setChangeTimes(-1);
            log.setAfterRemain(updatedStock.getRemainTimes());
            log.setBizType("BOOK");
            log.setBizNo(String.valueOf(bookingNo));
            log.setRemark("教练审批通过扣减");
            log.setCreateTime(LocalDateTime.now());
            courseStockLogMapper.insert(log);

            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("1");
            auditLog.setOperatorType("COACH");
            auditLog.setOperatorId(employeeNo);
            auditLog.setAction("APPROVE");
            bookingAuditLogMapper.insert(auditLog);

            venueBookingMapper.updateConsumeLogNo(bookingNo, log.getLogNo());
            resultMap.put("code", 200);
            resultMap.put("message", "教练审批成功");
        } else {
            throw new RuntimeException("状态更新冲突");
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> coachRejectBooking(int bookingNo, int employeeNo, String reason) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "记录不存在");
            return resultMap;
        }
        
        Course course = courseMapper.getCourseByNo(booking.getCourseNo());
        if (course == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "驳回失败，课程不存在");
            return resultMap;
        }
        
        // 教练端权限校验：仅允许驳回自己名下课程；若课程未绑定教练，允许教练驳回
        if (course.getEmployeeNo() != null && !course.getEmployeeNo().equals(employeeNo)) {
            resultMap.put("code", 403);
            resultMap.put("message", "无权操作");
            return resultMap;
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, "0", "2", null, reason, booking.getVersion());
        if (updated > 0) {
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("2");
            auditLog.setOperatorType("COACH");
            auditLog.setOperatorId(employeeNo);
            auditLog.setAction("REJECT");
            auditLog.setRemark(reason);
            bookingAuditLogMapper.insert(auditLog);
            resultMap.put("code", 200);
            resultMap.put("message", "已驳回预约");
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> coachFinishBooking(int bookingNo, int employeeNo) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null || !com.gym.constant.BookingStatus.APPROVED.equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "不可核销");
            return resultMap;
        }

        Course course = courseMapper.getCourseByNo(booking.getCourseNo());
        if (course == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "核销失败，课程不存在");
            return resultMap;
        }

        // 权限校验：仅允许核销自己负责的课程；若课程未绑定教练，允许核销
        if (course.getEmployeeNo() != null && !course.getEmployeeNo().equals(employeeNo)) {
            resultMap.put("code", 403);
            resultMap.put("message", "无权核销");
            return resultMap;
        }

        // 【权责发生制】课程完成时确认收入
        // 计算每节课的收入金额
        // 优先使用购课记录的实际成交价格，如果找不到则使用课程标准价格
        BigDecimal coursePrice;
        Integer purchaseNo = findPurchaseNoFromBooking(bookingNo, booking.getMemberNo(), booking.getCourseNo());
        if (purchaseNo != null) {
            CoursePurchase purchase = coursePurchaseMapper.getByPurchaseNo(purchaseNo.longValue());
            if (purchase != null && purchase.getTotalAmount() != null && purchase.getQuantity() != null && purchase.getQuantity() > 0) {
                // 单节课程收入 = 购课总价 / 购买次数
                coursePrice = purchase.getTotalAmount().divide(BigDecimal.valueOf(purchase.getQuantity()), 2, java.math.RoundingMode.HALF_UP);
            } else {
                coursePrice = BigDecimal.valueOf(course.getCoursePrice());
            }
        } else {
            coursePrice = BigDecimal.valueOf(course.getCoursePrice());
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, "1", com.gym.constant.BookingStatus.FINISHED, null, null, booking.getVersion());
        if (updated > 0) {
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("1");
            auditLog.setNewStatus(com.gym.constant.BookingStatus.FINISHED);
            auditLog.setOperatorType("COACH");
            auditLog.setOperatorId(employeeNo);
            auditLog.setAction("FINISH");
            bookingAuditLogMapper.insert(auditLog);

            // 【权责发生制】确认课程收入
            if (purchaseNo != null) {
                accrualFinanceService.recognizeCourseByBookingWithPurchase(
                        booking.getMemberNo(),
                        String.valueOf(bookingNo),
                        purchaseNo,
                        coursePrice
                );
            } else {
                // Fallback: 使用旧逻辑
            accrualFinanceService.recognizeCourseByBooking(
                    booking.getMemberNo(),
                    String.valueOf(bookingNo),
                    coursePrice
            );
            }

            resultMap.put("code", 200);
            resultMap.put("message", "核销成功（课程收入已确认）");
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> coachMarkNoShow(int bookingNo, int employeeNo) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null || !com.gym.constant.BookingStatus.APPROVED.equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "不可标记爽约");
            return resultMap;
        }

        Course course = courseMapper.getCourseByNo(booking.getCourseNo());
        if (course == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "标记失败，课程不存在");
            return resultMap;
        }

        // 权限校验：仅允许操作自己负责的课程；若课程未绑定教练，允许操作
        if (course.getEmployeeNo() != null && !course.getEmployeeNo().equals(employeeNo)) {
            resultMap.put("code", 403);
            resultMap.put("message", "无权操作");
            return resultMap;
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, "1", com.gym.constant.BookingStatus.NO_SHOW, null, null, booking.getVersion());
        if (updated > 0) {
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("1");
            auditLog.setNewStatus(com.gym.constant.BookingStatus.NO_SHOW);
            auditLog.setOperatorType("COACH");
            auditLog.setOperatorId(employeeNo);
            auditLog.setAction("NOSHOW");
            bookingAuditLogMapper.insert(auditLog);
            resultMap.put("code", 200);
            resultMap.put("message", "已标记为爽约");
        }
        return resultMap;
    }

    public List<VenueBooking> getPendingBookings() {
        return venueBookingMapper.getPendingBookings();
    }

    public List<AdminVenueBookingVO> getAllBookings() {
        return venueBookingMapper.getAllBookingsWithDetails();
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelPendingBooking(int bookingNo, int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null || !booking.getMemberNo().equals(memberNo)) {
            resultMap.put("code", 400);
            resultMap.put("message", "记录不存在或无权操作");
            return resultMap;
        }
        if (!"0".equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "当前状态不可撤回");
            return resultMap;
        }

        int updated = venueBookingMapper.updateBookingStatus(bookingNo, "0", "3", null, "会员自主撤回", booking.getVersion());
        if (updated > 0) {
            BookingAuditLog auditLog = new BookingAuditLog();
            auditLog.setBookingNo(bookingNo);
            auditLog.setOldStatus("0");
            auditLog.setNewStatus("3");
            auditLog.setOperatorType("MEMBER");
            auditLog.setOperatorId(memberNo);
            auditLog.setAction("MEMBER_CANCEL");
            bookingAuditLogMapper.insert(auditLog);
            resultMap.put("code", 200);
            resultMap.put("message", "已撤回预约");
        }
        return resultMap;
    }

    public List<Venue> getAvailableVenuesBySlot(String date, String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            LocalDateTime start = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(times[0].trim()));
            LocalDateTime end = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(times[1].trim()));
            return venueMapper.findAvailableVenues(start, end);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<MyVenueBooking> getMyVenueBookings(int memberNo) {
        List<MyVenueBooking> bookings = venueBookingMapper.getMyVenueBookings(memberNo);
        for (MyVenueBooking b : bookings) {
            // 修正判定逻辑：只有 1:APPROVED(待参加) 且日期还没到的才不算结束；
            // 只要状态是 6:FINISHED 或 7:NO_SHOW，或者状态是 2:REJECTED, 3:MEMBER_CANCEL, 4:ADMIN_CANCEL, 5:TIMEOUT 都视为结束
            String s = b.getStatus();
            boolean isFinalStatus = "2".equals(s) || "3".equals(s) || "4".equals(s) || "5".equals(s) || "6".equals(s) || "7".equals(s);
            b.setIsFinished(isFinalStatus);
            if (b.getIsEvaluated() == null) b.setIsEvaluated(false);
        }
        return bookings;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelVenueBooking(int bookingNo, int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(bookingNo);
        if (booking == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "记录不存在");
            return resultMap;
        }
        if (!(com.gym.constant.BookingStatus.PENDING.equals(booking.getStatus()) || com.gym.constant.BookingStatus.APPROVED.equals(booking.getStatus()))) {
            resultMap.put("code", 400);
            resultMap.put("message", "当前状态不可取消");
            return resultMap;
        }

        boolean shouldReturnStock = com.gym.constant.BookingStatus.APPROVED.equals(booking.getStatus());
        int cancel = venueBookingMapper.cancelVenueBooking(bookingNo, memberNo);
        if (cancel == 1) {
            if (shouldReturnStock) {
                courseStockMapper.returnStock(memberNo, booking.getCourseNo());
                CourseStock stock = courseStockMapper.getByMemberAndCourse(memberNo, booking.getCourseNo());
                CourseStockLog log = new CourseStockLog();
                log.setMemberNo(memberNo);
                log.setCourseNo(booking.getCourseNo());
                log.setChangeTimes(1);
                log.setAfterRemain(stock.getRemainTimes());
                log.setBizType("CANCEL_BOOK");
                log.setBizNo(String.valueOf(bookingNo));
                log.setRemark("取消预约返还");
                log.setCreateTime(LocalDateTime.now());
                courseStockLogMapper.insert(log);
            }
            resultMap.put("code", 200);
            resultMap.put("message", "取消成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "操作失败");
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bookVenue(int memberNo, int courseNo, int venueNo, String timeSlot, String bookingDate) {
        Map<String, Object> resultMap = new HashMap<>();
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，时间段不能为空");
            return resultMap;
        }
        if (bookingDate == null || bookingDate.trim().isEmpty()) {
            bookingDate = java.time.LocalDate.now().toString();
        }
        int memberConflict = venueBookingMapper.countMemberActiveBookingsBySlot(memberNo, bookingDate, timeSlot);
        if (memberConflict > 0) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，您在该时段已有其他预约");
            return resultMap;
        }
        int deducted = courseStockMapper.deductStock(memberNo, courseNo);
        if (deducted <= 0) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约失败，课程剩余次数不足");
            return resultMap;
        }
        try {
            VenueBooking booking = new VenueBooking();
            booking.setMemberNo(memberNo);
            booking.setCourseNo(courseNo);
            booking.setVenueNo(venueNo);
            booking.setTimeSlot(timeSlot);
            booking.setBookingDate(java.time.LocalDate.parse(bookingDate));
            booking.setStatus("0");
            int insert = venueBookingMapper.addVenueBooking(booking);
            if (insert > 0) {
                CourseStock stock = courseStockMapper.getByMemberAndCourse(memberNo, courseNo);
                CourseStockLog log = new CourseStockLog();
                log.setMemberNo(memberNo);
                log.setCourseNo(courseNo);
                log.setChangeTimes(-1);
                log.setAfterRemain(stock.getRemainTimes());
                log.setBizType("BOOK");
                log.setBizNo(String.valueOf(booking.getBookingNo()));
                log.setRemark("预约课程占用次数");
                log.setCreateTime(java.time.LocalDateTime.now());
                courseStockLogMapper.insert(log);
                venueBookingMapper.updateConsumeLogNo(booking.getBookingNo(), log.getLogNo());
                resultMap.put("code", 200);
                resultMap.put("message", "预约成功");
            } else {
                throw new RuntimeException("预约记录写入失败");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultMap;
    }

    /**
     * 从预约记录中找到购课单号
     * 通过查找课程库存日志，找到该预约使用的购课记录
     */
    private Integer findPurchaseNoFromBooking(int bookingNo, int memberNo, int courseNo) {
        try {
            // 1. 查找该预约的库存流水记录
            CourseStockLog log = courseStockLogMapper.getLatestPurchaseLog(memberNo, courseNo);
            if (log != null && log.getBizNo() != null) {
                return Integer.parseInt(log.getBizNo());
            }
        } catch (Exception e) {
            // 日志解析失败，返回 null，使用旧逻辑
        }
        return null;
    }
}
