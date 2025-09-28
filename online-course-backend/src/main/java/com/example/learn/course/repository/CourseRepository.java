package com.example.learn.course.repository;

import com.example.learn.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByStatus(Course.CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.status = :status AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.subtitle) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> findByStatusAndKeyword(@Param("status") Course.CourseStatus status,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    Page<Course> findByStatusAndCategoryId(Course.CourseStatus status, Long categoryId, Pageable pageable);

    List<Course> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    Page<Course> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.sections ORDER BY c.createdAt DESC")
    List<Course> findAllWithSections();

    @Query(value = "SELECT c FROM Course c LEFT JOIN FETCH c.sections",
           countQuery = "SELECT count(c) FROM Course c")
    Page<Course> findAllWithSections(Pageable pageable);
}