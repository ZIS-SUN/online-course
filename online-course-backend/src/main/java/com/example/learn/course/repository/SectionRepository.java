package com.example.learn.course.repository;

import com.example.learn.course.entity.Course;
import com.example.learn.course.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourseIdOrderBySortOrder(Long courseId);
    List<Section> findByCourseOrderBySortOrder(Course course);
    void deleteByIdAndCourseId(Long id, Long courseId);
    long countByCourse(Course course);
    long countByCourseId(Long courseId);
}