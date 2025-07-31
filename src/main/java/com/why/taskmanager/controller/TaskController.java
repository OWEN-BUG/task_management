package com.why.taskmanager.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.why.taskmanager.entity.TaskComment;
import com.why.taskmanager.entity.SubTask;
import com.why.taskmanager.entity.Task;
import com.why.taskmanager.entity.TaskTag;
import com.why.taskmanager.security.JwtTokenProvider;
import com.why.taskmanager.service.SubTaskService;
import com.why.taskmanager.service.TaskCommentService;
import com.why.taskmanager.service.TaskService;
import com.why.taskmanager.service.TaskTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "任务管理")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTagService taskTagService;
    @Autowired
    private TaskCommentService taskCommentService;
    @Autowired
    private SubTaskService subTaskService;
    @Autowired
    private JwtTokenProvider jwtUtil;

    @Operation(summary = "创建任务")
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        taskService.createTask(task, username);
        return ResponseEntity.ok("任务创建成功");
    }

    @Operation(summary = "查询任务")
    @GetMapping
    public ResponseEntity<IPage<Task>> getTasks(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(taskService.getTasks(username, page, size, status, keyword));
    }

    @Operation(summary = "更新任务")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        taskService.updateTask(id, task, username);
        return ResponseEntity.ok("任务更新成功");
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        taskService.deleteTask(id, username);
        return ResponseEntity.ok("任务删除成功");
    }

    @Operation(summary = "添加任务标签")
    @PostMapping("/{id}/tags")
    public ResponseEntity<?> addTag(@PathVariable Long id, @RequestBody TaskTag tag, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        tag.setTaskId(id);
        taskTagService.addTag(tag);
        return ResponseEntity.ok("标签添加成功");
    }

    @Operation(summary = "添加任务评论")
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody TaskComment taskComment, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        taskComment.setTaskId(id);
        taskComment.setUserId((long) jwtUtil.getUsernameFromToken(token.replace("Bearer ", "")).hashCode());
        taskCommentService.addComment(taskComment);
        return ResponseEntity.ok("评论添加成功");
    }

    @Operation(summary = "添加子任务")
    @PostMapping("/{id}/subtasks")
    public ResponseEntity<?> addSubTask(@PathVariable Long id, @RequestBody SubTask subTask, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        subTask.setTaskId(id);
        subTaskService.addSubTask(subTask);
        return ResponseEntity.ok("子任务添加成功");
    }

    @Operation(summary = "删除子任务")
    @DeleteMapping("/{id}/subtasks/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(@PathVariable Long id, @PathVariable Long subTaskId, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        subTaskService.deleteSubTask(subTaskId);
        return ResponseEntity.ok("子任务删除成功");
    }

    @Operation(summary = "更新任务标签")
    @PutMapping("/{id}/tags/{tagId}")
    public ResponseEntity<?> updateTag(
            @PathVariable Long id,
            @PathVariable Long tagId,
            @RequestBody TaskTag tag,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        taskTagService.updateTag(tagId, tag.getName());
        return ResponseEntity.ok("标签更新成功");
    }

    @Operation(summary = "更新子任务")
    @PutMapping("/{id}/subtasks/{subTaskId}")
    public ResponseEntity<?> updateSubTask(
            @PathVariable Long id,
            @PathVariable Long subTaskId,
            @RequestBody SubTask subTask,
            @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);
        subTaskService.updateSubTask(subTaskId, subTask.getTitle(), subTask.getStatus());
        return ResponseEntity.ok("子任务更新成功");
    }

    private String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
    }
}
