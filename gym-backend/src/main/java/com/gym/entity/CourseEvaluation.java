package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程评价 实体类
 */
@Data
public class CourseEvaluation {
    private Long evalNo;
    private Integer bookingNo;
    private Integer memberNo;
    private Integer courseNo;
    private Integer score;
    private String content;
    private LocalDateTime createTime;
}
