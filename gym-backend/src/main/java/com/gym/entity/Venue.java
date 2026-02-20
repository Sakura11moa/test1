package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 场地 实体
 *
 * @author: ShanZhu
 * @date: 2024-12-26
 */
@Data
public class Venue {

    private Integer venueNo;

    private String venueName;

    private String venueType;

    private String venueLocation;

    private Integer venueCapacity;

    private String venueState;

    private String openTime;

    private String venueMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private java.util.Map<String, Boolean> bookedSlotsByTimeSlot;

}
