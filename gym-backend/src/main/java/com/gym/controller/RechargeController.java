package com.gym.controller;

import com.gym.entity.Recharge;
import com.gym.service.RechargeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 充值 控制层
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@RestController
@RequestMapping("/api")
public class RechargeController {

    @Resource
    private RechargeService rechargeService;

    @RequestMapping(path="/getRechargeByMemberNo")
    public List<Recharge> getRechargeByMemberNo(int memberNo){
        return rechargeService.getRechargeByMemberNo(memberNo);
    }

    @RequestMapping(path = "/addRechargeByMemberNo")
    public Map<String,Object> addRechargeByMemberNo(Recharge recharge) {
        return rechargeService.addRechargeByMemberNo(recharge);
    }


}
