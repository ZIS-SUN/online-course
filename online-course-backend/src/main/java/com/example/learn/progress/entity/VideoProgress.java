package com.example.learn.progress.entity;

import com.example.learn.course.entity.Video;
import com.example.learn.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_progress")
@Data
@EntityListeners(AuditingEntityListener.class)
public class VideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "last_position_seconds", nullable = false)
    private Integer lastPositionSeconds = 0;

    @Column(name = "max_position_seconds", nullable = false)
    private Integer maxPositionSeconds = 0;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "playback_rate", nullable = false, precision = 3, scale = 2)
    private BigDecimal playbackRate = BigDecimal.ONE;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}