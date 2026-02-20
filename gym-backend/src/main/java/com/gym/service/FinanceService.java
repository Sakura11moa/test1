package com.gym.service;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.gym.entity.FinanceLedger;
import com.gym.entity.MemberCardType;
import com.gym.mapper.FinanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务流水与报表服务
 */
@Slf4j
@Service
public class FinanceService {

    @Resource
    private FinanceMapper financeMapper;

    /**
     * 写入流水（内部通用）
     */
    public void addLedger(String bizType, String bizNo, Integer memberNo, String direction, 
                          BigDecimal amount, String channel, BigDecimal balanceAfter, 
                          Integer operatorAdminId, String remark) {
        FinanceLedger ledger = new FinanceLedger();
        ledger.setMemberNo(memberNo);
        ledger.setBizType(bizType);
        ledger.setBizNo(bizNo);
        ledger.setDirection(direction);
        ledger.setAmount(amount);
        ledger.setChannel(channel);
        ledger.setBalanceAfter(balanceAfter);
        ledger.setOperatorAdminId(operatorAdminId);
        ledger.setRemark(remark);
        
        try {
            financeMapper.insertLedger(ledger);
        } catch (DuplicateKeyException e) {
            // 命中唯一键 uk_biz 则说明已处理过，不重复入账
        }
    }

    /**
     * 分页查询流水
     */
    public Map<String, Object> getLedgerList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        int page, size;
        try {
            page = Integer.parseInt(String.valueOf(params.get("page")));
            size = Integer.parseInt(String.valueOf(params.get("size")));
        } catch (NumberFormatException e) {
            page = 1;
            size = 15;
        }
        // 确保 page 至少为 1（前端可能从 0 开始）
        if (page < 1) page = 1;
        // 确保 size 是有效的正整数
        if (size < 1) size = 15;
        params.put("page", (page - 1) * size);
        params.put("size", size);
        
        List<FinanceLedger> list = financeMapper.getLedgerList(params);
        long total = financeMapper.getLedgerCount(params);
        
