package com.example.learn.course.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.course.entity.Category;
import com.example.learn.course.repository.CategoryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        List<Category> categories = categoryRepository.findByEnabledTrueOrderBySortOrder();
        List<CategoryDto> categoryDtos = categories.stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(categoryDtos));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> getCategoryTree() {
        List<Category> allCategories = categoryRepository.findByEnabledTrueOrderBySortOrder();
        List<CategoryTreeDto> tree = buildCategoryTree(allCategories);
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    private List<CategoryTreeDto> buildCategoryTree(List<Category> categories) {
        Map<Long, List<Category>> childrenMap = categories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));

        return categories.stream()
                .filter(c -> c.getParentId() == null)
                .map(parent -> {
                    CategoryTreeDto dto = CategoryTreeDto.fromEntity(parent);
                    dto.setChildren(buildChildren(parent.getId(), childrenMap));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<CategoryTreeDto> buildChildren(Long parentId, Map<Long, List<Category>> childrenMap) {
        List<Category> children = childrenMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }

        return children.stream()
                .map(child -> {
                    CategoryTreeDto dto = CategoryTreeDto.fromEntity(child);
                    dto.setChildren(buildChildren(child.getId(), childrenMap));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Data
    public static class CategoryDto {
        private Long id;
        private String name;
        private Long parentId;
        private String description;
        private String icon;
        private Integer sortOrder;
        private Boolean enabled;
        private Integer courseCount;

        public static CategoryDto fromEntity(Category category) {
            CategoryDto dto = new CategoryDto();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setParentId(category.getParentId());
            dto.setDescription(category.getDescription());
            dto.setIcon(category.getIcon());
            dto.setSortOrder(category.getSortOrder());
            dto.setEnabled(category.getEnabled());
            dto.setCourseCount(category.getCourseCount());
            return dto;
        }
    }

    @Data
    public static class CategoryTreeDto {
        private Long id;
        private String name;
        private Long parentId;
        private String description;
        private String icon;
        private Integer sortOrder;
        private Boolean enabled;
        private Integer courseCount;
        private List<CategoryTreeDto> children = new ArrayList<>();

        public static CategoryTreeDto fromEntity(Category category) {
            CategoryTreeDto dto = new CategoryTreeDto();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setParentId(category.getParentId());
            dto.setDescription(category.getDescription());
            dto.setIcon(category.getIcon());
            dto.setSortOrder(category.getSortOrder());
            dto.setEnabled(category.getEnabled());
            dto.setCourseCount(category.getCourseCount());
            return dto;
        }
    }
}