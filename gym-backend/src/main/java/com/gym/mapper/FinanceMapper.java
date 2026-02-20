package com.gym.mapper;

import com.gym.entity.FinanceLedger;
import com.gym.entity.MemberCardRenewal;
import com.gym.entity.MemberCardType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FinanceMapper {

    // 卡项配置
    List<MemberCardType> getAllCardTypes();
    MemberCardType getCardTypeById(Integer cardTypeId);

    // 续卡记录
    int insertRenewal(MemberCardRenewal renewal);
    List<MemberCardRenewal> getRenewalList(@Param("memberNo") Integer memberNo, @Param("startTime") String startTime, @Param("endTime") String endTime);

    // 财务流水
    int insertLedger(FinanceLedger ledger);
    List<FinanceLedger> getLedgerList(Map<String, Object> params);
    long getLedgerCount(Map<String, Object> params);
    List<FinanceLedger> getLedgerListForExport(Map<String, Object> params);

    // 报表统计
    List<Map<String, Object>> getDailyReport(@Param("start") String start, @Param("end") String end);
    List<Map<String, Object>> getMonthlyReport(@Param("year") String year);

    // 现金收款流水（仅充值+现金续卡，过滤内部调账）
    List<FinanceLedger> getCashFlowList(Map<String, Object> params);
    long getCashFlowCount(Map<String, Object> params);
    BigDecimal getCashFlowTotalAmount(Map<String, Object> params);

    // 现金收款汇总统计（日报）
    List<Map<String, Object>> getDailyCashSummary(@Param("start") String start, @Param("end") String end);

    // 现金收款汇总统计（月报）
    List<Map<String, Object>> getMonthlyCashSummary(@Param("year") String year);
}
