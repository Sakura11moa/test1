package com.gym.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程库存账户（每会员每课程一条）
 */
@Data
public class CourseStock {

    private Long id;

    private Integer memberNo;

    private Integer courseNo;

    private Integer totalTimes;

    private Integer remainTimes;

    private Integer version;

    private LocalDateTime updateTime;
}