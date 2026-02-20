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
 *
 * 修复：添加重试机制和告警机制
 */
@Slf4j
@Component
public class BalanceSnapshotTask {

    // 修复：重试机制配置
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @Resource
    private BalanceSnapshotMapper balanceSnapshotMapper;

    /**
     * 每月1日凌晨1点执行快照任务
     * 记录上月末（本月0日）的余额数据
     *
     * 修复：添加重试机制和告警机制
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void createMonthlySnapshot() {
        log.info("开始执行月度财务余额快照任务...");

        // 修复：添加重试机制
        int attempt = 0;
        boolean success = false;
        Exception lastException = null;

        while (attempt < MAX_RETRY_ATTEMPTS && !success) {
            attempt++;
            try {
                log.info("第{}次尝试执行快照任务", attempt);

                // 快照日期为上月末
                LocalDate snapshotDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).minusDays(1);

                // 检查是否已存在该日期的快照
                BalanceSnapshot existing = balanceSnapshotMapper.getBySnapshotDate(snapshotDate);
                if (existing != null) {
                    log.info("快照日期 {} 的记录已存在，跳过执行", snapshotDate);
                    success = true;
                    continue;
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
                success = true;

            } catch (Exception e) {
                lastException = e;
                log.error("第{}次执行月度财务余额快照任务失败: {}", attempt, e.getMessage());

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.info("等待{}ms后进行第{}次重试...", RETRY_DELAY_MS, attempt + 1);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // 修复：告警机制（打印日志 + 控制台输出）
        if (!success) {
            String alertMessage = "【告警】月度财务余额快照任务执行失败，已重试" + MAX_RETRY_ATTEMPTS + "次仍未成功！";
            log.error(alertMessage);
            System.err.println(alertMessage);
            if (lastException != null) {
                System.err.println("最后异常信息: " + lastException.getMessage());
                lastException.printStackTrace(System.err);
            }
        }
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
