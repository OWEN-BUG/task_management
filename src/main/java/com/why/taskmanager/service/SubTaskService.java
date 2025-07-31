package com.why.taskmanager.service;


import com.why.taskmanager.entity.SubTask;

public interface SubTaskService {
    void addSubTask(SubTask subTask);
    void deleteSubTask(Long subTaskId);
    void updateSubTask(Long subTaskId, String title, String status);
}
