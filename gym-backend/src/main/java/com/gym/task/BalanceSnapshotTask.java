package com.gym.task;

import com.gym.entity.BalanceSnapshot;
import com.gym.mapper.BalanceSnapshotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 财务余额快照定时任务
 * 每月1日凌晨执行，记录上月末的各类负债余额
 */
@Slf4j
@Component
public class BalanceSnapshotTask {

    @Resource
    private BalanceSnapshotMapper balanceSnapshotMapper;

    /**
     * 每月1日凌晨1点执行快照任务
     * 记录上月末（本月0日）的余额数据
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void createMonthlySnapshot() {
        log.info("开始执行月度财务余额快照任务...");
        
        // 快照日期为上月末
        LocalDate snapshotDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).minusDays(1);
        
        // 检查是否已存在该日期的快照
        BalanceSnapshot existing = balanceSnapshotMapper.getBySnapshotDate(snapshotDate);
        if (existing != null) {
            log.info("快照日期 {} 的记录已存在，跳过执行", snapshotDate);
            return;
        }
        
        // 获取当前各类余额
        BigDecimal balanceAmount = balanceSnapshotMapper.getTotalBalanceAmount();
        BigDecimal cardDeferredAmount = balanceSnapshotMapper.getTotalCardDeferredAmount();
        BigDecimal courseDeferredAmount = balanceSnapshotMapper.getTotalCourseDeferredAmount();
        
        // 创建快照记录
        BalanceSnapshot snapshot = new BalanceSnapshot();
        snapshot.setSnapshotDate(snapshotDate);
        snapshot.setBalanceAmount(balanceAmount != null ? balanceAmount : BigDecimal.ZERO);
        snapshot.setCardDeferredAmount(cardDeferredAmount != null ? cardDeferredAmount : BigDecimal.ZERO);
        snapshot.setCourseDeferredAmount(courseDeferredAmount != null ? courseDeferredAmount : BigDecimal.ZERO);
        
        balanceSnapshotMapper.insert(snapshot);
        
        log.info("月度财务余额快照任务执行完成，快照日期: {}, 储值余额: {}, 会籍预收: {}, 课程预收: {}", 
                snapshotDate, balanceAmount, cardDeferredAmount, courseDeferredAmount);
    }

    /**
     * 手动执行快照（用于测试或补录数据）
     * @param snapshotDate 快照日期
     */
    public void createSnapshot(LocalDate snapshotDate) {
        log.info("手动执行财务余额快照任务，快照日期: {}", snapshotDate);
        
        BigDecimal balanceAmount = balanceSnapshotMapper.getTotalBalanceAmount();
        BigDecimal cardDeferredAmount = balanceSnapshotMapper.getTotalCardDeferredAmount();
        BigDecimal courseDeferredAmount = balanceSnapshotMapper.getTotalCourseDeferredAmount();
        
        BalanceSnapshot snapshot = new BalanceSnapshot();
        snapshot.setSnapshotDate(snapshotDate);
        snapshot.setBalanceAmount(balanceAmount != null ? balanceAmount : BigDecimal.ZERO);
        snapshot.setCardDeferredAmount(cardDeferredAmount != null ? cardDeferredAmount : BigDecimal.ZERO);
        snapshot.setCourseDeferredAmount(courseDeferredAmount != null ? courseDeferredAmount : BigDecimal.ZERO);
        
        balanceSnapshotMapper.insert(snapshot);
        
        log.info("手动快照执行完成，快照日期: {}, 储值余额: {}, 会籍预收: {}, 课程预收: {}", 
                snapshotDate, balanceAmount, cardDeferredAmount, courseDeferredAmount);
    }
}
