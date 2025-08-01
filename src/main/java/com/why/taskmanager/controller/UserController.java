package com.why.taskmanager.controller;


import com.why.taskmanager.entity.Users;
import com.why.taskmanager.security.JwtTokenProvider;
import com.why.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {
        try {
            Users existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null && !existingUser.getIsDeleted()) {
                return ResponseEntity.badRequest().body("用户名已存在");
            }

            user.setCreatedTime(null);
            user.setUpdatedTime(null);

            LocalDateTime now = LocalDateTime.now();
            user.setCreatedTime(now);
            user.setUpdatedTime(now);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setIsDeleted(false);
            user.setRole(9); // 默认普通用户

            Users newUser = new Users();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(user.getPassword());
            newUser.setIsDeleted(user.getIsDeleted());
            newUser.setCreatedTime(user.getCreatedTime());
            newUser.setUpdatedTime(user.getUpdatedTime());
            newUser.setRole(user.getRole());

            userService.save(newUser);
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("注册失败: " + e.getMessage());
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        Users existingUser = userService.findByUsername(user.getUsername());
        if (existingUser == null || existingUser.getIsDeleted() || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        Users user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "获取所有用户（管理员）")
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(@RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        // 只有管理员可以查看所有用户
        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(summary = "获取普通用户列表（管理员）")
    @GetMapping("/normal")
    public ResponseEntity<List<Users>> getNormalUsers(@RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        // 只有管理员可以查看
        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.findNormalUsers());
    }

    @Operation(summary = "更新用户角色（管理员）")
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        // 只有管理员可以更新角色
        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限操作");
        }

        Integer role = request.get("role");
        if (role == null || (role != 0 && role != 9)) {
            return ResponseEntity.badRequest().body("无效的角色值");
        }

        Users user = userService.findByUsername(username);
        user.setRole(role);
        userService.updateUser(user);
        return ResponseEntity.ok("用户角色更新成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        Users user = userService.findByUsername(username);
        if (user.getId().equals(id)) {
            userService.deleteUser(id);
            return ResponseEntity.ok("用户删除成功");
        }
        return ResponseEntity.badRequest().body("无权限删除");
    }


    @Operation(summary = "更新当前用户信息（密码）")
    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@RequestHeader("Authorization") String token,
                                      @RequestBody Map<String, String> body) {
        String username = getUsernameFromToken(token);
        Users user = userService.findByUsername(username);

        String newPassword = body.get("password");
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedTime(LocalDateTime.now());
            userService.updateUser(user);
            return ResponseEntity.ok("密码更新成功");
        }
        return ResponseEntity.badRequest().body("密码不能为空");
    }

    @Data
    static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }

    private String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
    }
}
