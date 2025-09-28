package com.example.learn.course.repository;

import com.example.learn.course.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIdOrderBySortOrder(Long parentId);
    List<Category> findByParentIdIsNullOrderBySortOrder();
    List<Category> findAllByOrderBySortOrder();
    List<Category> findByEnabledTrueOrderBySortOrder();
}