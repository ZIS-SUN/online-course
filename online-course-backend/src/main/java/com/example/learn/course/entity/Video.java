package com.example.learn.course.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonBackReference
    private Section section;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(name = "hls_url")
    private String hlsUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds = 0;

    @Column(name = "size_bytes")
    private Long sizeBytes = 0L;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_free_preview")
    private Boolean isFreePreview = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "transcode_status")
    private TranscodeStatus transcodeStatus = TranscodeStatus.ORIGINAL;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TranscodeStatus {
        ORIGINAL, TRANSCODING, HLS_READY, FAILED
    }
}