package com.gym.entity;

import lombok.Data;

/**
 * 管理员 实体
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Data
public class Admin {

    private Integer adminNo;

    private String adminAccount;

    private String adminPassword;

    private Integer employeeNo;

    private Integer employeeJob;

    private String token;
}
