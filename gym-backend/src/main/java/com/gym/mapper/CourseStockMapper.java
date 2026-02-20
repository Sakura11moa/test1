package com.gym.mapper;

import com.gym.entity.CourseStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseStockMapper {

    /**
     * 查询某会员某课程的库存
     */
    CourseStock getByMemberAndCourse(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo);

    /**
     * 原子扣减库存（预约时使用）
     * 返回受影响行数，1=成功，0=库存不足
     */
    int deductStock(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo);

    /**
     * 增加库存（购买/取消返还）
     */
    int addStock(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo, @Param("delta") Integer delta);

    /**
     * 查询某会员所有库存>0的课程（用于已购课程列表）
     */
    List<Integer> findCourseNosWithPositiveStock(@Param("memberNo") Integer memberNo);

    /**
     * 查询某会员所有库存>0的课程详情（JOIN course 表）
     */
    List<com.gym.entity.Course> findCoursesWithPositiveStock(@Param("memberNo") Integer memberNo);

    /**
     * 专门用于取消预约返还库存（仅增加 remain_times）
     */
    int returnStock(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo);

    /**
     * 插入/更新库存（购买时使用）
     */
    int upsertStock(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo, @Param("totalTimes") Integer totalTimes, @Param("remainTimes") Integer remainTimes);

    /**
     * 退课：扣减库存（带乐观锁）
     * @return 受影响行数，1=成功，0=库存不足或版本冲突
     */
    int deductStockForRefund(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo, @Param("refundTimes") Integer refundTimes, @Param("currentVersion") Integer currentVersion);
}