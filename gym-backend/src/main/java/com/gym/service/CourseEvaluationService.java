package com.gym.service;

import com.gym.entity.CourseEvaluation;
import com.gym.entity.VenueBooking;
import com.gym.mapper.CourseEvaluationMapper;
import com.gym.mapper.VenueBookingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseEvaluationService {

    @Resource
    private CourseEvaluationMapper courseEvaluationMapper;

    @Resource
    private VenueBookingMapper venueBookingMapper;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addEvaluation(CourseEvaluation evaluation) {
        Map<String, Object> resultMap = new HashMap<>();
        
        if (evaluation.getBookingNo() == null || evaluation.getScore() == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "评价失败，单号和评分不能为空");
            return resultMap;
        }

        // 1. 校验预约状态：必须是已核销(6)状态才能评价
        VenueBooking booking = venueBookingMapper.getVenueBookingByNo(evaluation.getBookingNo());
        if (booking == null) {
            resultMap.put("code", 400);
            resultMap.put("message", "预约记录不存在");
            return resultMap;
        }
        if (!"6".equals(booking.getStatus())) {
            resultMap.put("code", 400);
            resultMap.put("message", "只有已核销完成的课程才能评价");
            return resultMap;
        }

        // 2. 检查是否已评价过
        CourseEvaluation exists = courseEvaluationMapper.getByBookingNo(evaluation.getBookingNo());
        if (exists != null) {
            resultMap.put("code", 400);
            resultMap.put("message", "该预约已评价过");
            return resultMap;
        }

        int result = courseEvaluationMapper.insert(evaluation);
        if (result > 0) {
            resultMap.put("code", 200);
            resultMap.put("message", "评价成功");
        } else {
            resultMap.put("code", 500);
            resultMap.put("message", "评价写入异常");
        }
        return resultMap;
    }

    public CourseEvaluation getByBookingNo(Integer bookingNo) {
        return courseEvaluationMapper.getByBookingNo(bookingNo);
    }

    public List<CourseEvaluation> getCourseEvaluations(Integer courseNo) {
        return courseEvaluationMapper.listByCourseNo(courseNo);
    }
}
