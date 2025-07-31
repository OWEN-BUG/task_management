package com.why.taskmanager.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.why.taskmanager.entity.Task;

public interface TaskService {
    void createTask(Task task, String username);
    IPage<Task> getTasks(String username, int page, int size, String status, String keyword);
    void updateTask(Long id, Task task, String username);
    void deleteTask(Long id, String username);
}