package com.gym.entity;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MemberCardType {
    private Integer cardTypeId;
    private String name;
    private Integer days;
    private Integer cardMonths; // 分摊月数
    private BigDecimal price;
    private Integer status;
}
