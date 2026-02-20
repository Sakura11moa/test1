package com.gym.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberCardRenewal {
    private Long renewalNo;
    private Integer memberNo;
    private Integer cardTypeId;
    private Integer daysAdded;
    private BigDecimal amount;
    private String payChannel;
    private LocalDateTime oldExpireTime;
    private LocalDateTime newExpireTime;
    private Integer operatorAdminId;
    private String remark;
    private LocalDateTime createTime;
}
