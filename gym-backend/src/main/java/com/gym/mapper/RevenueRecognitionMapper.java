package com.gym.mapper;

import com.gym.entity.RevenueRecognition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface RevenueRecognitionMapper {

    /**
     * 插入收入确认记录
     */
    int insert(RevenueRecognition recognition);

    /**
     * 根据ID查询
     */
    RevenueRecognition getById(Long id);

    /**
     * 查询会员的收入确认记录
     */
    List<RevenueRecognition> getByMemberNo(Integer memberNo);

    /**
     * 查询指定期间的收入确认
     */
    List<RevenueRecognition> getByPeriod(String period);

    /**
     * 查询指定递延ID的所有确认记录
     */
    List<RevenueRecognition> getByDeferredId(Long deferredId);

    /**
     * 统计指定期间的收入总额
     */
    BigDecimal getTotalByPeriod(String period);

    /**
     * 统计指定会员的收入总额
     */
    BigDecimal getTotalByMember(Integer memberNo);

    /**
     * 查询时间范围内的收入确认
     */
    List<RevenueRecognition> getByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 统计指定会员和业务类型的收入总额
     */
    BigDecimal getTotalByMemberAndBizType(@Param("memberNo") Integer memberNo, @Param("bizType") String bizType);

    /**
     * 统计指定期间和业务类型的收入总额
     */
    BigDecimal getTotalByPeriodAndBizType(@Param("period") String period, @Param("bizType") String bizType);

    /**
     * 统计指定业务类型的收入总额（全部）
     */
    BigDecimal getTotalByBizType(@Param("bizType") String bizType);
}
