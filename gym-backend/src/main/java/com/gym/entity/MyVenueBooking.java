package com.gym.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的预约 返回VO
 */
@Data
public class MyVenueBooking {
    private Integer bookingNo;
    private String courseName;
    private String venueName;
    private String venueLocation;
    private LocalDateTime bookingTime;
    /**
     * 预约结束时间
     */
    private java.time.LocalDateTime endTime;
    /**
     * 预约开始时间
     */
    private java.time.LocalDateTime startTime;
    /**
     * 预约日期
     */
    private java.time.LocalDate bookingDate;
    /**
     * 是否已结束
     */
    private Boolean isFinished;
    /**
     * 是否已评价
     */
    private Boolean isEvaluated;
    /**
     * 本次预约消耗的库存流水号（用于取消时返还）
     */
    private Long consumeLogNo;
    /**
     * 0有效 / 1已取消
     * 0:PENDING(待审), 1:APPROVED(通过), 2:REJECTED(驳回), 3:MEMBER_CANCEL(会员取消), 4:ADMIN_CANCEL(管理取消), 5:TIMEOUT(超时)
     */
    private String status;

    private String rejectReason;

    private java.time.LocalDateTime auditTime;
}


