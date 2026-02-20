package com.gym.entity;

import lombok.Data;

/**
 * 器材 实体
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Data
public class Equipment {

    private Integer equipmentNo;

    private String equipmentName;

    private String equipmentLocation;

    private Integer equipmentState;

    private String equipmentMessage;

}
