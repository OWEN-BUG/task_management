package com.why.taskmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.why.taskmanager.DTO.UserTaskVO;
import com.why.taskmanager.entity.Task;

import java.util.List;

public interface TaskService {
    void createTask(Task task, String username);
    IPage<UserTaskVO> getUserTasks(String username, int page, int size, String status, String keyword);
    void deleteUserTask(Long userTaskId, String username);
}