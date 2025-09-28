package com.example.learn.announcement.controller;

import com.example.learn.announcement.entity.Announcement;
import com.example.learn.announcement.service.AnnouncementService;
import com.example.learn.common.dto.ApiResponse;
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

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AnnouncementDto>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Announcement> announcements = announcementService.getActiveAnnouncements(pageable);
        Page<AnnouncementDto> dtos = announcements.map(AnnouncementDto::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/pinned")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getPinnedAnnouncements() {
        List<Announcement> announcements = announcementService.getPinnedAnnouncements();
        List<AnnouncementDto> dtos = announcements.stream()
                .map(AnnouncementDto::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementDetailDto>> getAnnouncement(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = authentication != null ? (User) authentication.getPrincipal() : null;
        Announcement announcement = announcementService.viewAnnouncement(id, user);
        AnnouncementDetailDto dto = AnnouncementDetailDto.fromEntity(announcement);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(ApiResponse.success(0L));
        }
        User user = (User) authentication.getPrincipal();
        long count = announcementService.getUnreadCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @Data
    public static class AnnouncementDto {
        private Long id;
        private String title;
        private String type;
        private String priority;
        private Boolean isPinned;
        private Integer viewCount;
        private LocalDateTime publishTime;
        private String authorName;

        public static AnnouncementDto fromEntity(Announcement announcement) {
            AnnouncementDto dto = new AnnouncementDto();
            dto.setId(announcement.getId());
            dto.setTitle(announcement.getTitle());
            dto.setType(announcement.getType().name());
            dto.setPriority(announcement.getPriority().name());
            dto.setIsPinned(announcement.getIsPinned());
            dto.setViewCount(announcement.getViewCount());
            dto.setPublishTime(announcement.getPublishTime());
            dto.setAuthorName(announcement.getAuthor().getNickname());
            return dto;
        }
    }

    @Data
    public static class AnnouncementDetailDto extends AnnouncementDto {
        private String content;
        private LocalDateTime expireTime;

        public static AnnouncementDetailDto fromEntity(Announcement announcement) {
            AnnouncementDetailDto dto = new AnnouncementDetailDto();
            dto.setId(announcement.getId());
            dto.setTitle(announcement.getTitle());
            dto.setContent(announcement.getContent());
            dto.setType(announcement.getType().name());
            dto.setPriority(announcement.getPriority().name());
            dto.setIsPinned(announcement.getIsPinned());
            dto.setViewCount(announcement.getViewCount());
            dto.setPublishTime(announcement.getPublishTime());
            dto.setExpireTime(announcement.getExpireTime());
            dto.setAuthorName(announcement.getAuthor().getNickname());
            return dto;
        }
    }
}