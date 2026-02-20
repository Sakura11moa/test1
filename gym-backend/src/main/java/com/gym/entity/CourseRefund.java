package com.gym.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退课记录实体类
 */
@Data
public class CourseRefund {
    private Long id;
    private Integer memberNo;           // 会员编号
    private String memberName;          // 会员姓名
    private Integer courseNo;            // 课程编号
    private String courseName;          // 课程名称
    private Long deferredId;            // 关联递延收益ID
    private String originalPurchaseNo;  // 原购课单号
    private Integer refundTimes;        // 退课次数
    private BigDecimal refundAmount;    // 退课金额
    private String refundChannel;       // 退款方式：CASH-现金退回, BALANCE-退至余额
    private String refundChannelName;   // 退款方式名称
    private String refundReason;         // 退课原因
    private Integer operatorId;          // 操作人ID
    private Integer status;            // 状态：1-进行中, 2-已完成, 3-已取消
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
