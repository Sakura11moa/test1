package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 管理员端 全量预约信息 VO
 */
@Data
public class AdminVenueBookingVO {
    // 预约基础信息
    private Integer bookingNo;
    private String status;
    private String timeSlot;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate bookingDate;
    private LocalDateTime bookingTime;
    
    // 会员信息
    private Integer memberNo;
    private String memberName;
    private String memberPhone;
    
    // 课程信息
    private Integer courseNo;
    private String courseName;
    private Integer employeeNo;
    private String employeeNameCoach;
    
    // 场地信息
    private Integer venueNo;
    private String venueName;
    private String venueLocation;
    
    // 审批信息
    private String rejectReason;
    private LocalDateTime auditTime;
    private Integer auditAdminId;
}
