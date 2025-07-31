package com.why.taskmanager.service.impl;


import com.why.taskmanager.entity.TaskTag;
import com.why.taskmanager.mapper.TaskTagMapper;
import com.why.taskmanager.service.TaskTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskTagServiceImpl implements TaskTagService {
    @Autowired
    private TaskTagMapper taskTagMapper;

    @Override
    public void addTag(TaskTag tag) {
        taskTagMapper.insert(tag);
    }

    @Override
    public void deleteTag(Long tagId) {
        taskTagMapper.deleteById(tagId);
    }

    @Override
    public void updateTag(Long tagId, String newName) {
        TaskTag tag = taskTagMapper.selectById(tagId);
        if (tag != null) {
            tag.setName(newName);
            taskTagMapper.updateById(tag);
        }
    }
}
