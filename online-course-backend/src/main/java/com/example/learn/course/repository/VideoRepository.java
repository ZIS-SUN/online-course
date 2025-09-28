package com.example.learn.course.repository;

import com.example.learn.course.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findBySectionIdOrderBySortOrder(Long sectionId);
    void deleteByIdAndSectionId(Long id, Long sectionId);
}