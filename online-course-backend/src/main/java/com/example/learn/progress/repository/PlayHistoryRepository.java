package com.example.learn.progress.repository;

import com.example.learn.progress.entity.PlayHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    Page<PlayHistory> findByUserIdOrderByWatchedAtDesc(Long userId, Pageable pageable);
}