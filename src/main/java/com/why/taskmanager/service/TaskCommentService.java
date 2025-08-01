package com.why.taskmanager.service;


import com.why.taskmanager.entity.TaskComment;

import java.util.List;

public interface TaskCommentService {
    void addComment(TaskComment taskComment);
    List<TaskComment> getCommentsByTaskId(Long taskId);
}
