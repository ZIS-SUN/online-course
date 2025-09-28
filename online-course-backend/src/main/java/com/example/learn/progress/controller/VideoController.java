package com.example.learn.progress.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.course.entity.Video;
import com.example.learn.course.repository.EnrollmentRepository;
import com.example.learn.course.repository.VideoRepository;
import com.example.learn.progress.entity.VideoProgress;
import com.example.learn.progress.service.ProgressService;
import com.example.learn.user.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoRepository videoRepository;
    private final ProgressService progressService;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoDto>> getVideo(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        // Check if user can access this video
        if (!Boolean.TRUE.equals(video.getIsFreePreview())) {
            // Non-preview video requires enrollment
            if (authentication == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("Please login to watch this video"));
            }

            User user = (User) authentication.getPrincipal();
            Long courseId = video.getSection().getCourse().getId();

            if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Please enroll in the course first"));
            }
        }
        // Free preview videos can be accessed by anyone

        VideoDto dto = VideoDto.fromEntity(video);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<ProgressDto>> getVideoProgress(
            @PathVariable Long id,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Please login first"));
        }

        User user = (User) authentication.getPrincipal();
        VideoProgress progress = progressService.getVideoProgress(user.getId(), id);

        ProgressDto dto = new ProgressDto();
        dto.setLastPositionSeconds(progress.getLastPositionSeconds());
        dto.setMaxPositionSeconds(progress.getMaxPositionSeconds());
        dto.setPercentage(progress.getPercentage());
        dto.setIsCompleted(progress.getIsCompleted());
        dto.setPlaybackRate(progress.getPlaybackRate());

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<ProgressResponseDto>> updateVideoProgress(
            @PathVariable Long id,
            @RequestBody ProgressService.UpdateProgressRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Please login first"));
        }

        User user = (User) authentication.getPrincipal();
        VideoProgress progress = progressService.updateVideoProgress(user, id, request);

        ProgressResponseDto response = new ProgressResponseDto();
        response.setIsCompleted(progress.getIsCompleted());
        response.setPercentage(progress.getPercentage());
        response.setResumeFrom(progress.getLastPositionSeconds());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Data
    public static class VideoDto {
        private Long id;
        private String title;
        private String sourceUrl;
        private String hlsUrl;
        private Integer durationSeconds;
        private Boolean isFreePreview;
        private Long sectionId;
        private String sectionTitle;
        private Long courseId;
        private String courseTitle;

        public static VideoDto fromEntity(Video video) {
            VideoDto dto = new VideoDto();
            dto.setId(video.getId());
            dto.setTitle(video.getTitle());
            dto.setSourceUrl(video.getSourceUrl());
            dto.setHlsUrl(video.getHlsUrl());
            dto.setDurationSeconds(video.getDurationSeconds());
            dto.setIsFreePreview(video.getIsFreePreview());
            dto.setSectionId(video.getSection().getId());
            dto.setSectionTitle(video.getSection().getTitle());
            dto.setCourseId(video.getSection().getCourse().getId());
            dto.setCourseTitle(video.getSection().getCourse().getTitle());
            return dto;
        }
    }

    @Data
    public static class ProgressDto {
        private Integer lastPositionSeconds;
        private Integer maxPositionSeconds;
        private BigDecimal percentage;
        private Boolean isCompleted;
        private BigDecimal playbackRate;
    }

    @Data
    public static class ProgressResponseDto {
        private Boolean isCompleted;
        private BigDecimal percentage;
        private Integer resumeFrom;
    }
}