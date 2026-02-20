package com.gym.mapper;

import com.gym.entity.VenueSlotLock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VenueSlotLockMapper {

    int insert(VenueSlotLock lock);

    /**
     * 查询某场地在指定时间范围内的有效锁定
     */
    List<VenueSlotLock> findOverlappingLocks(@Param("venueNo") Integer venueNo, 
                                            @Param("startTime") java.time.LocalDateTime startTime, 
                                            @Param("endTime") java.time.LocalDateTime endTime);

    int updateStatus(@Param("lockNo") Long lockNo, @Param("status") Integer status);
}
