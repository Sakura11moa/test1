package com.gym.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程购买订单/流水
 */
@Data
public class CoursePurchase {

    private Long purchaseNo;

    private Integer memberNo;

    private Integer courseNo;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private String status; // PAID / CANCELLED / TIMEOUT

    private LocalDateTime createTime;
}