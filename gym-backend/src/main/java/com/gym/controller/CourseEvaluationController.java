package com.gym.controller;

import com.gym.entity.CourseEvaluation;
import com.gym.service.CourseEvaluationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 课程评价 控制层
 */
@RestController
@RequestMapping("/api")
public class CourseEvaluationController {

    @Resource
    private CourseEvaluationService courseEvaluationService;

    /**
     * 提交课程评价
     * POST /evaluateBooking
     */
    @PostMapping("/evaluateBooking")
    @CrossOrigin
    public Map<String, Object> evaluateBooking(@RequestBody CourseEvaluation evaluation) {
        return courseEvaluationService.addEvaluation(evaluation);
    }

    /**
     * 根据课程查询评价列表
     * GET /getCourseEvaluations?courseNo=1
     */
    @GetMapping("/getCourseEvaluations")
    @CrossOrigin
    public List<CourseEvaluation> getCourseEvaluations(@RequestParam Integer courseNo) {
        return courseEvaluationService.getCourseEvaluations(courseNo);
    }

    /**
     * 根据预约单号查询评价
     * GET /getEvaluationByBooking?bookingNo=1
     */
    @GetMapping("/getEvaluationByBooking")
    @CrossOrigin
    public CourseEvaluation getEvaluationByBooking(@RequestParam Integer bookingNo) {
        return courseEvaluationService.getByBookingNo(bookingNo);
    }
}
