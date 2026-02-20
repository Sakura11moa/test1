package com.gym.mapper;

import com.gym.entity.CourseStockLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseStockLogMapper {

    int insert(CourseStockLog log);

    List<CourseStockLog> listByMemberAndCourse(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo);

    /**
     * 查询会员购买指定课程的最新购课记录（包含购课单号）
     */
    CourseStockLog getLatestPurchaseLog(@Param("memberNo") Integer memberNo, @Param("courseNo") Integer courseNo);
}