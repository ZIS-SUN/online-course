package com.example.learn.course.service;

import com.example.learn.course.entity.Course;
import com.example.learn.course.entity.Enrollment;
import com.example.learn.course.entity.Section;
import com.example.learn.course.repository.CourseRepository;
import com.example.learn.course.repository.EnrollmentRepository;
import com.example.learn.course.repository.SectionRepository;
import com.example.learn.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SectionRepository sectionRepository;

    public Page<Course> getPublishedCourses(String keyword, Long categoryId, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return courseRepository.findByStatusAndKeyword(Course.CourseStatus.PUBLISHED, keyword, pageable);
        } else if (categoryId != null) {
            return courseRepository.findByStatusAndCategoryId(Course.CourseStatus.PUBLISHED, categoryId, pageable);
        } else {
            return courseRepository.findByStatus(Course.CourseStatus.PUBLISHED, pageable);
        }
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public List<Section> getCourseSections(Long courseId) {
        return sectionRepository.findByCourseIdOrderBySortOrder(courseId);
    }

    @Transactional
    public Enrollment enrollCourse(User user, Long courseId) {
        Course course = getCourseById(courseId);

        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            return enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId).get();
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);

        return enrollmentRepository.save(enrollment);
    }

    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    public Page<Enrollment> getUserEnrollments(Long userId, Pageable pageable) {
        return enrollmentRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public Course createCourse(Course course, User creator) {
        course.setCreatedBy(creator);
        course.setStatus(Course.CourseStatus.DRAFT);
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Course updates) {
        Course course = getCourseById(id);
        course.setTitle(updates.getTitle());
        course.setSubtitle(updates.getSubtitle());
        course.setDescription(updates.getDescription());
        course.setCoverUrl(updates.getCoverUrl());
        course.setCategory(updates.getCategory());
        course.setLevel(updates.getLevel());
        course.setIsFree(updates.getIsFree());
        return courseRepository.save(course);
    }

    @Transactional
    public Course publishCourse(Long id) {
        Course course = getCourseById(id);
        course.setStatus(Course.CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }

    @Transactional
    public Course archiveCourse(Long id) {
        Course course = getCourseById(id);
        course.setStatus(Course.CourseStatus.ARCHIVED);
        return courseRepository.save(course);
    }

    public long getCourseEnrollmentCount(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }
}