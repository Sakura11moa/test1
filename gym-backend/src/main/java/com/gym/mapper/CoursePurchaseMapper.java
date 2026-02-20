package com.gym.mapper;

import com.gym.entity.CoursePurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CoursePurchaseMapper {

    int insert(CoursePurchase purchase);

    /**
     * 根据购课单号查询购课记录
     */
    CoursePurchase getByPurchaseNo(@Param("purchaseNo") Long purchaseNo);

    List<CoursePurchase> listByMember(@Param("memberNo") Integer memberNo);
}