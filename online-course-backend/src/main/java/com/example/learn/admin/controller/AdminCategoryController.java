package com.example.learn.admin.controller;

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
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAllByOrderBySortOrder();
        List<CategoryTreeDto> tree = buildCategoryTree(allCategories);
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder());
        category.setEnabled(request.getEnabled());
        category.setCourseCount(0);

        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(ApiResponse.success(CategoryDto.fromEntity(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder());
        category.setEnabled(request.getEnabled());

        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(ApiResponse.success(CategoryDto.fromEntity(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Check if category has children
        List<Category> children = categoryRepository.findByParentIdOrderBySortOrder(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("Cannot delete category with children");
        }

        // Check if category has courses (assuming courseCount > 0)
        if (category.getCourseCount() > 0) {
            throw new RuntimeException("Cannot delete category with courses");
        }

        categoryRepository.delete(category);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleCategoryStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setEnabled(request.getEnabled());
        categoryRepository.save(category);

        return ResponseEntity.ok(ApiResponse.success(null));
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
    public static class CreateCategoryRequest {
        private String name;
        private Long parentId;
        private String description;
        private String icon;
        private Integer sortOrder;
        private Boolean enabled;
    }

    @Data
    public static class UpdateCategoryRequest {
        private String name;
        private Long parentId;
        private String description;
        private String icon;
        private Integer sortOrder;
        private Boolean enabled;
    }

    @Data
    public static class StatusUpdateRequest {
        private Boolean enabled;
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