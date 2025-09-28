package com.example.learn.progress.repository;

import com.example.learn.progress.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    Optional<VideoProgress> findByUserIdAndVideoId(Long userId, Long videoId);

    @Query("SELECT vp FROM VideoProgress vp WHERE vp.user.id = :userId AND vp.video.section.course.id = :courseId")
    List<VideoProgress> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT COUNT(vp) FROM VideoProgress vp WHERE vp.video.section.course.id = :courseId AND vp.isCompleted = true")
    long countCompletedByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(DISTINCT vp.user.id) FROM VideoProgress vp WHERE vp.video.section.course.id = :courseId")
    long countDistinctUsersByCourseId(@Param("courseId") Long courseId);
}