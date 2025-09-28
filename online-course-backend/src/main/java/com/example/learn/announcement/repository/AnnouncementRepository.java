package com.example.learn.announcement.repository;

import com.example.learn.announcement.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.status = 'PUBLISHED' " +
           "AND (a.expireTime IS NULL OR a.expireTime > :now) " +
           "ORDER BY a.isPinned DESC, a.publishTime DESC")
    Page<Announcement> findActiveAnnouncements(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE a.status = 'PUBLISHED' " +
           "AND a.isPinned = true " +
           "AND (a.expireTime IS NULL OR a.expireTime > :now) " +
           "ORDER BY a.publishTime DESC")
    List<Announcement> findPinnedAnnouncements(@Param("now") LocalDateTime now);

    Page<Announcement> findByStatusOrderByCreatedAtDesc(Announcement.AnnouncementStatus status, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.status = 'PUBLISHED' " +
           "AND a.id NOT IN (SELECT ar.announcement.id FROM AnnouncementRead ar WHERE ar.user.id = :userId)")
    long countUnreadByUserId(@Param("userId") Long userId);
}