package com.example.learn.announcement.entity;

import com.example.learn.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private AnnouncementType type = AnnouncementType.SYSTEM;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @Enumerated(EnumType.STRING)
    private AnnouncementStatus status = AnnouncementStatus.DRAFT;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AnnouncementType {
        SYSTEM,      // 系统公告
        COURSE,      // 课程公告
        ACTIVITY,    // 活动公告
        MAINTENANCE  // 维护公告
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }

    public enum AnnouncementStatus {
        DRAFT,      // 草稿
        PUBLISHED,  // 已发布
        ARCHIVED    // 已归档
    }
}