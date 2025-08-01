package com.why.taskmanager.service.impl;

import com.why.taskmanager.DTO.UserScoreVO;
import com.why.taskmanager.entity.*;
import com.why.taskmanager.mapper.NotificationMapper;
import com.why.taskmanager.mapper.TaskMapper;
import com.why.taskmanager.mapper.UserMapper;
import com.why.taskmanager.mapper.UserTaskMapper;
import com.why.taskmanager.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserTaskServiceImpl implements UserTaskService {

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    TaskMapper taskMapper;

    @Override
    public void assignTask(Long taskId, Long userId) {
        UserTask userTask = new UserTask();
        userTask.setTaskId(taskId);
        userTask.setUserId(userId);
        userTask.setStatus("PENDING");
        userTask.setScore(0.0f);
        userTask.setCreatedTime(LocalDateTime.now());
        userTaskMapper.insert(userTask);
    }

    @Override
    @Transactional
    public void updateStatus(Long userTaskId, String status, String token) {
        UserTask userTask = userTaskMapper.selectById(userTaskId);
        String oldStatus = userTask.getStatus();
        userTask.setStatus(status);
        userTask.setUpdatedTime(LocalDateTime.now());

        // 状态变更为DONE时通知管理员
        if ("DONE".equals(status) && !"DONE".equals(oldStatus)) {
            userTask.setCompletedTime(LocalDateTime.now());

            // 创建通知
            Users user = userMapper.selectById(userTask.getUserId());
            Task task = taskMapper.selectById(userTask.getTaskId());

            Notification notification = new Notification();
            notification.setTaskId(userTask.getTaskId());
            notification.setUserId(userTask.getUserId());
            notification.setMessage(String.format("用户 %s 已完成任务: %s",
                    user.getUsername(), task.getTitle()));
            notification.setCreatedTime(LocalDateTime.now());
            notificationMapper.insert(notification);
        }

        userTaskMapper.updateById(userTask);
    }

    @Override
    public void updateScore(Long userTaskId, Float score) {
        UserTask userTask = userTaskMapper.selectById(userTaskId);
        userTask.setScore(score);
        userTaskMapper.updateById(userTask);
    }

    @Override
    public List<UserScoreVO> calculateMonthlyScores(String month) {
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return userTaskMapper.selectUserScoresByDateRange(start, end);
    }

    @Override
    public List<UserTask> getUserTasks(Long userId) {
        return userTaskMapper.findByUserId(userId);
    }

    @Override
    public UserTask findByTaskAndUser(Long taskId, Long userId) {
        return userTaskMapper.findByTaskAndUser(taskId, userId);
    }

        @Transactional
    @Override
    public void deleteUserTask(Long userTaskId) {
        UserTask userTask = userTaskMapper.selectById(userTaskId);
        if (userTask != null) {
            userTask.setIsDeleted(true);
            userTask.setUpdatedTime(LocalDateTime.now());
//            userTaskMapper.updateById(userTask);
            userTaskMapper.softDeleteById(userTaskId);
        }
    }
}