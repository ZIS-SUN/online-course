package com.example.learn.progress.entity;

import com.example.learn.course.entity.Video;
import com.example.learn.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "play_history")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PlayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "position_seconds")
    private Integer positionSeconds = 0;

    @CreatedDate
    @Column(name = "watched_at", nullable = false, updatable = false)
    private LocalDateTime watchedAt;
}