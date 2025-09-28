package com.example.learn.course.repository;

import com.example.learn.course.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Page<Enrollment> findByUserId(Long userId, Pageable pageable);
    Page<Enrollment> findByUserIdAndStatus(Long userId, Enrollment.EnrollmentStatus status, Pageable pageable);
    long countByCourseId(Long courseId);
}