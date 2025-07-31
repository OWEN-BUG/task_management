package com.why.taskmanager.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.taskmanager.entity.TaskComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<TaskComment> {
}