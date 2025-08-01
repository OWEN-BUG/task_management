package com.why.taskmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.why.taskmanager.DTO.UserScoreVO;
import com.why.taskmanager.DTO.UserTaskVO;
import com.why.taskmanager.entity.*;
import com.why.taskmanager.mapper.TaskMapper;
import com.why.taskmanager.mapper.UserTaskMapper;
import com.why.taskmanager.security.JwtTokenProvider;
import com.why.taskmanager.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "任务管理")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TaskCommentService taskCommentService;
    @Autowired
    private SubTaskService subTaskService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserTaskMapper userTaskMapper;
    @Autowired
    private JwtTokenProvider jwtUtil;

    @Operation(summary = "创建任务")
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);
        taskService.createTask(task, username);
        return ResponseEntity.ok("任务创建成功");
    }

    @Operation(summary = "获取用户任务列表")
    @GetMapping
    public ResponseEntity<IPage<UserTaskVO>> getUserTasks(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        String username = getUsernameFromToken(token);
        return ResponseEntity.ok(taskService.getUserTasks(username, page, size, status, keyword));
    }

    @Operation(summary = "分配任务给用户")
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<?> assignTask(
            @PathVariable Long taskId,
            @RequestBody UserTaskAssignment assignment,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限操作");
        }

        userTaskService.assignTask(taskId, assignment.getUserId());
        return ResponseEntity.ok("任务分配成功");
    }

    @Operation(summary = "更新任务评分")
    @PutMapping("/user-tasks/{userTaskId}/score")
    public ResponseEntity<?> updateScore(
            @PathVariable Long userTaskId,
            @RequestBody Map<String, Float> request,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限操作");
        }

        userTaskService.updateScore(userTaskId, request.get("score"));
        return ResponseEntity.ok("评分更新成功");
    }

    @Operation(summary = "更新用户任务状态")
    @PutMapping("/user-tasks/{userTaskId}/status")
    public ResponseEntity<?> updateUserTaskStatus(
            @PathVariable Long userTaskId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {

        String status = request.get("status");
        userTaskService.updateStatus(userTaskId, status, token);
        return ResponseEntity.ok("状态更新成功");
    }

    @Operation(summary = "删除用户任务")
    @DeleteMapping("/user-tasks/{userTaskId}")
    public ResponseEntity<?> deleteUserTask(
            @PathVariable Long userTaskId,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        taskService.deleteUserTask(userTaskId, username);
        return ResponseEntity.ok("任务删除成功");
    }

    @Operation(summary = "获取月度用户分数")
    @GetMapping("/stats/monthly-scores")
    public ResponseEntity<List<UserScoreVO>> getMonthlyScores(
            @RequestParam String month,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userTaskService.calculateMonthlyScores(month));
    }

    // 评论相关接口
    @Operation(summary = "添加任务评论")
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long taskId,
            @RequestBody TaskComment comment,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users user = userService.findByUsername(username);

        comment.setTaskId(taskId);
        comment.setUserId(user.getId());
        taskCommentService.addComment(comment);

        return ResponseEntity.ok("评论添加成功");
    }

    @Operation(summary = "获取任务评论")
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<TaskComment>> getComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskCommentService.getCommentsByTaskId(taskId));
    }

    // 子任务相关接口
    @Operation(summary = "添加子任务")
    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<?> addSubTask(
            @PathVariable Long taskId,
            @RequestBody SubTask subTask,
            @RequestHeader("Authorization") String token) {

        subTask.setTaskId(taskId);
        subTaskService.addSubTask(subTask);
        return ResponseEntity.ok("子任务添加成功");
    }

    @Operation(summary = "获取子任务")
    @GetMapping("/{taskId}/subtasks")
    public ResponseEntity<List<SubTask>> getSubTasks(@PathVariable Long taskId) {
        return ResponseEntity.ok(subTaskService.getSubTasksByTaskId(taskId));
    }

    @Operation(summary = "更新子任务")
    @PutMapping("/subtasks/{subTaskId}")
    public ResponseEntity<?> updateSubTask(
            @PathVariable Long subTaskId,
            @RequestBody SubTask subTask,
            @RequestHeader("Authorization") String token) {

        subTaskService.updateSubTask(subTaskId, subTask.getTitle(), subTask.getStatus());
        return ResponseEntity.ok("子任务更新成功");
    }

    @Operation(summary = "删除子任务")
    @DeleteMapping("/subtasks/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(
            @PathVariable Long subTaskId,
            @RequestHeader("Authorization") String token) {

        subTaskService.deleteSubTask(subTaskId);
        return ResponseEntity.ok("子任务删除成功");
    }

    // 通知相关接口
    @Operation(summary = "获取未读通知")
    @GetMapping("/notifications/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users user = userService.findByUsername(username);
        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getId()));
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        notificationService.markAsRead(id);
        return ResponseEntity.ok("通知已标记为已读");
    }

    @Operation(summary = "删除整个任务（管理员）")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteEntireTask(
            @PathVariable Long taskId,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        Users currentUser = userService.findByUsername(username);

        if (currentUser.getRole() != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限操作");
        }

        try {
            // 逻辑删除任务
            Task task = taskMapper.selectById(taskId);
            if (task != null) {
                task.setIsDeleted(true);
                taskMapper.updateById(task);
            }

            // 同时删除所有相关的用户任务
            List<UserTask> userTasks = userTaskMapper.findByTaskId(taskId);
            for (UserTask userTask : userTasks) {
                userTaskService.deleteUserTask(userTask.getId());
            }

            return ResponseEntity.ok("任务删除成功");
        } catch (Exception e) {
            log.error("删除整个任务失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除失败: " + e.getMessage());
        }
    }

    // 辅助类
    @Data
    static class UserTaskAssignment {
        private Long userId;
    }

    private String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
    }
}