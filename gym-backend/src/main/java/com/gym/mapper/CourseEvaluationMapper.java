package com.gym.mapper;

import com.gym.entity.CourseEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseEvaluationMapper {

    int insert(CourseEvaluation evaluation);

    CourseEvaluation getByBookingNo(@Param("bookingNo") Integer bookingNo);

    List<CourseEvaluation> listByCourseNo(@Param("courseNo") Integer courseNo);
}
