package com.gym.entity;
import lombok.Data;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class MemberCardType {
    private Integer cardTypeId;
    private String name;
    private Integer days;
    // 修复：cardMonths字段无校验注解问题，原逻辑无参数校验，现新增非空和最小值校验
    @NotNull(message = "分摊月数不能为空")
    @Min(value = 1, message = "分摊月数至少为1")
    private Integer cardMonths;
    private BigDecimal price;
    private Integer status;
}
