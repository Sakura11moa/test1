package com.gym.mapper;

import com.gym.entity.ReceivableTransfer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ReceivableTransferMapper {

    /**
     * 插入划转记录
     */
    int insert(ReceivableTransfer transfer);

    /**
     * 根据ID查询
     */
    ReceivableTransfer getById(Long id);

    /**
     * 查询会员的划转记录
     */
    List<ReceivableTransfer> getByMemberNo(Integer memberNo);

    /**
     * 查询有效的划转记录
     */
    List<ReceivableTransfer> getValidList(@Param("memberNo") Integer memberNo);

    /**
     * 取消划转
     */
    int cancel(@Param("id") Long id);
}
