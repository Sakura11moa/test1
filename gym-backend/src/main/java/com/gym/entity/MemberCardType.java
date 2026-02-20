package com.gym.entity;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MemberCardType {
    private Integer cardTypeId;
    private String name;
    private Integer days;
    private BigDecimal price;
    private Integer status;
}
