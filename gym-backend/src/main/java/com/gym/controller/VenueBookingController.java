package com.gym.controller;

import com.gym.entity.MyVenueBooking;
import com.gym.service.VenueBookingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 场地预约 控制层（会员端）
 */
@RestController
@RequestMapping("/api")
public class VenueBookingController {

    @Resource
    private VenueBookingService venueBookingService;

    /**
     * 预约场地（绑定课程+时段+日期）
     * POST /bookVenue
     */
    @RequestMapping(path = "/bookVenue")
    @CrossOrigin
    public Map<String, Object> bookVenue(int memberNo, int courseNo, int venueNo, String timeSlot, String bookingDate) {
        return venueBookingService.bookVenue(memberNo, courseNo, venueNo, timeSlot, bookingDate);
    }

    /**
     * 查询我的预约
     * GET /getMyVenueBookings
     */
    @RequestMapping(path = "/getMyVenueBookings")
    @CrossOrigin
    public List<MyVenueBooking> getMyVenueBookings(int memberNo) {
        return venueBookingService.getMyVenueBookings(memberNo);
    }

    /**
     * 根据日期和时段查询可用的场地列表
     * GET /getAvailableVenuesBySlot?date=2024-08-01&timeSlot=17:00-19:00
     */
    @RequestMapping(path = "/getAvailableVenuesBySlot")
    @CrossOrigin
    public List<com.gym.entity.Venue> getAvailableVenuesBySlot(String date, String timeSlot) {
        return venueBookingService.getAvailableVenuesBySlot(date, timeSlot);
    }

    /**
     * v3.0: 根据日期和场地查询所有可选的开始时间点（15分钟粒度，45分钟时长）
     * GET /getAvailableStartTimes?date=2026-02-08&venueNo=1
     */
    @RequestMapping(path = "/getAvailableStartTimes")
    @CrossOrigin
    public List<String> getAvailableStartTimes(String date, Integer venueNo) {
        return venueBookingService.getAvailableStartTimes(date, venueNo);
    }

    /**
     * v3.0: 灵活预约接口（45分钟时长）
     * POST /bookVenueV3
     */
    @PostMapping(path = "/bookVenueV3")
    @CrossOrigin
    public Map<String, Object> bookVenueV3(@RequestBody Map<String, Object> body) {
        Integer memberNo = body.get("memberNo") == null ? null : Integer.valueOf(body.get("memberNo").toString());
        Integer courseNo = body.get("courseNo") == null ? null : Integer.valueOf(body.get("courseNo").toString());
        Integer venueNo = body.get("venueNo") == null ? null : Integer.valueOf(body.get("venueNo").toString());
        String bookingDate = body.get("bookingDate") == null ? null : body.get("bookingDate").toString();
        String startTime = body.get("startTime") == null ? null : body.get("startTime").toString();

        Map<String, Object> result = new java.util.HashMap<>();
        if (memberNo == null || courseNo == null || venueNo == null || bookingDate == null || startTime == null) {
            result.put("code", 400);
            result.put("message", "预约失败，参数不能为空");
            return result;
        }

        return venueBookingService.bookVenueV3(memberNo, courseNo, venueNo, bookingDate, startTime);
    }

