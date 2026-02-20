package com.gym.mapper;

import com.gym.entity.CourseRefund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Mapper
@Repository
public interface CourseRefundMapper {

    /**
     * 插入退课记录
     */
    int insert(CourseRefund courseRefund);

    /**
     * 根据ID查询
     */
    CourseRefund getById(Long id);

    /**
     * 查询会员的退课记录
     */
    List<CourseRefund> getByMemberNo(Integer memberNo);

    /**
     * 统计退课总金额
     */
    BigDecimal getTotalRefundAmount(@Param("memberNo") Integer memberNo);

    /**
     * 统计指定期间的退课金额
     */
    BigDecimal getTotalRefundAmountByPeriod(@Param("start") String start, @Param("end") String end);

    /**
     * 查询指定期间的退课记录明细
     */
    List<CourseRefund> getByPeriod(@Param("start") String start, @Param("end") String end);
}
