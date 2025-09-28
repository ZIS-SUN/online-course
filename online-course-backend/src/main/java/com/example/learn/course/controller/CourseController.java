package com.example.learn.course.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.course.entity.Course;
import com.example.learn.course.entity.Enrollment;
import com.example.learn.course.entity.Section;
import com.example.learn.course.entity.Video;
import com.example.learn.course.service.CourseService;
import com.example.learn.user.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseDto>>> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses = courseService.getPublishedCourses(keyword, categoryId, pageable);
        Page<CourseDto> courseDtos = courses.map(CourseDto::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(courseDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDetailDto>> getCourse(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Course course = courseService.getCourseById(id);
        CourseDetailDto dto = CourseDetailDto.fromEntity(course);

        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            dto.setEnrolled(courseService.isEnrolled(user.getId(), id));
        }

        dto.setEnrollmentCount(courseService.getCourseEnrollmentCount(id));
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/outline")
    public ResponseEntity<ApiResponse<List<SectionDto>>> getCourseOutline(@PathVariable Long id) {
        List<Section> sections = courseService.getCourseSections(id);
        List<SectionDto> sectionDtos = sections.stream()
                .map(SectionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(sectionDtos));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentDto>> enrollCourse(
            @PathVariable Long id,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Please login first"));
        }

        User user = (User) authentication.getPrincipal();
        Enrollment enrollment = courseService.enrollCourse(user, id);
        return ResponseEntity.ok(ApiResponse.success(EnrollmentDto.fromEntity(enrollment)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<EnrollmentDto>>> getMyCourses(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Please login first"));
        }

        User user = (User) authentication.getPrincipal();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        Page<Enrollment> enrollments = courseService.getUserEnrollments(user.getId(), pageable);
        Page<EnrollmentDto> enrollmentDtos = enrollments.map(EnrollmentDto::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(enrollmentDtos));
    }

    @Data
    public static class CourseDto {
        private Long id;
        private String title;
        private String subtitle;
        private String description;
        private String coverUrl;
        private String level;
        private Boolean isFree;
        private Integer sectionCount;
        private Integer videoCount;
        private LocalDateTime createdAt;

        public static CourseDto fromEntity(Course course) {
            CourseDto dto = new CourseDto();
            dto.setId(course.getId());
            dto.setTitle(course.getTitle());
            dto.setSubtitle(course.getSubtitle());
            dto.setDescription(course.getDescription());
            dto.setCoverUrl(course.getCoverUrl());
            dto.setLevel(course.getLevel().name());
            dto.setIsFree(course.getIsFree());
            dto.setSectionCount(course.getSections().size());
            dto.setVideoCount(course.getSections().stream()
                    .mapToInt(s -> s.getVideos().size())
                    .sum());
            dto.setCreatedAt(course.getCreatedAt());
            return dto;
        }
    }

    @Data
    public static class CourseDetailDto extends CourseDto {
        private String categoryName;
        private String creatorName;
        private Boolean enrolled;
        private Long enrollmentCount;
        private List<SectionDto> sections;

        public static CourseDetailDto fromEntity(Course course) {
            CourseDetailDto dto = new CourseDetailDto();
            dto.setId(course.getId());
            dto.setTitle(course.getTitle());
            dto.setSubtitle(course.getSubtitle());
            dto.setDescription(course.getDescription());
            dto.setCoverUrl(course.getCoverUrl());
            dto.setLevel(course.getLevel().name());
            dto.setIsFree(course.getIsFree());
            dto.setCategoryName(course.getCategory() != null ? course.getCategory().getName() : null);
            dto.setCreatorName(course.getCreatedBy() != null ? course.getCreatedBy().getNickname() : null);
            dto.setCreatedAt(course.getCreatedAt());
            dto.setSections(course.getSections().stream()
                    .map(SectionDto::fromEntity)
                    .collect(Collectors.toList()));
            return dto;
        }
    }

    @Data
    public static class SectionDto {
        private Long id;
        private String title;
        private Integer sortOrder;
        private List<VideoDto> videos;

        public static SectionDto fromEntity(Section section) {
            SectionDto dto = new SectionDto();
            dto.setId(section.getId());
            dto.setTitle(section.getTitle());
            dto.setSortOrder(section.getSortOrder());
            dto.setVideos(section.getVideos().stream()
                    .map(VideoDto::fromEntity)
                    .collect(Collectors.toList()));
            return dto;
        }
    }

    @Data
    public static class VideoDto {
        private Long id;
        private String title;
        private Integer durationSeconds;
        private Boolean isFreePreview;
        private Integer sortOrder;

        public static VideoDto fromEntity(Video video) {
            VideoDto dto = new VideoDto();
            dto.setId(video.getId());
            dto.setTitle(video.getTitle());
            dto.setDurationSeconds(video.getDurationSeconds());
            dto.setIsFreePreview(video.getIsFreePreview());
            dto.setSortOrder(video.getSortOrder());
            return dto;
        }
    }

    @Data
    public static class EnrollmentDto {
        private Long id;
        private CourseDto course;
        private String status;
        private LocalDateTime enrolledAt;

        public static EnrollmentDto fromEntity(Enrollment enrollment) {
            EnrollmentDto dto = new EnrollmentDto();
            dto.setId(enrollment.getId());
            dto.setCourse(CourseDto.fromEntity(enrollment.getCourse()));
            dto.setStatus(enrollment.getStatus().name());
            dto.setEnrolledAt(enrollment.getEnrolledAt());
            return dto;
        }
    }
}