    /**
     * v3.1: 获取会员端可用时间段及智能推荐Top3
     * 联动：课程-场地-日期-时间段
     * GET /api/booking/timeOptions
     */
    @RequestMapping(path = "/booking/timeOptions")
    @CrossOrigin
    public Map<String, Object> getTimeOptions(Integer memberNo, Integer courseNo, Integer venueNo, String date) {
        if (memberNo == null || courseNo == null || venueNo == null || date == null) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("code", 400);
            result.put("message", "参数不能为空");
            return result;
        }
        return venueBookingService.getTimeOptions(memberNo, courseNo, venueNo, date);
    }

    /**
     * 获取待审批预约列表 (管理端)
     * GET /api/admin/getPendingBookings
     */
    @RequestMapping(path = "/admin/getPendingBookings")
    @CrossOrigin
    public List<com.gym.entity.VenueBooking> getPendingBookings() {
        return venueBookingService.getPendingBookings();
    }

    /**
     * 获取全量预约记录 (管理端 - 运营/统计)
     * GET /api/admin/getAllBookings
     */
    @RequestMapping(path = "/admin/getAllBookings")
    @CrossOrigin
    public List<com.gym.entity.AdminVenueBookingVO> getAllBookings() {
        return venueBookingService.getAllBookings();
    }

    /**
     * 审批通过 (管理端)
     * POST /api/admin/approveBooking
     */
    @RequestMapping(path = "/admin/approveBooking")
    @CrossOrigin
    public Map<String, Object> approveBooking(int bookingNo, int adminId) {
        return venueBookingService.approveBooking(bookingNo, adminId);
    }

    /**
     * 审批驳回 (管理端)
     * POST /api/admin/rejectBooking
     */
    @RequestMapping(path = "/admin/rejectBooking")
    @CrossOrigin
    public Map<String, Object> rejectBooking(int bookingNo, int adminId, String reason) {
        return venueBookingService.rejectBooking(bookingNo, adminId, reason);
    }

    /**
     * 教练端：获取我负责的待审批预约
     * GET /api/coach/getPendingBookings?employeeNo=1
     */
    @RequestMapping(path = "/coach/getPendingBookings")
    @CrossOrigin
    public List<com.gym.entity.VenueBooking> getPendingBookingsByCoach(Integer employeeNo) {
        return venueBookingService.getPendingBookingsByCoach(employeeNo);
    }

    /**
     * 教练端：审批通过
     * POST /api/coach/approveBooking
     */
    @RequestMapping(path = "/coach/approveBooking")
    @CrossOrigin
    public Map<String, Object> coachApproveBooking(int bookingNo, int employeeNo) {
        return venueBookingService.coachApproveBooking(bookingNo, employeeNo);
    }

    /**
     * 教练端：审批驳回
     * POST /api/coach/rejectBooking
     */
    @RequestMapping(path = "/coach/rejectBooking")
    @CrossOrigin
    public Map<String, Object> coachRejectBooking(int bookingNo, int employeeNo, String reason) {
        return venueBookingService.coachRejectBooking(bookingNo, employeeNo, reason);
    }

    /**
     * 教练端：核销完成
     * POST /api/coach/finishBooking
     */
    @RequestMapping(path = "/coach/finishBooking")
    @CrossOrigin
    public Map<String, Object> coachFinishBooking(int bookingNo, int employeeNo) {
        return venueBookingService.coachFinishBooking(bookingNo, employeeNo);
    }

    /**
     * 教练端：标记爽约
     * POST /api/coach/markNoShow
     */
    @RequestMapping(path = "/coach/markNoShow")
    @CrossOrigin
    public Map<String, Object> coachMarkNoShow(int bookingNo, int employeeNo) {
        return venueBookingService.coachMarkNoShow(bookingNo, employeeNo);
    }

    /**
     * 会员取消待审批申请 (撤回申请)
     * POST /api/member/cancelPendingBooking
     */
    @RequestMapping(path = "/member/cancelPendingBooking")
    @CrossOrigin
    public Map<String, Object> cancelPendingBooking(int bookingNo, int memberNo) {
        return venueBookingService.cancelPendingBooking(bookingNo, memberNo);
    }

    /**
     * 取消预约
     * POST /cancelVenueBooking
     */
    @RequestMapping(path = "/cancelVenueBooking")
    @CrossOrigin
    public Map<String, Object> cancelVenueBooking(String bookingNo, String memberNo) {
        try {
            // 记录原始输入以便排查
            System.out.println("[DIAGNOSTIC] 接口接收参数: bookingNo=" + bookingNo + ", memberNo=" + memberNo);
            
            if (bookingNo == null || memberNo == null) {
                Map<String, Object> errorMap = new java.util.HashMap<>();
                errorMap.put("code", 400);
                errorMap.put("message", "参数缺失: bookingNo 或 memberNo 不能为空 (当前值: bookingNo=" + bookingNo + ", memberNo=" + memberNo + ")");
                return errorMap;
            }
            
            int bNo = Integer.parseInt(bookingNo.trim());
            int mNo = Integer.parseInt(memberNo.trim());
            return venueBookingService.cancelVenueBooking(bNo, mNo);
        } catch (NumberFormatException e) {
            Map<String, Object> errorMap = new java.util.HashMap<>();
            errorMap.put("code", 400);
            errorMap.put("message", "参数格式错误: 必须为数字 (bookingNo=" + bookingNo + ", memberNo=" + memberNo + ")");
            return errorMap;
        } catch (Exception e) {
            Map<String, Object> errorMap = new java.util.HashMap<>();
            errorMap.put("code", 500);
            errorMap.put("message", "接口处理异常: " + e.toString());
            return errorMap;
        }
    }
}


