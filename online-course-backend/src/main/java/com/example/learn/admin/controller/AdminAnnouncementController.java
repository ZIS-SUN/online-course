package com.example.learn.admin.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/announcements")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Announcement>>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Announcement> announcements = announcementService.getAllAnnouncements(pageable);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @Valid @RequestBody AnnouncementDto dto,
            Authentication authentication
    ) {
        User admin = (User) authentication.getPrincipal();

        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(Announcement.AnnouncementType.valueOf(dto.getType()));
        announcement.setPriority(Announcement.Priority.valueOf(dto.getPriority()));
        announcement.setIsPinned(dto.getIsPinned());
        announcement.setExpireTime(dto.getExpireTime());

        Announcement saved = announcementService.createAnnouncement(announcement, admin);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Announcement>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDto dto
    ) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(Announcement.AnnouncementType.valueOf(dto.getType()));
        announcement.setPriority(Announcement.Priority.valueOf(dto.getPriority()));
        announcement.setIsPinned(dto.getIsPinned());
        announcement.setExpireTime(dto.getExpireTime());

        Announcement updated = announcementService.updateAnnouncement(id, announcement);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<Announcement>> publishAnnouncement(@PathVariable Long id) {
        Announcement published = announcementService.publishAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(published));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<Announcement>> archiveAnnouncement(@PathVariable Long id) {
        Announcement archived = announcementService.archiveAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(archived));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Data
    public static class AnnouncementDto {
        @NotBlank(message = "标题不能为空")
        private String title;

        @NotBlank(message = "内容不能为空")
        private String content;

        private String type = "SYSTEM";
        private String priority = "NORMAL";
        private Boolean isPinned = false;
        private LocalDateTime expireTime;
    }
}