package com.why.taskmanager.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.why.taskmanager.entity.SubTask;
import com.why.taskmanager.mapper.SubTaskMapper;
import com.why.taskmanager.service.SubTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubTaskServiceImpl implements SubTaskService {
    @Autowired
    private SubTaskMapper subTaskMapper;

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setIsDeleted(false);
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

    public List<SubTask> getSubTasksByTaskId(Long taskId) {
        return subTaskMapper.selectList(
                new QueryWrapper<SubTask>().eq("task_id", taskId).eq("is_deleted", 0)
        );
    }
}
