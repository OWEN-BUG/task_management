package com.why.taskmanager.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserTaskVO {
    private Long id;
    private Long taskId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer priority;
    private String status;
    private Float score;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private LocalDateTime completedTime;

    // 关联信息
    private Integer commentCount;
    private Integer subTaskCount;
}