        resultMap.put("code", 200);
        resultMap.put("data", list);
        resultMap.put("total", total);
        return resultMap;
    }

    /**
     * 获取卡项列表
     */
    public List<MemberCardType> getAllCardTypes() {
        return financeMapper.getAllCardTypes();
    }

    /**
     * 财务报表统计
     * 修复：添加异常处理和兜底方案
     */
    public Map<String, Object> getFinanceReport(String type, String start, String end, String year) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<Map<String, Object>> data;
            if ("monthly".equalsIgnoreCase(type)) {
                data = financeMapper.getMonthlyReport(year);
            } else {
                data = financeMapper.getDailyReport(start, end);
            }
            // 修复：数据为空时返回空列表而非null
            if (data == null) {
                data = new java.util.ArrayList<>();
            }
            resultMap.put("code", 200);
            resultMap.put("data", data);
            return resultMap;
        } catch (Exception e) {
            // 修复：异常处理和兜底方案
            resultMap.put("code", 500);
            resultMap.put("message", "财务报表生成失败：" + e.getMessage());
            resultMap.put("data", new java.util.ArrayList<>());
            return resultMap;
        }
    }

    /**
     * 导出财务流水 Excel
     * @param params 筛选条件（start, end, bizType, memberNo, channel, direction）
     * @param response HTTP响应
     */
    public void exportLedger(Map<String, Object> params, HttpServletResponse response) throws Exception {
        // 1. 查询全部数据（不分页）
        List<FinanceLedger> list = financeMapper.getLedgerListForExport(params);

        // 2. 在内存中创建Excel写出
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 3. 写出表头和数据
        writer.write(list, true);

        // 4. 设置浏览器响应头
        String fileName = "财务流水_" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

        // 5. 写出到响应流
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 现金收款流水（仅充值+现金续卡，过滤内部调账）
     */
    public Map<String, Object> getCashFlowList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        int page, size;
        try {
            page = Integer.parseInt(String.valueOf(params.get("page")));
            size = Integer.parseInt(String.valueOf(params.get("size")));
        } catch (NumberFormatException e) {
            page = 1;
            size = 15;
        }
        if (page < 1) page = 1;
        if (size < 1) size = 15;
        params.put("page", (page - 1) * size);
        params.put("size", size);

        // 处理渠道筛选（支持多选）
        if (params.containsKey("channel") && params.get("channel") != null) {
            String channel = params.get("channel").toString();
            if (!channel.isEmpty()) {
                // 将单个渠道转为列表
                params.put("channels", Arrays.asList(channel.split(",")));
            }
        }

        // 处理结束日期：追加时间以包含整天
        if (params.containsKey("end") && params.get("end") != null) {
            String end = params.get("end").toString();
            if (!end.isEmpty() && !end.contains(" ")) {
                params.put("end", end + " 23:59:59");
            }
        }

        List<FinanceLedger> list = financeMapper.getCashFlowList(params);
        long total = financeMapper.getCashFlowCount(params);
        BigDecimal totalIncome = financeMapper.getCashFlowTotalAmount(params);

        resultMap.put("code", 200);
        resultMap.put("data", list);
        resultMap.put("total", total);
        resultMap.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        return resultMap;
    }

    /**
     * 现金收款汇总统计（日报/月报）
     */
    public Map<String, Object> getCashSummary(String start, String end, String year) {
        Map<String, Object> resultMap = new HashMap<>();

        // 处理结束日期：追加时间以包含整天
        if (end != null && !end.isEmpty() && !end.contains(" ")) {
            end = end + " 23:59:59";
        }

        List<Map<String, Object>> data;

        if (year != null && !year.isEmpty()) {
            // 月报模式
            data = financeMapper.getMonthlyCashSummary(year);
        } else {
            // 日报模式
            data = financeMapper.getDailyCashSummary(start, end);
        }

        // 计算总计
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal rechargeIncome = BigDecimal.ZERO;
        BigDecimal renewIncome = BigDecimal.ZERO;
        BigDecimal cashIncome = BigDecimal.ZERO;

        if (data != null) {
            for (Map<String, Object> item : data) {
                Object total = item.get("totalAmount");
                Object recharge = item.get("rechargeAmount");
                Object renew = item.get("renewAmount");
                Object cash = item.get("cashAmount");

                if (total != null) totalIncome = totalIncome.add(new BigDecimal(total.toString()));
                if (recharge != null) rechargeIncome = rechargeIncome.add(new BigDecimal(recharge.toString()));
                if (renew != null) renewIncome = renewIncome.add(new BigDecimal(renew.toString()));
                if (cash != null) cashIncome = cashIncome.add(new BigDecimal(cash.toString()));
            }
        }

        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("totalIncome", totalIncome);
        totalMap.put("rechargeIncome", rechargeIncome);
        totalMap.put("renewIncome", renewIncome);
        totalMap.put("cashIncome", cashIncome);

        resultMap.put("code", 200);
        resultMap.put("data", data);
        resultMap.put("total", totalMap);
        return resultMap;
    }

    /**
     * 获取指定月份的充值总额
     */
    public BigDecimal getMonthRechargeTotal(String yearMonth) {
        try {
            List<Map<String, Object>> data = financeMapper.getMonthlyCashSummary(yearMonth.substring(0, 4));
            if (data != null) {
                for (Map<String, Object> item : data) {
                    if (yearMonth.equals(item.get("period"))) {
                        Object recharge = item.get("rechargeAmount");
                        return recharge != null ? new BigDecimal(recharge.toString()) : BigDecimal.ZERO;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取月充值总额失败", e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取指定月份的续卡总额
     */
    public BigDecimal getMonthCardRenewTotal(String yearMonth) {
        try {
            List<Map<String, Object>> data = financeMapper.getMonthlyCashSummary(yearMonth.substring(0, 4));
            if (data != null) {
                for (Map<String, Object> item : data) {
                    if (yearMonth.equals(item.get("period"))) {
                        Object renew = item.get("renewAmount");
                        return renew != null ? new BigDecimal(renew.toString()) : BigDecimal.ZERO;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取月续卡总额失败", e);
        }
        return BigDecimal.ZERO;
    }
}
