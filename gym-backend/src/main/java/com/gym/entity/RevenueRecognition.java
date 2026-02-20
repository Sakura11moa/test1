package com.gym.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收入确认记录实体类
 * 记录每笔已确认的收入
 */
@Data
public class RevenueRecognition {
    private Long id;
    private Integer memberNo;           // 会员编号
    private String memberName;           // 会员姓名
    private Long deferredId;            // 关联递延收益ID
    private String bizType;             // 业务类型：CARD-会籍费, COURSE-课程费
    private String bizTypeName;         // 业务类型名称
    private String recognitionType;    // 确认方式：MONTHLY-按月分摊, COURSE_COMPLETE-课程核销
    private BigDecimal amount;          // 本次确认金额
    private String period;             // 分摊期间(YYYY-MM)或课程完成日期
    private String relatedBizNo;        // 关联业务单号
    private String sourceBiz;           // 来源业务描述
    private String courseName;          // 课程名称（课程核销时使用）
    private Integer courseNo;           // 课程编号（课程核销时使用）
    private String remark;
    private LocalDateTime createTime;
}
