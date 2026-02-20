package com.gym.mapper;

import com.gym.entity.BookingAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BookingAuditLogMapper {

    int insert(BookingAuditLog log);

    List<BookingAuditLog> listByBookingNo(Integer bookingNo);
}
