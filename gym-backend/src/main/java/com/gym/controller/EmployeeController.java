package com.gym.controller;

import com.gym.entity.Common;
import com.gym.entity.Employee;
import com.gym.service.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 员工 控制层
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    @RequestMapping(path = "/getAllEmployee")
    public List<Employee> getAllEmployee(int page, int size) {
        return employeeService.getAllEmployee(page, size);
    }

    @RequestMapping(path = "/addEmployee")
    public Map<String, Object> addEmployee(Employee employee, String account, String password) {
        return employeeService.addEmployee(employee, account, password);
    }

    @RequestMapping(path = "/updateEmployee")
    public Map<String, Object> updateEmployee(Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    @RequestMapping(path = "/deleteEmployee")
    public Map<String, Object> deleteEmployee(int employeeNo) {
        return employeeService.deleteEmployee(employeeNo);
    }

    @RequestMapping(path = "/totalEmployee")
    public Common totalEmployee() {
        return employeeService.totalEmployee();
    }

    @RequestMapping(path = "/getByKeywordEmployee")
    public List<Employee> getByKeywordEmployee(String keyWord, int page, int size) {
        return employeeService.getByKeywordEmployee(keyWord, page, size);
    }

    @RequestMapping(path = "/totalEmployeeFuzzy")
    public Common totalEmployeeFuzzy(String keyWord) {
        return employeeService.totalEmployeeFuzzy(keyWord);
    }

}

