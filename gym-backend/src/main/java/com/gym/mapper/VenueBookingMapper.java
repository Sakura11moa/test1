package com.gym.mapper;

import com.gym.entity.MyVenueBooking;
import com.gym.entity.VenueBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 场地预约记录 仓库类
 */
@Mapper
@Repository
public interface VenueBookingMapper {

    /**
     * 管理员端：查询全量预约记录（含会员、课程、场地详情）
     */
    List<com.gym.entity.AdminVenueBookingVO> getAllBookingsWithDetails();

    int addVenueBooking(VenueBooking venueBooking);

    /**
     * 更新预约关联的库存流水号
     */
    int updateConsumeLogNo(@Param("bookingNo") Integer bookingNo, @Param("consumeLogNo") Long consumeLogNo);

    /**
     * 更新预约关联的购课单号
     */
    int updatePurchaseNo(@Param("bookingNo") Integer bookingNo, @Param("purchaseNo") Integer purchaseNo);

    /**
     * 仅允许取消自己的、且当前有效(status='0')的预约
     * @return 受影响行数，1=成功，0=失败
     */
    int cancelVenueBooking(@Param("bookingNo") int bookingNo, @Param("memberNo") int memberNo);

    /**
     * 更新预约状态及其审批信息
     */
    int updateBookingStatus(@Param("bookingNo") Integer bookingNo, 
                           @Param("oldStatus") String oldStatus, 
                           @Param("newStatus") String newStatus, 
                           @Param("adminId") Integer adminId, 
                           @Param("rejectReason") String rejectReason,
                           @Param("version") Integer version);

    /**
     * 获取所有待审批的预约
     */
    List<VenueBooking> getPendingBookings();

    /**
     * 获取指定教练负责的待审批预约
     */
    List<VenueBooking> getPendingBookingsByCoach(@Param("employeeNo") Integer employeeNo);

    /**
     * 根据bookingNo获取预约详情（用于释放场地）
     */
    VenueBooking getVenueBookingByNo(int bookingNo);

    /**
     * 查询我的预约记录（JOIN course + venue）
     */
    List<MyVenueBooking> getMyVenueBookings(int memberNo);

    /**
     * 查询指定日期各场地的已占用时段（status='0'）
     * @param date yyyy-MM-dd
     * @return List<venueNo, timeSlot>
     */
    List<java.util.Map<String, Object>> getBookedSlotsByDate(@Param("date") String date);

    /**
     * 查询某场地在指定时间范围内的有效预约（用于场地冲突检测），支持排除特定预约ID
     */
    List<VenueBooking> findOverlappingBookingsExcl(@Param("venueNo") Integer venueNo, 
                                                  @Param("startTime") java.time.LocalDateTime startTime, 
                                                  @Param("endTime") java.time.LocalDateTime endTime,
                                                  @Param("excludeBookingNo") Integer excludeBookingNo);

    /**
     * 查询某会员在指定时间范围内的有效预约（用于会员冲突检测），支持排除特定预约ID
     */
    List<VenueBooking> findMemberOverlappingBookingsExcl(@Param("memberNo") Integer memberNo, 
                                                        @Param("startTime") java.time.LocalDateTime startTime, 
                                                        @Param("endTime") java.time.LocalDateTime endTime,
                                                        @Param("excludeBookingNo") Integer excludeBookingNo);

    /**
     * 统计指定场地在指定日期仍存在的有效预约数量
     */
    int countActiveBookingsByVenueDate(@Param("venueNo") int venueNo, @Param("bookingDate") String bookingDate);

    /**
     * 统计指定会员在指定日期和时段的有效预约数量
     */
    int countMemberActiveBookingsBySlot(@Param("memberNo") int memberNo, @Param("date") String date, @Param("timeSlot") String timeSlot);

    /**
     * 统计指定会员在指定日期的已预约总时长（分钟）
     * 修复：场地预约单日时长上限校验
     */
    Integer getMemberDayTotalMinutes(@Param("memberNo") int memberNo, @Param("date") String date);

    // 修复：接口幂等性查询方法
    VenueBooking selectByRequestNo(@Param("requestNo") String requestNo);

    // 修复：并发预约超售问题，新增原子插入方法
    int insertIfNotConflict(
        @Param("venueNo") Integer venueNo,
        @Param("memberNo") Integer memberNo,
        @Param("startTime") java.time.LocalDateTime startTime,
        @Param("endTime") java.time.LocalDateTime endTime,
        @Param("requestNo") String requestNo
    );
}


