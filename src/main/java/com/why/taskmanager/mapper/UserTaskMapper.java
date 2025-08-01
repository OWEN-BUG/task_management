package com.why.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.why.taskmanager.DTO.UserScoreVO;
import com.why.taskmanager.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {

    @Select("SELECT u.id AS userId, u.username, SUM(ut.score) AS totalScore " +
            "FROM user_tasks ut " +
            "JOIN users u ON ut.user_id = u.id " +
            "WHERE ut.status = 'DONE' " +
            "AND ut.completed_time BETWEEN #{start} AND #{end} " +
            "AND u.role = 9 " +  // 只统计普通用户
            "GROUP BY u.id, u.username")
    List<UserScoreVO> selectUserScoresByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Select("SELECT * FROM user_tasks WHERE task_id = #{taskId} AND user_id = #{userId} AND is_deleted = 0")
    UserTask findByTaskAndUser(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Select("SELECT * FROM user_tasks WHERE user_id = #{userId} AND is_deleted = 0")
    List<UserTask> findByUserId(Long userId);

    @Update("UPDATE user_tasks SET is_deleted = true WHERE id = #{userTaskId}")
    int softDeleteById(Long userTaskId);

    @Select("SELECT * FROM user_tasks WHERE task_id = #{taskId} AND is_deleted = 0")
    List<UserTask> findByTaskId(Long taskId);
}