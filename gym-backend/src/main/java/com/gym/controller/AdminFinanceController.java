package com.gym.controller;

import com.gym.entity.DeferredRevenue;
import com.gym.entity.RevenueRecognition;
import com.gym.service.AccrualFinanceService;
import com.gym.service.CardService;
import com.gym.service.FinanceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务与会员卡管理 控制层 (管理员权限)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminFinanceController {

    @Resource
    private CardService cardService;

    @Resource
    private FinanceService financeService;

    @Resource
    private AccrualFinanceService accrualFinanceService;

    /**
     * 管理员代办续卡
     * @param params {memberNo, cardTypeId, payChannel, remark, operatorAdminId}
     */
    @PostMapping("/renewMemberCard")
    public Map<String, Object> renewMemberCard(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Integer memberNo = (Integer) params.get("memberNo");
            Integer cardTypeId = (Integer) params.get("cardTypeId");
            String payChannel = (String) params.get("payChannel");
            String remark = (String) params.get("remark");
            Integer operatorAdminId = (Integer) params.get("operatorAdminId");

            return cardService.renewMemberCard(memberNo, cardTypeId, payChannel, operatorAdminId, remark);
        } catch (IllegalArgumentException e) {
            resultMap.put("code", 400);
            resultMap.put("message", e.getMessage());
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("message", "续卡失败：" + e.getMessage());
            return resultMap;
        }
    }

    /**
     * 获取所有可用的卡项模板
     */
    @GetMapping("/cardTypes")
    public Map<String, Object> getCardTypes() {
        Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("data", financeService.getAllCardTypes());
        return resultMap;
    }

    /**
     * 查询财务流水总账
     */
    @GetMapping("/ledger/list")
    public Map<String, Object> getLedgerList(@RequestParam Map<String, Object> params) {
        // 默认分页（确保是数字）
        if (!params.containsKey("page") || params.get("page") == null || params.get("page").toString().isEmpty()) {
            params.put("page", 1);
        }
        if (!params.containsKey("size") || params.get("size") == null || params.get("size").toString().isEmpty()) {
            params.put("size", 15);
        }
        return financeService.getLedgerList(params);
    }

    /**
     * 现金收款流水（仅显示充值+现金续卡，过滤内部调账）
     * GET /api/admin/cash/flow
     * Query Params: start, end, page, size
     */
    @GetMapping("/cash/flow")
    public Map<String, Object> getCashFlowList(@RequestParam Map<String, Object> params) {
        // 默认分页
        if (!params.containsKey("page") || params.get("page") == null || params.get("page").toString().isEmpty()) {
            params.put("page", 1);
        }
        if (!params.containsKey("size") || params.get("size") == null || params.get("size").toString().isEmpty()) {
            params.put("size", 15);
        }
        // 固定过滤条件：只显示充值+现金续卡，排除内部调账
        // bizType: RECHARGE 或 CARD_RENEW
        // channel: CASH (续卡时)
        // direction: IN (收入方向，排除NONE内部调账)
        return financeService.getCashFlowList(params);
    }

    /**
     * 现金收款汇总统计
     * GET /api/admin/cash/summary
     * Query Params: start, end (日报模式) 或 year (月报模式)
     */
    @GetMapping("/cash/summary")
    public Map<String, Object> getCashSummary(@RequestParam(required = false) String start,
                                               @RequestParam(required = false) String end,
                                               @RequestParam(required = false) String year) {
        return financeService.getCashSummary(start, end, year);
    }

    /**
     * 财务报表: 日报/月报
     * @param type daily/monthly
     */
    @GetMapping("/finance/report")
    public Map<String, Object> getFinanceReport(@RequestParam String type,
                                               @RequestParam(required = false) String start,
                                               @RequestParam(required = false) String end,
                                               @RequestParam(required = false) String year) {
        return financeService.getFinanceReport(type, start, end, year);
    }

    /**
     * 导出财务流水 Excel
     * GET /api/admin/ledger/export
     * Query Params: start, end, bizType, memberNo, channel, direction
     */
    @GetMapping("/ledger/export")
    public void exportLedger(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        financeService.exportLedger(params, response);
    }

    // ==================== 权责发生制财务报表 ====================

    /**
     * 查询会员递延收益概览
     * GET /api/admin/accrual/member/overview?memberNo=6
     */
    @GetMapping("/accrual/member/overview")
    public Map<String, Object> getMemberDeferredOverview(@RequestParam(required = false) Integer memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap.put("code", 200);
            resultMap.put("data", accrualFinanceService.getMemberDeferredOverview(memberNo));
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("message", "获取概览失败：" + e.getMessage());
            return resultMap;
        }
    }

    /**
     * 查询会员递延收益详情
     * GET /api/admin/accrual/member/deferred?memberNo=6
     */
    @GetMapping("/accrual/member/deferred")
    public Map<String, Object> getMemberDeferredDetails(@RequestParam(required = false) Integer memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<DeferredRevenue> list = accrualFinanceService.getMemberDeferredDetails(memberNo);
            resultMap.put("code", 200);
            resultMap.put("data", list);
            resultMap.put("total", list.size());
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("message", "获取预收账款明细失败：" + e.getMessage());
            return resultMap;
        }
    }

    /**
     * 查询会员收入确认记录
     * GET /api/admin/accrual/member/recognized?memberNo=6
     */
    @GetMapping("/accrual/member/recognized")
    public Map<String, Object> getMemberRevenueRecognitions(@RequestParam(required = false) Integer memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<RevenueRecognition> list = accrualFinanceService.getMemberRevenueRecognitions(memberNo);
            resultMap.put("code", 200);
            resultMap.put("data", list);
            resultMap.put("total", list.size());
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("message", "获取收入确认记录失败：" + e.getMessage());
            return resultMap;
        }
    }

    /**
     * 手动触发会员卡分摊（供测试/补录用）
     * POST /api/admin/accrual/amortize
     * Body: {"yearMonth": "2026-02"}
     */
    @PostMapping("/accrual/amortize")
    public Map<String, Object> amortizeCardRevenue(@RequestBody Map<String, Object> params) {
        String yearMonth = (String) params.get("yearMonth");
        if (yearMonth == null || yearMonth.isEmpty()) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", 400);
            resultMap.put("message", "yearMonth参数必填，格式：yyyy-MM");
            return resultMap;
        }
        return accrualFinanceService.amortizeCardRevenue(yearMonth);
    }

    /**
     * 权责发生制财务报表：预收账款 vs 已实现收入
     * GET /api/admin/accrual/report/overview?yearMonth=2026-02
     */
    @GetMapping("/accrual/report/overview")
    public Map<String, Object> getAccrualReportOverview(@RequestParam(required = false) String yearMonth) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap.put("code", 200);
            resultMap.put("data", accrualFinanceService.getAccrualBusinessReport(yearMonth));
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("message", "获取权责报表失败：" + e.getMessage());
            return resultMap;
        }
    }

}
