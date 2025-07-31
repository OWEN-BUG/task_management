package com.why.taskmanager.service;


import com.why.taskmanager.entity.TaskTag;

public interface TaskTagService {
    void addTag(TaskTag tag);
    void deleteTag(Long tagId);
    void updateTag(Long tagId, String newName);
}
