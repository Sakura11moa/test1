package com.gym.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预收账款内部划转记录实体类
 */
@Data
public class ReceivableTransfer {
    private Long id;
    private Integer memberNo;           // 会员编号
    private String fromSourceType;      // 转出来源：RECHARGE-充值余额
    private String fromSourceNo;        // 转出来源单号
    private String toSourceType;        // 转入类型：CARD_RENEW-会籍费, COURSE-课程费
    private String toRenewalNo;        // 关联续卡号
    private BigDecimal amount;         // 划转金额
    private Integer status;            // 状态：1-有效, 2-已取消
    private String remark;
    private LocalDateTime createTime;
}
