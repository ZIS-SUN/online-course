package com.example.learn.announcement.repository;

import com.example.learn.announcement.entity.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, Long> {
    Optional<AnnouncementRead> findByAnnouncementIdAndUserId(Long announcementId, Long userId);
    boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId);
}