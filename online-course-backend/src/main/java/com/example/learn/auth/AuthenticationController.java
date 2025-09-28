package com.example.learn.auth;

import com.example.learn.common.dto.ApiResponse;
import com.example.learn.user.entity.User;
import com.example.learn.user.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名已存在"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.USER);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);
        response.setUser(UserDto.fromEntity(savedUser));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(token);
            response.setUser(UserDto.fromEntity(user));

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名或密码错误"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(UserDto.fromEntity(user)));
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        private String nickname;
        private String email;
        private String phone;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class AuthenticationResponse {
        private String token;
        private UserDto user;
    }

    @Data
    public static class UserDto {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String phone;
        private String role;
        private String avatarUrl;

        public static UserDto fromEntity(User user) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setRole(user.getRole().name());
            dto.setAvatarUrl(user.getAvatarUrl());
            return dto;
        }
    }
}