package com.gym.controller;

import com.gym.entity.Common;
import com.gym.entity.Course;
import com.gym.service.CourseService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 课程 控制层
 *
 */
@RestController
@RequestMapping("/api")
public class CourseController {

    @Resource
    private CourseService courseService;

    @RequestMapping(path = "/getAllCourse")
    public List<Course> getAllCourse(int page, int size) {
        return courseService.getAllCourse(page, size);
    }

    @RequestMapping(path = "/getAllCourseRegister")
    public List<Course> getAllCourseRegister() {
        return courseService.getAllCourseRegister();
    }

    @RequestMapping(path = "/addCourse")
    public Map<String, Object> addCourse(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @RequestMapping(path = "/updateCourse")
    public Map<String, Object> updateCourse(Course course) {
        return courseService.updateCourse(course);
    }

    @RequestMapping(path = "/deleteCourse")
    public Map<String, Object> deleteEmployee(int courseNo) {
        return courseService.deleteCourse(courseNo);
    }

    @RequestMapping(path = "/totalCourse")
    public Common totalCourse() {
        return courseService.totalCourse();
    }

    @RequestMapping(path = "/getByKeywordCourse")
    public List<Course> getByKeywordCourse(String keyWord, int page, int size) {
        return courseService.getByKeywordCourse(keyWord, page, size);
    }

    @RequestMapping(path = "/totalCourseFuzzy")
    public Common totalCourseFuzzy(String keyWord) {
        return courseService.totalCourseFuzzy(keyWord);
    }

    @RequestMapping(path = "/getPurchasedCourses")
    public java.util.List<Course> getPurchasedCourses(String memberNo) {
        if (memberNo == null || memberNo.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return courseService.getMyPurchasedCourses(memberNo);
    }

    @RequestMapping(path = "/purchaseCourse")
    public Map<String, Object> purchaseCourse(Integer memberNo, Integer courseNo, Integer quantity) {
        return courseService.purchaseCourse(memberNo, courseNo, quantity);
    }

    /**
     * 获取会员课程包列表（管理员退课用）
     */
    @RequestMapping(path = "/admin/member/courses")
    public List<Map<String, Object>> getMemberCoursePackages(@RequestParam Integer memberNo) {
        return courseService.getMemberCoursePackages(memberNo);
    }

    /**
     * 处理退课
     */
    @RequestMapping(path = "/admin/course/refund")
    public Map<String, Object> processCourseRefund(
            @RequestParam Integer memberNo,
            @RequestParam(required = false) Long purchaseNo,
            @RequestParam Integer courseNo,
            @RequestParam Integer refundTimes,
            @RequestParam java.math.BigDecimal refundAmount,
            @RequestParam(required = false) String refundReason,
            @RequestParam(required = false) Integer operatorAdminId) {
        return courseService.processCourseRefund(memberNo, purchaseNo, courseNo, refundTimes, refundAmount, refundReason, operatorAdminId);
    }

}
