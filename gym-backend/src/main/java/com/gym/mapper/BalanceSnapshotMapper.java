package com.gym.mapper;

import com.gym.entity.BalanceSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
@Repository
public interface BalanceSnapshotMapper {

    /**
     * 插入快照记录
     */
    int insert(BalanceSnapshot snapshot);

    /**
     * 根据日期查询快照
     */
    BalanceSnapshot getBySnapshotDate(@Param("snapshotDate") LocalDate snapshotDate);

    /**
     * 查询最近的快照
     */
    BalanceSnapshot getLatestSnapshot();

    /**
     * 查询所有快照
     */
    List<BalanceSnapshot> getAll();

    /**
     * 获取储值余额（所有会员的储值余额总和）
     */
    BigDecimal getTotalBalanceAmount();

    /**
     * 获取会籍预收金额
     */
    BigDecimal getTotalCardDeferredAmount();

    /**
     * 获取课程预收金额
     */
    BigDecimal getTotalCourseDeferredAmount();
}
