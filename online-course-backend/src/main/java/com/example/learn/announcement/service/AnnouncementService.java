package com.example.learn.announcement.service;

import com.example.learn.announcement.entity.Announcement;
import com.example.learn.announcement.entity.AnnouncementRead;
import com.example.learn.announcement.repository.AnnouncementReadRepository;
import com.example.learn.announcement.repository.AnnouncementRepository;
import com.example.learn.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementReadRepository announcementReadRepository;

    public Page<Announcement> getActiveAnnouncements(Pageable pageable) {
        return announcementRepository.findActiveAnnouncements(LocalDateTime.now(), pageable);
    }

    public List<Announcement> getPinnedAnnouncements() {
        return announcementRepository.findPinnedAnnouncements(LocalDateTime.now());
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
    }

    @Transactional
    public Announcement viewAnnouncement(Long id, User user) {
        Announcement announcement = getAnnouncementById(id);

        // 增加浏览次数
        announcement.setViewCount(announcement.getViewCount() + 1);
        announcementRepository.save(announcement);

        // 记录已读
        if (user != null && !announcementReadRepository.existsByAnnouncementIdAndUserId(id, user.getId())) {
            AnnouncementRead read = new AnnouncementRead();
            read.setAnnouncement(announcement);
            read.setUser(user);
            announcementReadRepository.save(read);
        }

        return announcement;
    }

    public long getUnreadCount(Long userId) {
        return announcementRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public Announcement createAnnouncement(Announcement announcement, User author) {
        announcement.setAuthor(author);
        announcement.setStatus(Announcement.AnnouncementStatus.DRAFT);
        announcement.setViewCount(0);
        return announcementRepository.save(announcement);
    }

    @Transactional
    public Announcement updateAnnouncement(Long id, Announcement updates) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setTitle(updates.getTitle());
        announcement.setContent(updates.getContent());
        announcement.setType(updates.getType());
        announcement.setPriority(updates.getPriority());
        announcement.setIsPinned(updates.getIsPinned());
        announcement.setExpireTime(updates.getExpireTime());
        return announcementRepository.save(announcement);
    }

    @Transactional
    public Announcement publishAnnouncement(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setStatus(Announcement.AnnouncementStatus.PUBLISHED);
        announcement.setPublishTime(LocalDateTime.now());
        return announcementRepository.save(announcement);
    }

    @Transactional
    public Announcement archiveAnnouncement(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setStatus(Announcement.AnnouncementStatus.ARCHIVED);
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    public Page<Announcement> getAllAnnouncements(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }
}