package com.gym.service;

import com.gym.entity.Admin;
import com.gym.mapper.AdminMapper;
import com.gym.utils.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员 服务类
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    public Map<String,Object> getAdminPassword(String adminAccount, String adminPassword, String type){
        Map<String,Object> resultMap = new HashMap<>();
        Admin result = adminMapper.getAdminPassword(adminAccount,adminPassword);

        if(result != null){
            // 角色校验逻辑
            boolean isCoach = result.getEmployeeNo() != null;
            
            // 如果从管理员入口(type="admin")登录，但账号是教练身份
            if ("admin".equals(type) && isCoach) {
                resultMap.put("code", 403);
                resultMap.put("message", "该账号为教练账号，请从教练入口登录");
                return resultMap;
            }
            
            // 如果从教练入口(type="coach")登录，但账号没有绑定员工
            if ("coach".equals(type) && !isCoach) {
                resultMap.put("code", 403);
                resultMap.put("message", "该账号非教练账号，请从管理员入口登录");
                return resultMap;
            }

            result.setToken(JwtUtil.createToken());
            resultMap.put("token",result.getToken());
            resultMap.put("adminAccount",adminAccount);
            resultMap.put("adminNo", result.getAdminNo());
            resultMap.put("employeeNo", result.getEmployeeNo());
            resultMap.put("employeeJob", result.getEmployeeJob());
            resultMap.put("code",200);
            resultMap.put("message","登录成功");

        }else{
            resultMap.put("code",400);
            resultMap.put("message","账号或密码错误");
        }
        return resultMap;
    }

}
