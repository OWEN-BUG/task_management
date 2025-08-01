package com.why.taskmanager.DTO;

import lombok.Data;

@Data
public class UserScoreVO {
    private Long userId;
    private String username;
    private Float totalScore;  // 用户总分数
    private Integer completedCount; // 完成任务数
}
