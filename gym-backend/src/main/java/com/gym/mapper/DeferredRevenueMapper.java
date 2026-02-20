package com.gym.mapper;

import com.gym.entity.DeferredRevenue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Mapper
@Repository
public interface DeferredRevenueMapper {

    /**
     * 插入递延收益记录
     */
    int insert(DeferredRevenue deferredRevenue);

    /**
     * 根据ID查询
     */
    DeferredRevenue getById(Long id);

    /**
     * 根据来源查询（幂等）
     */
    DeferredRevenue getBySource(@Param("sourceType") String sourceType, @Param("sourceNo") String sourceNo);

    /**
     * 更新已确认金额
     */
    int updateRecognizedAmount(@Param("id") Long id,
                               @Param("recognizedAmount") BigDecimal recognizedAmount,
                               @Param("recognizedPeriods") Integer recognizedPeriods);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询待分摊的记录
     */
    List<DeferredRevenue> getPendingList(@Param("memberNo") Integer memberNo);

    /**
     * 查询进行中的递延收益
     */
    List<DeferredRevenue> getActiveList(@Param("bizType") String bizType);

    /**
     * 查询会员的所有递延收益
     */
    List<DeferredRevenue> getByMemberNo(Integer memberNo);

    /**
     * 统计待分摊总金额
     */
    BigDecimal getTotalDeferredAmount(@Param("memberNo") Integer memberNo, @Param("bizType") String bizType);

    /**
     * 统计待分摊总金额（按渠道）
     */
    BigDecimal getTotalDeferredAmountByChannel(@Param("memberNo") Integer memberNo,
                                               @Param("bizType") String bizType,
                                               @Param("sourceChannel") String sourceChannel);

    /**
     * 查询到期时间分布（用于风险预警）
     * 返回：1个月内、1-3个月、3-6个月、6个月以上
     */
    List<DeferredRevenue> getMaturityDistribution();

    /**
     * 按生效日期统计本月新增会籍负债
     * @param yearMonth 统计月份 (YYYY-MM)
     * @return 本月新增会籍预收金额
     */
    BigDecimal getNewCardLiabilityByEffectiveDate(@Param("yearMonth") String yearMonth);

    /**
     * 按生效日期统计本月新增课程负债
     * @param yearMonth 统计月份 (YYYY-MM)
     * @return 本月新增课程预收金额
     */
    BigDecimal getNewCourseLiabilityByStartDate(@Param("yearMonth") String yearMonth);

    /**
     * 按生效日期和渠道统计本月新增会籍负债（用于区分现金/余额来源）
     * @param yearMonth 统计月份 (YYYY-MM)
     * @param sourceChannel 资金来源渠道：CASH-现金, BALANCE-余额
     * @return 本月新增会籍预收金额
     */
    BigDecimal getNewCardLiabilityByStartDateAndChannel(@Param("yearMonth") String yearMonth, @Param("sourceChannel") String sourceChannel);

    /**
     * 退课：减少递延金额
     * @param id 递延收益ID
     * @param refundAmount 退课金额
     * @return 受影响行数
     */
    int reduceDeferredAmount(@Param("id") Long id, @Param("refundAmount") BigDecimal refundAmount);
}
