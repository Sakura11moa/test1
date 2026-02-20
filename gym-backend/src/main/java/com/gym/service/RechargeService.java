package com.gym.service;


import com.gym.entity.Recharge;
import com.gym.mapper.MemberMapper;
import com.gym.mapper.RechargeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 充值 服务类
 *

 */
@Slf4j
@Service
public class RechargeService {

    @Resource
    private RechargeMapper rechargeMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private FinanceService financeService;

    @Resource
    private AccrualFinanceService accrualFinanceService;

    public List<Recharge> getRechargeByMemberNo(int memberNo){
        return rechargeMapper.getRechargeByMemberNo(memberNo);
    }

    /**
     * 修复：充值接口无幂等性问题，原逻辑无重复请求拦截，现新增请求号幂等控制
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> addRechargeByMemberNo(Recharge recharge, String requestNo) {
        Map<String,Object> resultMap = new HashMap<>();

        // 1. 前置校验：请求号已存在则直接返回成功
        if (requestNo != null && !requestNo.isEmpty()) {
            Recharge existRecord = rechargeMapper.selectByRequestNo(requestNo);
            if (existRecord != null) {
                log.info("重复充值请求，请求号：{}，已存在记录：{}", requestNo, existRecord.getRechargeNo());
                resultMap.put("code", 200);
                resultMap.put("message", "充值已成功");
                resultMap.put("rechargeNo", existRecord.getRechargeNo());
                return resultMap;
            }
        }

        if (recharge == null || recharge.getMemberNo() == null || recharge.getRechargeMoney() == null) {
            resultMap.put("code",400);
            resultMap.put("message","充值失败，参数不能为空");
            return resultMap;
        }

        //充值时间
        recharge.setRechargeDate(LocalDate.now());
        recharge.setRechargeMethod("在线充值");
        recharge.setRechargeStatus(1);
        // 修复：设置请求号
        recharge.setRequestNo(requestNo);

        try {
            int result = rechargeMapper.addRechargeByMemberNo(recharge);

            if(result>0){
                double money = recharge.getRechargeMoney();
                int memberNo = recharge.getMemberNo();

                memberMapper.increaseMemberChange(memberNo, money);

                Double after = memberMapper.getMemberChange(memberNo);
                if (after == null) after = 0.0;

                // 这里保证 bizNo 一定是数字字符串（防止回填失败导致bizNo异常）
                String bizNo = (recharge.getRechargeNo() == null) ? "0" : String.valueOf(recharge.getRechargeNo());

                // 【权责发生制】充值记为预收账款，不直接算收入
                // 记录充值预收账款
                accrualFinanceService.recordRechargeDeferred(
                        memberNo,
                        bizNo,
                        BigDecimal.valueOf(money),
                        "ONLINE"
                );

                // 【收付实现制】记录充值财务流水
                financeService.addLedger(
                        "RECHARGE",  // bizType
                        bizNo,       // bizNo
                        memberNo,    // memberNo
                        "IN",        // direction
                        BigDecimal.valueOf(money),  // amount
                        "ONLINE",    // channel
                        BigDecimal.valueOf(after),   // balanceAfter
                        null,        // operatorAdminId
                        "会员在线充值"  // remark
                );

                resultMap.put("code",200);
                resultMap.put("message","充值成功");
                resultMap.put("balanceAfter", after);
                resultMap.put("rechargeNo", recharge.getRechargeNo());
            }else{
                resultMap.put("code",400);
                resultMap.put("message","充值失败");
            }
        } catch (DuplicateKeyException e) {
            // 2. 唯一索引冲突兜底，重复请求直接返回成功
            log.warn("重复充值请求，请求号：{}", requestNo);
            Recharge existRecord = rechargeMapper.selectByRequestNo(requestNo);
            if (existRecord != null) {
                resultMap.put("code", 200);
                resultMap.put("message", "充值已成功");
                resultMap.put("rechargeNo", existRecord.getRechargeNo());
            } else {
                throw e;
            }
        }
        return resultMap;
    }

}
