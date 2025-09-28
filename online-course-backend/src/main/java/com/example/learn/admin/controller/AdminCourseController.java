package com.example.learn.admin.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.course.entity.Course;
import com.example.learn.course.entity.Section;
import com.example.learn.course.entity.Video;
import com.example.learn.course.repository.CourseRepository;
import com.example.learn.course.repository.EnrollmentRepository;
import com.example.learn.course.repository.SectionRepository;
import com.example.learn.course.repository.VideoRepository;
import com.example.learn.course.service.CourseService;
import com.example.learn.user.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SectionRepository sectionRepository;
    private final VideoRepository videoRepository;
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminCourseDto>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses;

        if (keyword.isEmpty()) {
            courses = courseRepository.findAll(pageable);
        } else {
            courses = courseRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        }

        Page<AdminCourseDto> courseDtos = courses.map(this::convertToAdminDto);
        return ResponseEntity.ok(ApiResponse.success(courseDtos));
    }

    private AdminCourseDto convertToAdminDto(Course course) {
        AdminCourseDto dto = new AdminCourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setSubtitle(course.getSubtitle());
        dto.setDescription(course.getDescription());
        dto.setLevel(course.getLevel().name());
        dto.setStatus(course.getStatus().name());
        dto.setIsFree(course.getIsFree());
        dto.setThumbnail(course.getCoverUrl());
        dto.setPrice(course.getIsFree() ? 0.0 : 0.0); // 设置默认价格，如果有价格字段可以添加
        dto.setCreatedAt(course.getCreatedAt());

        // Count sections using repository to avoid lazy loading issues
        long sectionCount = sectionRepository.countByCourse(course);
        dto.setSectionCount((int) sectionCount);

        // 暂时不统计video数量，避免N+1查询问题
        dto.setVideoCount(0);

        // 计算报名人数
        long enrollCount = enrollmentRepository.countByCourseId(course.getId());
        dto.setEnrollCount((int) enrollCount);

        return dto;
    }

    private VideoResponseDto convertToVideoResponseDto(Video video) {
        VideoResponseDto dto = new VideoResponseDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setSourceUrl(video.getSourceUrl());
        dto.setDurationSeconds(video.getDurationSeconds());
        dto.setIsFreePreview(video.getIsFreePreview());
        dto.setSortOrder(video.getSortOrder());
        dto.setTranscodeStatus(video.getTranscodeStatus().name());
        dto.setSectionId(video.getSection().getId());
        dto.setSectionTitle(video.getSection().getTitle());
        return dto;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Course>> createCourse(
            @Valid @RequestBody CourseDto courseDto,
            Authentication authentication
    ) {
        User admin = (User) authentication.getPrincipal();
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setSubtitle(courseDto.getSubtitle());
        course.setDescription(courseDto.getDescription());
        course.setCoverUrl(courseDto.getCoverUrl());
        course.setLevel(Course.CourseLevel.valueOf(courseDto.getLevel()));
        // 确保 isFree 有默认值
        course.setIsFree(courseDto.getIsFree() != null ? courseDto.getIsFree() : false);
        course.setStatus(Course.CourseStatus.DRAFT);

        Course savedCourse = courseService.createCourse(course, admin);
        return ResponseEntity.ok(ApiResponse.success(savedCourse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDto courseDto
    ) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(courseDto.getTitle());
        course.setSubtitle(courseDto.getSubtitle());
        course.setDescription(courseDto.getDescription());
        course.setCoverUrl(courseDto.getCoverUrl());
        course.setLevel(Course.CourseLevel.valueOf(courseDto.getLevel()));
        // 确保 isFree 有默认值
        course.setIsFree(courseDto.getIsFree() != null ? courseDto.getIsFree() : false);

        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(ApiResponse.success(updatedCourse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<Course>> publishCourse(@PathVariable Long id) {
        Course publishedCourse = courseService.publishCourse(id);
        return ResponseEntity.ok(ApiResponse.success(publishedCourse));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<Course>> archiveCourse(@PathVariable Long id) {
        Course archivedCourse = courseService.archiveCourse(id);
        return ResponseEntity.ok(ApiResponse.success(archivedCourse));
    }

    // Section management
    @GetMapping("/{courseId}/sections")
    public ResponseEntity<ApiResponse<List<Section>>> getCourseSections(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Section> sections = sectionRepository.findByCourseOrderBySortOrder(course);
        return ResponseEntity.ok(ApiResponse.success(sections));
    }

    @PostMapping("/{courseId}/sections")
    public ResponseEntity<ApiResponse<Section>> createSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionDto sectionDto
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Section section = new Section();
        section.setCourse(course);
        section.setTitle(sectionDto.getTitle());
        section.setSortOrder(sectionDto.getSortOrder());

        Section savedSection = sectionRepository.save(section);
        return ResponseEntity.ok(ApiResponse.success(savedSection));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<Section>> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody SectionDto sectionDto
    ) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        section.setTitle(sectionDto.getTitle());
        section.setSortOrder(sectionDto.getSortOrder());

        Section updatedSection = sectionRepository.save(section);
        return ResponseEntity.ok(ApiResponse.success(updatedSection));
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long id) {
        sectionRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // Video management
    @GetMapping("/sections/{sectionId}/videos")
    public ResponseEntity<ApiResponse<List<VideoResponseDto>>> getSectionVideos(@PathVariable Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        List<VideoResponseDto> videos = section.getVideos().stream()
                .map(this::convertToVideoResponseDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    @PostMapping("/sections/{sectionId}/videos")
    public ResponseEntity<ApiResponse<VideoResponseDto>> createVideo(
            @PathVariable Long sectionId,
            @Valid @RequestBody VideoDto videoDto
    ) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        Video video = new Video();
        video.setSection(section);
        video.setTitle(videoDto.getTitle());
        video.setSourceUrl(videoDto.getSourceUrl());
        video.setDurationSeconds(videoDto.getDurationSeconds());
        video.setIsFreePreview(videoDto.getIsFreePreview());
        video.setSortOrder(videoDto.getSortOrder());

        Video savedVideo = videoRepository.save(video);
        VideoResponseDto responseDto = convertToVideoResponseDto(savedVideo);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PostMapping("/videos/upload")
    public ResponseEntity<ApiResponse<String>> uploadVideo(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String uploadDir = "./uploads/videos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);
            Files.write(path, file.getBytes());

            String videoUrl = "/uploads/videos/" + filename;
            return ResponseEntity.ok(ApiResponse.success(videoUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to upload video: " + e.getMessage()));
        }
    }

    @PutMapping("/videos/{id}")
    public ResponseEntity<ApiResponse<VideoResponseDto>> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody VideoDto videoDto
    ) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        video.setTitle(videoDto.getTitle());
        video.setSourceUrl(videoDto.getSourceUrl());
        video.setDurationSeconds(videoDto.getDurationSeconds());
        video.setIsFreePreview(videoDto.getIsFreePreview());
        video.setSortOrder(videoDto.getSortOrder());

        Video updatedVideo = videoRepository.save(video);
        VideoResponseDto responseDto = convertToVideoResponseDto(updatedVideo);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable Long id) {
        videoRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Data
    public static class CourseDto {
        private String title;
        private String subtitle;
        private String description;
        private String coverUrl;
        private String level;
        private Boolean isFree;
    }

    @Data
    public static class SectionDto {
        private String title;
        private Integer sortOrder;
    }

    @Data
    public static class VideoDto {
        private String title;
        private String sourceUrl;
        private Integer durationSeconds;
        private Boolean isFreePreview;
        private Integer sortOrder;
    }

    @Data
    public static class AdminCourseDto {
        private Long id;
        private String title;
        private String subtitle;
        private String description;
        private String level;
        private String status;
        private Boolean isFree;
        private String thumbnail;
        private Double price;
        private Integer sectionCount;
        private Integer videoCount;
        private Integer enrollCount;
        private java.time.LocalDateTime createdAt;
    }

    @Data
    public static class VideoResponseDto {
        private Long id;
        private String title;
        private String sourceUrl;
        private Integer durationSeconds;
        private Boolean isFreePreview;
        private Integer sortOrder;
        private String transcodeStatus;
        private Long sectionId;
        private String sectionTitle;
    }
}