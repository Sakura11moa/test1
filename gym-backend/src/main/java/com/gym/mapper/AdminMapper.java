package com.gym.mapper;

import com.gym.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 管理员 仓库类
 *
 * @author: ShanZhu
 * @date: 2024-08-01
 */
@Mapper
@Repository
public interface AdminMapper {

    Admin getAdminPassword(String adminAccount, String adminPassword);

    int addAdmin(Admin admin);

    @Select("SELECT * FROM admin WHERE adminAccount = #{adminAccount} LIMIT 1")
    Admin getByAccount(String adminAccount);

}
