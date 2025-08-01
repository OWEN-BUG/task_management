package com.why.taskmanager.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.why.taskmanager.entity.TaskComment;
import com.why.taskmanager.mapper.CommentMapper;
import com.why.taskmanager.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskCommentServiceImpl implements TaskCommentService {
    @Autowired
    private CommentMapper taskCommentMapper;

    @Override
    public void addComment(TaskComment c) {
        c.setCreatedTime(LocalDateTime.now());
        taskCommentMapper.insert(c);
    }

    @Override
    public List<TaskComment> getCommentsByTaskId(Long taskId) {
        return taskCommentMapper.selectList(
                new QueryWrapper<TaskComment>().eq("task_id", taskId).orderByDesc("created_time")
        );
    }
}