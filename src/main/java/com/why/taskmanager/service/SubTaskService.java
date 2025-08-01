package com.why.taskmanager.service;


import com.why.taskmanager.entity.SubTask;

import java.util.List;

public interface SubTaskService {
    void addSubTask(SubTask subTask);
    void deleteSubTask(Long subTaskId);
    void updateSubTask(Long subTaskId, String title, String status);
    List<SubTask> getSubTasksByTaskId(Long taskId);
}
