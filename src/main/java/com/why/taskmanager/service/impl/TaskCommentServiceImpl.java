package com.why.taskmanager.service.impl;


import com.why.taskmanager.entity.TaskComment;
import com.why.taskmanager.mapper.CommentMapper;
import com.why.taskmanager.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskCommentServiceImpl implements TaskCommentService {
    @Autowired
    private CommentMapper taskCommentMapper;

    @Override
    public void addComment(TaskComment taskComment) {
        taskCommentMapper.insert(taskComment);
    }
}