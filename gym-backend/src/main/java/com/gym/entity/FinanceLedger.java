package com.gym.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceLedger {
    private Long ledgerNo;
    private Integer memberNo;
    private String memberName;      // 会员姓名
    private String memberPhone;     // 会员手机号
    private String bizType;
    private String bizNo;
    private String direction;
    private BigDecimal amount;
    private String channel;
    private BigDecimal balanceAfter;
    private Integer operatorAdminId;
    private String remark;
    private LocalDateTime createTime;
}
