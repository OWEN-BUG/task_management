package com.why.taskmanager.service.impl;


import com.why.taskmanager.entity.SubTask;
import com.why.taskmanager.mapper.SubTaskMapper;
import com.why.taskmanager.service.SubTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubTaskServiceImpl implements SubTaskService {
    @Autowired
    private SubTaskMapper subTaskMapper;

    @Override
    public void addSubTask(SubTask subTask) {
        subTaskMapper.insert(subTask);
    }

    @Override
    public void deleteSubTask(Long subTaskId) {
        SubTask subTask = subTaskMapper.selectById(subTaskId);
        if (subTask != null) {
            subTask.setIsDeleted(true);
            subTaskMapper.updateById(subTask);
        }
    }

    @Override
    public void updateSubTask(Long subTaskId, String title, String status) {
        SubTask subTask = subTaskMapper.selectById(subTaskId);
        if (subTask != null) {
            subTask.setTitle(title);
            subTask.setStatus(status);
            subTaskMapper.updateById(subTask);
        }
    }
}
