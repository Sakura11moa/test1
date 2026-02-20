package com.gym.task;

import com.gym.service.AccrualFinanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 权责发生制定时任务
 *
 * 功能：
 * 1. 每月1日自动分摊会员卡收入
 * 2. 其他定时任务...
 */
@Component
public class AccrualFinanceTask {

    private static final Logger logger = LoggerFactory.getLogger(AccrualFinanceTask.class);

    @Resource
    private AccrualFinanceService accrualFinanceService;

    /**
     * 每月1日凌晨2点执行，自动分摊上个月的会员卡收入
     * cron: 秒 分 时 日 月 周
     * "0 0 2 1 * ?" = 每月1日 02:00:00
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void amortizeMonthlyCardRevenue() {
        logger.info("========== 开始执行会员卡月度分摊任务 ==========");

        try {
            // 计算上个月的年月
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String yearMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            logger.info("分摊期间: {}", yearMonth);

            // 执行分摊
            Map<String, Object> result = accrualFinanceService.amortizeCardRevenue(yearMonth);

            if ("200".equals(String.valueOf(result.get("code")))) {
                logger.info("分摊完成: 共处理{}笔，总金额{}",
                        result.get("count"),
                        result.get("totalAmount"));
            } else {
                logger.warn("分摊未完成: {}", result.get("message"));
            }

        } catch (Exception e) {
            logger.error("会员卡月度分摊任务执行失败", e);
        }

        logger.info("========== 会员卡月度分摊任务执行完毕 ==========");
    }

    /**
     * 每月1日凌晨3点执行，补充分摊当前月（处理1号凌晨的情况）
     * 确保每月的第一天也能分摊
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void amortizeCurrentMonthCardRevenue() {
        logger.info("========== 开始执行会员卡当月分摊任务 ==========");

        try {
            // 计算当前月的年月
            LocalDate currentMonth = LocalDate.now();
            String yearMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            logger.info("分摊期间(当月): {}", yearMonth);

            // 执行分摊
            Map<String, Object> result = accrualFinanceService.amortizeCardRevenue(yearMonth);

            if ("200".equals(String.valueOf(result.get("code")))) {
                logger.info("当月分摊完成: 共处理{}笔，总金额{}",
                        result.get("count"),
                        result.get("totalAmount"));
            } else {
                logger.info("当月无需分摊或分摊完成: {}", result.get("message"));
            }

        } catch (Exception e) {
            logger.error("会员卡当月分摊任务执行失败", e);
        }

        logger.info("========== 会员卡当月分摊任务执行完毕 ==========");
    }

    /**
     * 测试用：手动触发指定月份的分摊
     * 调用方式：直接调用 accrualFinanceService.amortizeCardRevenue("2026-02")
     */
    public void testAmortize(String yearMonth) {
        logger.info("========== 测试分摊任务 ==========");
        logger.info("分摊期间: {}", yearMonth);

        Map<String, Object> result = accrualFinanceService.amortizeCardRevenue(yearMonth);
        logger.info("分摊结果: {}", result);

        logger.info("========== 测试分摊完成 ==========");
    }
}
