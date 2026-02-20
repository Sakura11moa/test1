package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约审批日志 实体类
 */
@Data
public class BookingAuditLog {
    private Long id;
    private Integer bookingNo;
    private String oldStatus;
    private String newStatus;
    private String operatorType; // ADMIN / COACH
    private Integer operatorId;
    private String action; // APPROVE / REJECT / CANCEL / FINISH / NOSHOW
    private String remark;
    private LocalDateTime createTime;
}
