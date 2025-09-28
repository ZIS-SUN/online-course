package com.example.learn.admin.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.course.repository.CourseRepository;
import com.example.learn.course.repository.EnrollmentRepository;
import com.example.learn.progress.repository.VideoProgressRepository;
import com.example.learn.user.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminStatsController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final VideoProgressRepository videoProgressRepository;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<StatsOverview>> getOverview() {
        StatsOverview overview = new StatsOverview();
        overview.setTotalCourses(courseRepository.count());
        overview.setTotalUsers(userRepository.count());
        overview.setTotalEnrollments(enrollmentRepository.count());
        overview.setActiveUsers(userRepository.count()); // TODO: Add actual active users logic

        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<CourseStats>> getCourseStats(@PathVariable Long courseId) {
        CourseStats stats = new CourseStats();
        stats.setCourseId(courseId);
        stats.setEnrollmentCount(enrollmentRepository.countByCourseId(courseId));
        stats.setCompletedCount(videoProgressRepository.countCompletedByCourseId(courseId));
        stats.setActiveUsers(videoProgressRepository.countDistinctUsersByCourseId(courseId));

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrends() {
        Map<String, Object> trends = new HashMap<>();

        // TODO: Implement actual trend calculations
        trends.put("enrollmentTrend", "increasing");
        trends.put("completionRate", 65.5);
        trends.put("averageProgress", 45.2);

        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @Data
    public static class StatsOverview {
        private Long totalCourses;
        private Long totalUsers;
        private Long totalEnrollments;
        private Long activeUsers;
    }

    @Data
    public static class CourseStats {
        private Long courseId;
        private Long enrollmentCount;
        private Long completedCount;
        private Long activeUsers;
        private Double completionRate;
    }
}