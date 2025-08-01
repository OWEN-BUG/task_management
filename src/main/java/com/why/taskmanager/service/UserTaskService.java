package com.why.taskmanager.service;


import com.why.taskmanager.DTO.UserScoreVO;
import com.why.taskmanager.entity.UserTask;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserTaskService {
    void assignTask(Long taskId, Long userId);
    void updateStatus(Long userTaskId, String status, String token);
    void updateScore(Long userTaskId, Float score);
    List<UserScoreVO> calculateMonthlyScores(String month);
    List<UserTask> getUserTasks(Long userId);
    UserTask findByTaskAndUser(Long taskId, Long userId);

    @Transactional
    void deleteUserTask(Long userTaskId);
}