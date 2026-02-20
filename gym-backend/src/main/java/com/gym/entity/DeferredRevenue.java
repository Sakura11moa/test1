package com.gym.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 递延收益实体类
 * 记录预收款项的来源和待分摊金额
 */
@Data
public class DeferredRevenue {
    private Long id;
    private Integer memberNo;           // 会员编号
    private String sourceType;           // 来源类型：RECHARGE-充值续入, CARD_RENEW-续卡, PURCHASE-购课
    private String sourceNo;             // 来源单号
    private BigDecimal sourceAmount;     // 原始预收金额
    private BigDecimal deferredAmount;   // 待分摊递延金额
    private BigDecimal recognizedAmount; // 已确认收入金额
    private String bizType;              // 业务类型：CARD-会籍费, COURSE-课程费
    private String sourceChannel;        // 资金来源渠道：CASH-现金, BALANCE-余额
    private Integer totalPeriods;        // 总分摊期数
    private Integer recognizedPeriods;  // 已分摊期数
    private Integer status;             // 状态：1-进行中, 2-已完成, 3-已取消
    private LocalDate startDate;        // 生效日期（开始计算摊销的日期）
    private LocalDate endDate;          // 结束分摊日期
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
