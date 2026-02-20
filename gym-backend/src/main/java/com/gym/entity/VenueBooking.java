package com.gym.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 场地预约记录 实体
 */
@Data
public class VenueBooking {

    private Integer bookingNo;

    private Integer memberNo;

    private Integer courseNo;

    /**
     * 关联的购课单号（用于确认收入时找到正确的递延记录）
     */
    private Integer purchaseNo;

    private Integer venueNo;

    private String timeSlot;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationMinutes;

    /**
     * 本次预约消耗的库存流水号
     */
    private Long consumeLogNo;

    private LocalDateTime bookingTime;

    private LocalDate bookingDate;

    /**
     * 0有效 / 1已取消
     * 0:PENDING(待审), 1:APPROVED(通过), 2:REJECTED(驳回), 3:MEMBER_CANCEL(会员取消), 4:ADMIN_CANCEL(管理取消), 5:TIMEOUT(超时)
     */
    private String status;

    private Integer auditAdminId;

    private LocalDateTime auditTime;

    private String rejectReason;

    private LocalDateTime timeoutAt;

    private Integer version;
}


