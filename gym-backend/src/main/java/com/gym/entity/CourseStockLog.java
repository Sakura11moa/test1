package com.gym.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存变更流水
 */
@Data
public class CourseStockLog {

    private Long logNo;

    private Integer memberNo;

    private Integer courseNo;

    private Integer changeTimes;

    private Integer afterRemain;

    private String bizType; // PURCHASE / BOOK / CANCEL_BOOK / ADMIN_ADJUST

    private String bizNo;

    private String remark;

    private LocalDateTime createTime;
}