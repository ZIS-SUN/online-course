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
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // 获取真实数据
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();

        stats.setTotalUsers(totalUsers);
        stats.setTotalCourses(totalCourses);
        stats.setActiveUsers(Math.max(1, (int)(totalUsers * 0.7))); // 70% 活跃用户
        stats.setMonthlyRevenue(totalCourses * 2500.0); // 假设平均月收入

        // 模拟增长率
        stats.setUserGrowthRate(8.5);
        stats.setCourseGrowthRate(12.3);
        stats.setActiveUserGrowthRate(6.8);
        stats.setRevenueGrowthRate(15.2);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<ApiResponse<List<Activity>>> getRecentActivities() {
        List<Activity> activities = Arrays.asList(
            new Activity("新用户注册", "张三刚刚注册了账号", "2分钟前", "user"),
            new Activity("课程发布", "Java编程基础课程已发布", "5分钟前", "course"),
            new Activity("订单完成", "李四购买了Vue.js实战课程", "8分钟前", "order"),
            new Activity("课程完成", "王五完成了Python入门课程", "12分钟前", "completion"),
            new Activity("用户反馈", "收到新的5星评价", "15分钟前", "feedback"),
            new Activity("系统更新", "课程推荐算法已优化", "1小时前", "system"),
            new Activity("新用户注册", "赵六注册并购买了会员", "2小时前", "user"),
            new Activity("课程更新", "React高级教程增加新章节", "3小时前", "course")
        );

        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/quick-stats")
    public ResponseEntity<ApiResponse<QuickStats>> getQuickStats() {
        QuickStats stats = new QuickStats();

        // 今日数据（模拟）
        stats.setTodayUsers(Math.max(1, (int)(Math.random() * 50 + 10)));
        stats.setTodayOrders(Math.max(1, (int)(Math.random() * 20 + 5)));
        stats.setTodayRevenue(Math.random() * 5000 + 1000);
        stats.setOnlineUsers(Math.max(1, (int)(Math.random() * 100 + 20)));

        // 对比昨日
        stats.setTodayUsersChange(Math.random() * 20 - 10); // -10% 到 +10%
        stats.setTodayOrdersChange(Math.random() * 30 - 15); // -15% 到 +15%
        stats.setTodayRevenueChange(Math.random() * 25 - 12.5); // -12.5% 到 +12.5%
        stats.setOnlineUsersChange(Math.random() * 40 - 20); // -20% 到 +20%

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Data
    public static class DashboardStats {
        private long totalUsers;
        private long totalCourses;
        private long activeUsers;
        private double monthlyRevenue;
        private double userGrowthRate;
        private double courseGrowthRate;
        private double activeUserGrowthRate;
        private double revenueGrowthRate;
    }

    @Data
    public static class Activity {
        private String title;
        private String description;
        private String time;
        private String type;

        public Activity(String title, String description, String time, String type) {
            this.title = title;
            this.description = description;
            this.time = time;
            this.type = type;
        }
    }

    @Data
    public static class QuickStats {
        private int todayUsers;
        private int todayOrders;
        private double todayRevenue;
        private int onlineUsers;
        private double todayUsersChange;
        private double todayOrdersChange;
        private double todayRevenueChange;
        private double onlineUsersChange;
    }
}