package com.gym.entity;

import lombok.Data;

import java.util.Date;

/**
 * 课程 实体
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Data
public class Course {

    private Integer courseNo;

    private String courseName;

    private String courseDuration;

    private double coursePrice;

    private String courseDesc;

    private int courseIntegral;

    private Integer employeeNo;

    private Integer managerNo;

    private String employeeNameCoach;

    private String employeePhoneCoach;

    private String employeeNameManager;

    private String employeePhoneManager;

    /**
     * 总共购买课时
     */
    private Integer totalTimes;

    /**
     * 剩余可用课时
     */
    private Integer remainTimes;

}
