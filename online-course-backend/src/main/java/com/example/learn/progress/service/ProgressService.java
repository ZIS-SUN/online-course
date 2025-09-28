package com.example.learn.progress.service;

import com.example.learn.course.entity.Video;
import com.example.learn.course.repository.VideoRepository;
import com.example.learn.progress.entity.PlayHistory;
import com.example.learn.progress.entity.VideoProgress;
import com.example.learn.progress.repository.PlayHistoryRepository;
import com.example.learn.progress.repository.VideoProgressRepository;
import com.example.learn.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final VideoProgressRepository videoProgressRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final VideoRepository videoRepository;

    public VideoProgress getVideoProgress(Long userId, Long videoId) {
        return videoProgressRepository.findByUserIdAndVideoId(userId, videoId)
                .orElseGet(() -> {
                    VideoProgress progress = new VideoProgress();
                    progress.setLastPositionSeconds(0);
                    progress.setMaxPositionSeconds(0);
                    progress.setPercentage(BigDecimal.ZERO);
                    progress.setIsCompleted(false);
                    return progress;
                });
    }

    @Transactional
    public VideoProgress updateVideoProgress(User user, Long videoId, UpdateProgressRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        VideoProgress progress = videoProgressRepository
                .findByUserIdAndVideoId(user.getId(), videoId)
                .orElseGet(() -> {
                    VideoProgress newProgress = new VideoProgress();
                    newProgress.setUser(user);
                    newProgress.setVideo(video);
                    return newProgress;
                });

        // Update position
        progress.setLastPositionSeconds(request.getPositionSeconds());

        // Update max position (simple anti-skip strategy)
        if (request.getMaxPositionSeconds() != null) {
            progress.setMaxPositionSeconds(
                    Math.max(progress.getMaxPositionSeconds(), request.getMaxPositionSeconds())
            );
        } else {
            progress.setMaxPositionSeconds(
                    Math.max(progress.getMaxPositionSeconds(), request.getPositionSeconds())
            );
        }

        // Update playback rate
        if (request.getPlaybackRate() != null) {
            progress.setPlaybackRate(request.getPlaybackRate());
        }

        // Calculate percentage
        int duration = request.getDurationSeconds() != null ?
                request.getDurationSeconds() : video.getDurationSeconds();
        if (duration > 0) {
            BigDecimal percentage = BigDecimal.valueOf(progress.getMaxPositionSeconds())
                    .divide(BigDecimal.valueOf(duration), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            progress.setPercentage(percentage.min(BigDecimal.valueOf(100)));
        }

        // Check completion
        if ("end".equals(request.getEvent()) ||
            (duration > 0 && progress.getLastPositionSeconds() >= duration * 0.9)) {
            progress.setIsCompleted(true);
            progress.setPercentage(BigDecimal.valueOf(100));
        }

        // Save progress
        progress = videoProgressRepository.save(progress);

        // Record play history
        recordPlayHistory(user, video, request.getPositionSeconds());

        return progress;
    }

    private void recordPlayHistory(User user, Video video, Integer positionSeconds) {
        PlayHistory history = new PlayHistory();
        history.setUser(user);
        history.setVideo(video);
        history.setPositionSeconds(positionSeconds);
        playHistoryRepository.save(history);
    }

    public BigDecimal getCourseProgress(Long userId, Long courseId) {
        var progresses = videoProgressRepository.findByUserIdAndCourseId(userId, courseId);

        if (progresses.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long completedCount = progresses.stream()
                .filter(VideoProgress::getIsCompleted)
                .count();

        return BigDecimal.valueOf(completedCount)
                .divide(BigDecimal.valueOf(progresses.size()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static class UpdateProgressRequest {
        private Integer positionSeconds;
        private Integer durationSeconds;
        private String event;
        private Integer maxPositionSeconds;
        private BigDecimal playbackRate;
        private String device;
        private String clientTime;

        // Getters and setters
        public Integer getPositionSeconds() { return positionSeconds; }
        public void setPositionSeconds(Integer positionSeconds) { this.positionSeconds = positionSeconds; }

        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }

        public Integer getMaxPositionSeconds() { return maxPositionSeconds; }
        public void setMaxPositionSeconds(Integer maxPositionSeconds) { this.maxPositionSeconds = maxPositionSeconds; }

        public BigDecimal getPlaybackRate() { return playbackRate; }
        public void setPlaybackRate(BigDecimal playbackRate) { this.playbackRate = playbackRate; }

        public String getDevice() { return device; }
        public void setDevice(String device) { this.device = device; }

        public String getClientTime() { return clientTime; }
        public void setClientTime(String clientTime) { this.clientTime = clientTime; }
    }
}