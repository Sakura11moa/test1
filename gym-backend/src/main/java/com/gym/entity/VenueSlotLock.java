package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 场地时段锁定 实体类
 * 用于管理员维护、清洁、包场等
 */
@Data
public class VenueSlotLock {
    private Long lockNo;
    private Integer venueNo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String lockType; // MAINTAIN / CLEAN / EVENT
    private String remark;
    private Integer status; // 0有效 / 1解除
    private LocalDateTime createTime;
}
