package com.why.taskmanager.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.taskmanager.entity.Task;
import com.why.taskmanager.entity.Users;
import com.why.taskmanager.mapper.TaskMapper;
import com.why.taskmanager.mapper.UserMapper;
import com.why.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void createTask(Task task, String username) {
        Users user = userMapper.findByUsername(username);
        task.setUserId(user.getId());
        taskMapper.insert(task);
    }

    @Override
    public IPage<Task> getTasks(String username, int page, int size, String status, String keyword) {
        Users user = userMapper.findByUsername(username);
        Page<Task> pageObj = new Page<>(page, size);
        return taskMapper.selectTasksByUserId(pageObj, user.getId(), status, keyword);
    }

    @Override
    public void updateTask(Long id, Task task, String username) {
        Users user = userMapper.findByUsername(username);
        Task existingTask = taskMapper.selectById(id);
        if (existingTask != null && existingTask.getUserId().equals(user.getId())) {
            task.setId(id);
            task.setUserId(user.getId());
            taskMapper.updateById(task);
        }
    }

    @Override
    public void deleteTask(Long id, String username) {
        Users user = userMapper.findByUsername(username);
        Task task = taskMapper.selectById(id);
        if (task != null && task.getUserId().equals(user.getId())) {
            task.setIsDeleted(true);
            taskMapper.updateById(task);
        }
    }
}
