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
 *
 * 修复：添加重试机制和告警机制
 */
@Component
public class AccrualFinanceTask {

    private static final Logger logger = LoggerFactory.getLogger(AccrualFinanceTask.class);

    // 修复：重试机制配置
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @Resource
    private AccrualFinanceService accrualFinanceService;

    /**
     * 每月1日凌晨2点执行，自动分摊上个月的会员卡收入
     * cron: 秒 分 时 日 月 周
     * "0 0 2 1 * ?" = 每月1日 02:00:00
     *
     * 修复：添加重试机制（maxAttempts=3，delay=5000ms）
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void amortizeMonthlyCardRevenue() {
        logger.info("========== 开始执行会员卡月度分摊任务 ==========");

        // 修复：添加重试机制
        int attempt = 0;
        boolean success = false;
        Exception lastException = null;

        while (attempt < MAX_RETRY_ATTEMPTS && !success) {
            attempt++;
            try {
                // 计算上个月的年月
                LocalDate lastMonth = LocalDate.now().minusMonths(1);
                String yearMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                logger.info("分摊期间: {}, 第{}次尝试", yearMonth, attempt);

                // 执行分摊
                Map<String, Object> result = accrualFinanceService.amortizeCardRevenue(yearMonth);

                if ("200".equals(String.valueOf(result.get("code")))) {
                    logger.info("分摊完成: 共处理{}笔，总金额{}",
                            result.get("count"),
                            result.get("totalAmount"));
                    success = true;
                } else {
                    logger.warn("分摊未完成: {}", result.get("message"));
                    // 如果是业务逻辑错误，不再重试
                    if (!"200".equals(String.valueOf(result.get("code")))) {
                        success = true;
                    }
                }

            } catch (Exception e) {
                lastException = e;
                logger.error("第{}次执行会员卡月度分摊任务失败: {}", attempt, e.getMessage());

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    logger.info("等待{}ms后进行第{}次重试...", RETRY_DELAY_MS, attempt + 1);
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
            String alertMessage = "【告警】会员卡月度分摊任务执行失败，已重试" + MAX_RETRY_ATTEMPTS + "次仍未成功！";
            logger.error(alertMessage);
            System.err.println(alertMessage);
            if (lastException != null) {
                System.err.println("最后异常信息: " + lastException.getMessage());
                lastException.printStackTrace(System.err);
            }
        }

        logger.info("========== 会员卡月度分摊任务执行完毕 ==========");
    }

    /**
     * 每月1日凌晨3点执行，补充分摊当前月（处理1号凌晨的情况）
     * 确保每月的第一天也能分摊
     *
     * 修复：添加重试机制和告警机制
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void amortizeCurrentMonthCardRevenue() {
        logger.info("========== 开始执行会员卡当月分摊任务 ==========");

        // 修复：添加重试机制
        int attempt = 0;
        boolean success = false;
        Exception lastException = null;

        while (attempt < MAX_RETRY_ATTEMPTS && !success) {
            attempt++;
            try {
                // 计算当前月的年月
                LocalDate currentMonth = LocalDate.now();
                String yearMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                logger.info("分摊期间(当月): {}, 第{}次尝试", yearMonth, attempt);

                // 执行分摊
                Map<String, Object> result = accrualFinanceService.amortizeCardRevenue(yearMonth);

                if ("200".equals(String.valueOf(result.get("code")))) {
                    logger.info("当月分摊完成: 共处理{}笔，总金额{}",
                            result.get("count"),
                            result.get("totalAmount"));
                    success = true;
                } else {
                    logger.info("当月无需分摊或分摊完成: {}", result.get("message"));
                    success = true; // 业务逻辑认为不需要处理，也视为成功
                }

            } catch (Exception e) {
                lastException = e;
                logger.error("第{}次执行会员卡当月分摊任务失败: {}", attempt, e.getMessage());

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    logger.info("等待{}ms后进行第{}次重试...", RETRY_DELAY_MS, attempt + 1);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // 修复：告警机制
        if (!success) {
            String alertMessage = "【告警】会员卡当月分摊任务执行失败，已重试" + MAX_RETRY_ATTEMPTS + "次仍未成功！";
            logger.error(alertMessage);
            System.err.println(alertMessage);
            if (lastException != null) {
                System.err.println("最后异常信息: " + lastException.getMessage());
                lastException.printStackTrace(System.err);
            }
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
