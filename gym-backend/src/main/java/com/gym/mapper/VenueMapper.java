package com.gym.mapper;

import com.gym.entity.Common;
import com.gym.entity.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 场地 仓库类
 *
 * @author: ShanZhu
 * @date: 2024-12-26
 */
@Mapper
@Repository
public interface VenueMapper {
    List<Venue> getAllVenue(int page, int size);

    int addVenue(Venue venue);

    int updateVenue(Venue venue);

    int deleteVenue(int venueNo);

    /**
     * 根据日期和时间范围查询可用的场地列表
     */
    List<Venue> findAvailableVenues(@Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);

    Venue getVenueByNo(@Param("venueNo") Integer venueNo);

    Common totalVenue();

    List<Venue> getByKeywordVenue(String keyWord,int page,int size);

    Common totalVenueFuzzy(String keyWord);

    /**
     * 原子占用场地：仅当场地为可用(venue_state='1')时，更新为已预订(venue_state='3')
     * @return 受影响行数，1=成功，0=失败
     */
    int occupyVenueIfAvailable(int venueNo);

    /**
     * 释放场地：将场地状态恢复为可用(venue_state='1')
     */
    int releaseVenue(int venueNo);
}
