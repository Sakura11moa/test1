package com.gym.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * 签到表 实体
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */

@Data
public class CheckIn {

    private Integer checkNo;

    private Integer memberNo;

    private LocalDate checkDate;

}
