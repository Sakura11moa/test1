package com.gym.service;

import com.gym.entity.Common;
import com.gym.entity.Employee;
import com.gym.entity.Admin;
import com.gym.mapper.AdminMapper;
import com.gym.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工 服务类
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Service
public class EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private AdminMapper adminMapper;

    public List<Employee> getAllEmployee(int page, int size){
        return employeeMapper.getAllEmployee(page,size);
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> addEmployee(Employee employee, String account, String password){
        Map<String,Object> resultMap = new HashMap<>();
        
        // 1. 如果是教练且提供了账号，先检查账号是否已存在
        if ("1".equals(String.valueOf(employee.getEmployeeJob())) && account != null && !account.isEmpty()) {
            if (adminMapper.getByAccount(account) != null) {
                resultMap.put("code", 400);
                resultMap.put("message", "添加失败，该登录账号已存在");
                return resultMap;
            }
        }

        // 2. 设置入职时间并新增员工
        employee.setEmployeeTime(LocalDateTime.now());
        int result =  employeeMapper.addEmployee(employee);

        if(result > 0){
            // 3. 如果是教练，创建 admin 账号
            if ("1".equals(String.valueOf(employee.getEmployeeJob())) && account != null && !account.isEmpty()) {
                Admin admin = new Admin();
                admin.setAdminAccount(account);
                admin.setAdminPassword(password);
                admin.setEmployeeNo(employee.getEmployeeNo()); // 自动获取新增的自增ID
                adminMapper.addAdmin(admin);
            }
            
            resultMap.put("code", 200);
            resultMap.put("message", "添加成功");
        } else {
            resultMap.put("code", 400);
            resultMap.put("message", "添加失败");
        }

        return resultMap;
    }

    public Map<String,Object>  updateEmployee(Employee employee) {
        Map<String,Object> resultMap = new HashMap<>();
        int result =  employeeMapper.updateEmployee(employee);

        if(result>0){
            resultMap.put("code",200);
            resultMap.put("message","修改成功");
        }else{
            resultMap.put("code",400);
            resultMap.put("message","修改失败");
        }

        return resultMap;
    }

    public Map<String,Object>  deleteEmployee(int employeeNo) {
        Map<String,Object> resultMap = new HashMap<>();
        int result =  employeeMapper.deleteEmployee(employeeNo);

        if(result>0){
            resultMap.put("code",200);
            resultMap.put("message","删除成功");
        }else{
            resultMap.put("code",400);
            resultMap.put("message","删除失败");
        }

        return resultMap;
    }

    public Common totalEmployee() {
        return employeeMapper.totalEmployee();
    }

    public List<Employee> getByKeywordEmployee(String keyWord,int page,int size) {
        return  employeeMapper.getByKeywordEmployee(keyWord,page,size);
    }

    public Common totalEmployeeFuzzy(String keyWord){
        return employeeMapper.totalEmployeeFuzzy(keyWord);
    }

}


