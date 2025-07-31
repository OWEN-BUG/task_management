package com.why.taskmanager.controller;


import com.why.taskmanager.entity.Users;
import com.why.taskmanager.security.JwtTokenProvider;
import com.why.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

            // 确保只接受必要字段，忽略客户端发送的时间
            user.setCreatedTime(null);
            user.setUpdatedTime(null);

            // 由服务器设置时间
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedTime(now);
            user.setUpdatedTime(now);

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setIsDeleted(false);

            // 确保只保存必要字段
            Users newUser = new Users();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(user.getPassword());
            newUser.setIsDeleted(user.getIsDeleted());
            newUser.setCreatedTime(user.getCreatedTime());
            newUser.setUpdatedTime(user.getUpdatedTime());

            userService.save(newUser);
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
//            logger.error("注册失败: {}", e.getMessage(), e);
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

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request) {

        String username = getUsernameFromToken(token);
        Users user = userService.findByUsername(username);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedTime(LocalDateTime.now());
        userService.updateUser(user);
        return ResponseEntity.ok("密码修改成功");
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
