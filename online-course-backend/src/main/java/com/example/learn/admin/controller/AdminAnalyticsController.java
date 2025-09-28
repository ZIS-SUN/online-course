package com.example.learn.admin.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.course.repository.CourseRepository;
import com.example.learn.user.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<OverviewStats>> getOverviewStats() {
        OverviewStats stats = new OverviewStats();

        // 获取真实数据
        stats.setTotalUsers(userRepository.count());
        stats.setTotalCourses(courseRepository.count());

        // 计算其他统计数据（这里先用模拟数据，实际项目中需要从数据库查询）
        stats.setActiveUsers(Math.max(1, (int)(stats.getTotalUsers() * 0.6))); // 60% 活跃用户
        stats.setTotalRevenue(stats.getTotalCourses() * 199.0); // 假设平均课程价格199元

        // 增长率（模拟数据）
        stats.setUserGrowthRate(8.5);
        stats.setCourseGrowthRate(12.3);
        stats.setActiveUserGrowthRate(5.2);
        stats.setRevenueGrowthRate(15.6);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/user-trends")
    public ResponseEntity<ApiResponse<List<TrendData>>> getUserTrends(@RequestParam(defaultValue = "7") int days) {
        List<TrendData> trends = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 生成过去n天的趋势数据
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            TrendData trend = new TrendData();
            trend.setDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            trend.setValue((int)(Math.random() * 50 + 20)); // 20-70之间的随机数
            trends.add(trend);
        }

        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @GetMapping("/course-trends")
    public ResponseEntity<ApiResponse<List<TrendData>>> getCourseTrends(@RequestParam(defaultValue = "7") int days) {
        List<TrendData> trends = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            TrendData trend = new TrendData();
            trend.setDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            trend.setValue((int)(Math.random() * 30 + 10)); // 10-40之间的随机数
            trends.add(trend);
        }

        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @GetMapping("/revenue-trends")
    public ResponseEntity<ApiResponse<List<TrendData>>> getRevenueTrends(@RequestParam(defaultValue = "7") int days) {
        List<TrendData> trends = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            TrendData trend = new TrendData();
            trend.setDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            trend.setValue((int)(Math.random() * 5000 + 2000)); // 2000-7000之间的随机数
            trends.add(trend);
        }

        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @GetMapping("/category-distribution")
    public ResponseEntity<ApiResponse<List<CategoryDistribution>>> getCategoryDistribution() {
        List<CategoryDistribution> distribution = Arrays.asList(
            new CategoryDistribution("编程开发", 35),
            new CategoryDistribution("设计创意", 25),
            new CategoryDistribution("数据科学", 20),
            new CategoryDistribution("商业管理", 15),
            new CategoryDistribution("语言学习", 5)
        );

        return ResponseEntity.ok(ApiResponse.success(distribution));
    }

    @GetMapping("/popular-courses")
    public ResponseEntity<ApiResponse<List<PopularCourse>>> getPopularCourses() {
        List<PopularCourse> courses = Arrays.asList(
            new PopularCourse("Java编程入门", 1245, 4.8),
            new PopularCourse("Spring Boot实战", 986, 4.7),
            new PopularCourse("Vue.js前端开发", 875, 4.6),
            new PopularCourse("Python数据分析", 654, 4.5),
            new PopularCourse("React开发实战", 543, 4.4)
        );

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @Data
    public static class OverviewStats {
        private long totalUsers;
        private long totalCourses;
        private long activeUsers;
        private double totalRevenue;
        private double userGrowthRate;
        private double courseGrowthRate;
        private double activeUserGrowthRate;
        private double revenueGrowthRate;
    }

    @Data
    public static class TrendData {
        private String date;
        private int value;
    }

    @Data
    public static class CategoryDistribution {
        private String name;
        private int percentage;

        public CategoryDistribution(String name, int percentage) {
            this.name = name;
            this.percentage = percentage;
        }
    }

    @Data
    public static class PopularCourse {
        private String name;
        private int enrollments;
        private double rating;

        public PopularCourse(String name, int enrollments, double rating) {
            this.name = name;
            this.enrollments = enrollments;
            this.rating = rating;
        }
    }
}