package com.why.taskmanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.taskmanager.DTO.UserTaskVO;
import com.why.taskmanager.entity.*;
import com.why.taskmanager.mapper.TaskMapper;
import com.why.taskmanager.mapper.UserMapper;
import com.why.taskmanager.mapper.UserTaskMapper;
import com.why.taskmanager.service.TaskService;
import com.why.taskmanager.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTaskMapper userTaskMapper;
    @Autowired
    private UserTaskService userTaskService;

    // 创建任务（管理员）
    @Override
    public void createTask(Task task, String username) {
        task.setCreatedTime(LocalDateTime.now());
        task.setUpdatedTime(LocalDateTime.now());
        task.setIsDeleted(false);
        taskMapper.insert(task);
    }

    // 获取用户任务列表
    @Override
    public IPage<UserTaskVO> getUserTasks(String username, int page, int size, String status, String keyword) {
        Users user = userMapper.findByUsername(username);
        Page<UserTaskVO> p = new Page<>(page, size);
        return taskMapper.selectTasksByUserId(p, user.getId(), status, keyword);
    }

    // 删除用户任务（逻辑删除）
    @Override
    public void deleteUserTask(Long userTaskId, String username) {
        Users user = userMapper.findByUsername(username);
        UserTask userTask = userTaskMapper.selectById(userTaskId);

        // 检查权限：用户只能删除自己的任务
        if (userTask != null && userTask.getUserId().equals(user.getId())) {
            userTaskService.deleteUserTask(userTaskId);
        } else {
            throw new SecurityException("无权删除此任务");
        }
    }
}