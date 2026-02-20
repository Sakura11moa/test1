package com.gym.service;

import cn.hutool.crypto.digest.MD5;
import com.gym.entity.Common;
import com.gym.entity.Member;
import com.gym.mapper.MemberMapper;
import com.gym.mapper.RegisterMapper;
import com.gym.utils.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员 服务类
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private RegisterMapper registerMapper;

    // 简单内存存储验证码：手机号 -> 验证码 (开发阶段使用)
    private static final Map<String, String> resetCodeMap = new HashMap<>();

    public Map<String, Object> sendResetCode(String memberPhone) {
        Map<String, Object> resultMap = new HashMap<>();
        Member member = memberMapper.getMemberByPhone(memberPhone);
        if (member == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "该手机号未注册");
            return resultMap;
        }

        // 生成6位随机验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        resetCodeMap.put(memberPhone, code);

        resultMap.put("code", 200);
        resultMap.put("message", "验证码已发送");
        resultMap.put("resetCode", code); // A方案：直接返回给前端方便测试
        return resultMap;
    }

    public Map<String, Object> resetPassword(String memberPhone, String code, String newPassword) {
        Map<String, Object> resultMap = new HashMap<>();
        String savedCode = resetCodeMap.get(memberPhone);

        if (savedCode == null || !savedCode.equals(code)) {
            resultMap.put("code", 400);
            resultMap.put("message", "验证码错误或已失效");
            return resultMap;
        }

        // 密码加密并更新
        String encryptedPassword = MD5.create().digestHex(newPassword);
        int result = memberMapper.updateMemberPassword(memberPhone, encryptedPassword);

        if (result > 0) {
            resetCodeMap.remove(memberPhone); // 重置成功后移除验证码
            resultMap.put("code", 200);
            resultMap.put("message", "密码重置成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "重置失败，请稍后重试");
        }
        return resultMap;
    }

    public List<Member> getMemberMapper(int page, int size) {
        return memberMapper.getAllMember(page, size);
    }

    public List<Member> getAllMemberNoPage() {
        return memberMapper.getAllMemberNoPage();
    }

    public Map<String, Object> saveMemberBatch(List<Member> list) {
        Map<String, Object> resultMap = new HashMap<>();

        int result = memberMapper.saveMemberBatch(list);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "导入成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "导入失败");
        }

        return resultMap;
    }

    public Map<String, Object> addMember(Member member) {

        Map<String, Object> resultMap = new HashMap<>();


        /*随机生成用户名*/
        int Username = java.util.UUID.randomUUID().toString().hashCode();
        if (Username < 0) {
            Username = -Username;
        }
        // 0 代表前面补充0
        // 10 代表长度为10
        // d 代表参数为正数型
        String format = String.format("%010d", Username).substring(0, 10);
        member.setMemberUsername(format);

        /*默认密码*/
        member.setMemberPassword("123456");
        member.setCardTime(LocalDateTime.now());

        /*新增用户时将购买时间=剩余时间*/
        member.setCardNextClass(member.getCardClass());

        int result = memberMapper.addMember(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "创建成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "创建失败");
        }

        return resultMap;
    }

    public Map<String, Object> deleteMember(int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        int result = memberMapper.deleteMember(memberNo);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "删除成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "删除失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateMember(Member member) {

        Map<String, Object> resultMap = new HashMap<>();


        int result = memberMapper.updateMember(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "修改失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateMemberByMemberNo(Member member) {

        Map<String, Object> resultMap = new HashMap<>();


        int result = memberMapper.updateMemberByMemberNo(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "修改失败");
        }
        return resultMap;
    }

    public Common totalMember() {
        return memberMapper.totalMember();
    }

    public List<Member> getByKeywordMember(String keyWord, int page, int size) {
        return memberMapper.getByKeywordMember(keyWord, page, size);
    }

    public Common totalMemberFuzzy(String keyWord) {
        return memberMapper.totalMemberFuzzy(keyWord);
    }


    public Map<String, Object> getMemberPassword(String memberPhone, String memberPassword) {
        Map<String, Object> resultMap = new HashMap<>();
        // 密码加密后进行验证
        String encryptedPassword = MD5.create().digestHex(memberPassword);
        Member result = memberMapper.getMemberPassword(memberPhone, encryptedPassword);

        if (result != null) {
            result.setToken(JwtUtil.createTokenToMember());

            resultMap.put("token", result.getToken());
            resultMap.put("memberUsername", result.getMemberUsername());
            resultMap.put("memberNo", result.getMemberNo());
            resultMap.put("memberPhone", result.getMemberPhone());
            resultMap.put("code", 200);
            resultMap.put("message", "登录成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "登录失败");
        }
        return resultMap;
    }

    public Member getMemberByMemberNo(int memberNo) {
        return memberMapper.getMemberByMemberNo(memberNo);
    }

    public double getMemberIntegral(int memberNo) {
        return memberMapper.getMemberIntegral(memberNo);
    }


    public double getMemberPower(int memberNo) {
        return memberMapper.getMemberPower(memberNo);
    }

    public Double getMemberChange(int memberNo) {
        return memberMapper.getMemberChange(memberNo);
    }

    public Double getTotalMoney(int memberNo) {
        Double totalMoney = memberMapper.getTotalMoney(memberNo);
        if (totalMoney == null) {
            totalMoney = Double.valueOf(0);
        }
        return totalMoney;
    }


    public int updateMemberChange(int memberNo) {
        //获取购物总额
        Double totalBuy = registerMapper.getTotalBuyByMemberNo(memberNo);
        if (totalBuy == null) {
            totalBuy = Double.valueOf(0);
        }
        return memberMapper.updateMemberChange(memberNo, totalBuy);
    }

    public int updateMemberChangeByMemberNo(int memberNo, double coursePrice) {
        return memberMapper.updateMemberChangeByMemberNo(memberNo, coursePrice);
    }

    public int updateMemberIntegral(double price, int memberNo) {
        return memberMapper.updateMemberIntegral(price, memberNo);
    }

    public Map<String, Object> updateMemberPassword(String memberPhone, String memberPassword) {
        Map<String, Object> resultMap = new HashMap<>();

        // 密码加密
        String encryptedPassword = MD5.create().digestHex(memberPassword);
        int result = memberMapper.updateMemberPassword(memberPhone, encryptedPassword);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "密码修改成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "密码修改失败");
        }
        return resultMap;
    }

    public Map<String, Object> updateMemberPower(int memberPower, int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        int result = memberMapper.updateMemberPower(memberPower, memberNo);
        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "兑换成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "兑换失败");
        }
        return resultMap;
    }

    public Map<String, Object> registerMember(Member member) {
        Map<String, Object> resultMap = new HashMap<>();

        // 检查手机号是否已注册
        Member existingMember = memberMapper.getMemberByPhone(member.getMemberPhone());
        if (existingMember != null) {
            resultMap.put("code", 400);
            resultMap.put("message", "该手机号已注册");
            return resultMap;
        }

        // 设置默认值
        member.setMemberUsername(member.getMemberPhone()); // 使用手机号作为用户名
        member.setMemberIntegral(0); // 默认积分0
        member.setMemberChange(0); // 默认余额0
        member.setMemberPower("0"); // 默认会员等级0

        // 密码加密
        String encryptedPassword = MD5.create().digestHex(member.getMemberPassword());
        member.setMemberPassword(encryptedPassword);

        // 注册会员
        int result = memberMapper.registerMember(member);

        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "注册成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "注册失败");
        }

        return resultMap;
    }

    /**
     * 获取会员卡状态信息
     * @param memberNo 会员编号
     * @return cardStatus(1有效/0过期/2停卡), expireTime, daysLeft(剩余天数)
     */
    public Map<String, Object> getCardStatus(int memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        Member member = memberMapper.getMemberByMemberNo(memberNo);
        if (member == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "会员不存在");
            return resultMap;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = member.getExpireTime();
        Integer cardStatus = member.getCardStatus();

        // 计算剩余天数
        long daysLeft = 0;
        if (expireTime != null) {
            daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expireTime.toLocalDate());
        }

        // 统一状态描述：优先根据实际过期时间判断，再考虑卡状态字段
        String statusDesc = "有效";
        // 如果没有到期时间，或者已过期
        if (expireTime == null || expireTime.isBefore(now)) {
            statusDesc = "已过期";
        } else if (cardStatus != null && cardStatus == 2) {
            statusDesc = "已停卡";
        }

        // 如果状态描述是"有效"，但卡状态字段为0或null，需要同步更新
        // 这种情况下以实际过期时间为准，状态字段可能未及时更新
        if ("有效".equals(statusDesc) && cardStatus != null && cardStatus != 1) {
            // 实际有效但状态字段不是1，这种情况应该是正常的（可能状态字段有其他含义）
        }

        resultMap.put("code", 200);
        resultMap.put("expireTime", expireTime);
        resultMap.put("cardStatus", cardStatus != null ? cardStatus : 0);
        resultMap.put("daysLeft", daysLeft);
        resultMap.put("statusDesc", statusDesc);
        return resultMap;
    }

}
