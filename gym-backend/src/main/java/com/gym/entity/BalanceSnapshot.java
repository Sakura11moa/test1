package com.gym.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 财务余额快照实体类
 * 每月初记录上月末的各类负债余额
 */
@Data
public class BalanceSnapshot {
    private Long id;
    private LocalDate snapshotDate;     // 快照日期
    private BigDecimal balanceAmount;   // 储值余额
    private BigDecimal cardDeferredAmount;   // 会籍预收金额
    private BigDecimal courseDeferredAmount; // 课程预收金额
    private LocalDateTime createTime;
}
