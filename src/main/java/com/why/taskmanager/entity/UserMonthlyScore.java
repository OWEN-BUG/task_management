package com.why.taskmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_monthly_scores")
public class UserMonthlyScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer month; // 1~12
    private Integer year;  // 补充年份字段，避免跨年冲突
    private Float totalScore;
    private Integer completedCount;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}

