package com.gym.service;

import com.gym.entity.Member;
import com.gym.entity.MemberCardRenewal;
import com.gym.entity.MemberCardType;
import com.gym.entity.Recharge;
import com.gym.mapper.FinanceMapper;
import com.gym.mapper.MemberMapper;
import com.gym.mapper.RechargeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardService {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private FinanceMapper financeMapper;

    @Resource
    private FinanceService financeService;

    @Resource
    private AccrualFinanceService accrualFinanceService;

    @Resource
    private RechargeMapper rechargeMapper;

    @Transactional
    public Map<String, Object> renewMemberCard(Integer memberNo, Integer cardTypeId, String payChannel, Integer operatorAdminId, String remark) {
        Map<String, Object> resultMap = new HashMap<>();

        // 参数验证
        if (memberNo == null || cardTypeId == null || payChannel == null || payChannel.trim().isEmpty()) {
            throw new IllegalArgumentException("参数不能为空");
        }

        payChannel = payChannel.trim().toUpperCase();
        if (!"BALANCE".equals(payChannel) && !"CASH".equals(payChannel)) {
            throw new IllegalArgumentException("payChannel仅支持BALANCE/CASH");
        }

        Member member = memberMapper.getMemberByMemberNo(memberNo);
        if (member == null) {
            throw new IllegalArgumentException("会员不存在");
        }

        MemberCardType cardType = financeMapper.getCardTypeById(cardTypeId);
        if (cardType == null || cardType.getStatus() == null || cardType.getStatus() != 1) {
            throw new IllegalArgumentException("卡项不存在或已停用");
        }

        int days = cardType.getDays();
        BigDecimal amount = cardType.getPrice();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldExpire = member.getExpireTime();
        LocalDateTime base = (oldExpire == null || oldExpire.isBefore(now)) ? now : oldExpire;
        LocalDateTime newExpire = base.plusDays(days);
        
        // 计算生效日期：用于按生效日期统计本月新增负债
        // 如果原卡已过期，生效日期为今天；否则为原卡到期日的下一天
        LocalDate effectiveDate;
        if (oldExpire == null || oldExpire.isBefore(now)) {
            effectiveDate = LocalDate.now();
        } else {
            effectiveDate = oldExpire.toLocalDate().plusDays(1);
        }

        // 计算新的卡状态：如果新的到期时间在当前时间之后，则状态为有效(1)，否则为过期(0)
        int newCardStatus;
        if (newExpire.isAfter(now)) {
            newCardStatus = 1; // 有效
        } else {
            newCardStatus = 0; // 已过期
        }

        System.out.println("========== 续卡操作开始 ==========");
        System.out.println("会员编号: " + memberNo);
        System.out.println("卡项ID: " + cardTypeId);
        System.out.println("增加天数: " + days);
        System.out.println("原到期时间: " + oldExpire);
        System.out.println("新到期时间: " + newExpire);
        System.out.println("新卡状态: " + newCardStatus);
        System.out.println("支付方式: " + payChannel);
        System.out.println("==================================");

        BigDecimal balanceAfter = null;

        if ("BALANCE".equals(payChannel)) {
            Double current = memberMapper.getMemberChange(memberNo);
            if (current == null) current = 0.0;
            BigDecimal balance = BigDecimal.valueOf(current);
            if (balance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("余额不足");
            }

            int deducted = memberMapper.decreaseMemberChange(memberNo, amount.doubleValue());
            if (deducted <= 0) {
                throw new RuntimeException("扣减余额失败");
            }

            Double after = memberMapper.getMemberChange(memberNo);
            if (after == null) after = 0.0;
            balanceAfter = BigDecimal.valueOf(after);
        }

        // 写续卡记录
        MemberCardRenewal renewal = new MemberCardRenewal();
        renewal.setMemberNo(memberNo);
        renewal.setCardTypeId(cardTypeId);
        renewal.setDaysAdded(days);
        renewal.setAmount(amount);
        renewal.setPayChannel(payChannel);
        renewal.setOldExpireTime(oldExpire);
        renewal.setNewExpireTime(newExpire);
        renewal.setOperatorAdminId(operatorAdminId);
        renewal.setRemark(remark);

        int insRenewal = financeMapper.insertRenewal(renewal);
        System.out.println("续卡记录插入结果: " + insRenewal + ", renewalNo: " + renewal.getRenewalNo());
        if (insRenewal <= 0 || renewal.getRenewalNo() == null) {
            throw new RuntimeException("续卡记录写入失败");
        }

        // 更新会员卡信息
        System.out.println("开始更新会员卡信息...");
        int upd = memberMapper.updateMemberCardInfo(memberNo, newExpire, newCardStatus);
        System.out.println("会员卡信息更新结果: " + upd);
        if (upd <= 0) {
            throw new RuntimeException("更新会员卡信息失败");
        }
        System.out.println("会员卡信息更新成功!");

        // 【权责发生制】记录预收账款
        // 余额续卡：内部划转；现金续卡：预收账款
        String renewalNoStr = String.valueOf(renewal.getRenewalNo());
        // LocalDateTime 转 LocalDate
        LocalDate newExpireDate = newExpire.toLocalDate();
        if ("BALANCE".equals(payChannel)) {
            // 余额续卡：记录预收账款内部划转
            // 查找会员最近的充值记录作为来源
            String fromRechargeNo = getLatestRechargeNo(memberNo);
            accrualFinanceService.recordBalanceRenewalTransfer(
                    memberNo,
                    renewalNoStr,
                    amount,
                    days,
                    fromRechargeNo,
                    newExpireDate,
                    effectiveDate  // 作为 startDate（生效日期）
            );
        } else {
            // 现金续卡：记录预收账款（会籍费）
            accrualFinanceService.recordCashRenewalDeferred(
                    memberNo,
                    renewalNoStr,
                    amount,
                    days,
                    newExpireDate,
                    operatorAdminId,
                    effectiveDate  // 作为 startDate（生效日期）
            );

            // 【收付实现制】记录现金续卡财务流水
            financeService.addLedger(
                    "CARD_RENEW",    // bizType
                    renewalNoStr,    // bizNo
                    memberNo,        // memberNo
                    "IN",            // direction
                    amount,         // amount
                    "CASH",          // channel
                    balanceAfter,    // balanceAfter
                    operatorAdminId, // operatorAdminId
                    "现金续卡"        // remark
            );
        }

        resultMap.put("code", 200);
        resultMap.put("message", "续卡成功");
        resultMap.put("renewalNo", renewal.getRenewalNo());
        resultMap.put("oldExpireTime", oldExpire);
        resultMap.put("newExpireTime", newExpire);
        resultMap.put("amount", amount);
        resultMap.put("payChannel", payChannel);
        if (balanceAfter != null) {
            resultMap.put("balanceAfter", balanceAfter);
        }

        return resultMap;
    }

    /**
     * 获取会员最近一次充值单号
     * @param memberNo 会员编号
     * @return 充值单号，如果没有充值记录则返回续卡号作为标识
     */
    private String getLatestRechargeNo(Integer memberNo) {
        List<Recharge> rechargeList = rechargeMapper.getRechargeByMemberNo(memberNo);
        if (rechargeList != null && !rechargeList.isEmpty()) {
            // 返回最近一次充值的单号
            return String.valueOf(rechargeList.get(0).getRechargeNo());
        }
        // 如果没有充值记录，返回空字符串（数据库已允许NULL）
        return "";
    }
}
