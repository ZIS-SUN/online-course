package com.example.learn.admin.controller;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.user.entity.User;
import com.example.learn.user.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @PathVariable Long id,
            @RequestBody PasswordResetDto dto
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDto dto
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(dto.getStatus());
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(updatedUser));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateDto dto
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(User.UserRole.valueOf(dto.getRole()));
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        // Prevent deleting admin users
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == User.UserRole.ADMIN) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot delete admin user"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Data
    public static class PasswordResetDto {
        private String newPassword;
    }

    @Data
    public static class StatusUpdateDto {
        private Integer status;
    }

    @Data
    public static class RoleUpdateDto {
        private String role;
    }
}