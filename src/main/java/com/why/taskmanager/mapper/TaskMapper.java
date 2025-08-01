package com.why.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.taskmanager.DTO.UserTaskVO;
import com.why.taskmanager.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    @Select("<script>" +
            "SELECT t.*, " +
            "(SELECT COUNT(*) FROM task_tags WHERE task_id = t.id) AS tag_count, " +
            "(SELECT COUNT(*) FROM comments WHERE task_id = t.id) AS comment_count, " +
            "(SELECT COUNT(*) FROM sub_tasks WHERE task_id = t.id AND is_deleted = 0) AS sub_task_count " +
            "FROM tasks t " +
            "WHERE t.user_id = #{userId} AND t.is_deleted = 1 " +
            "<if test='keyword != null'> AND (t.title LIKE CONCAT('%', #{keyword}, '%') OR t.description LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "</script>")
    IPage<UserTaskVO> selectDeletedTasksByUserId(Page<UserTaskVO> page,
                                             @Param("userId") Long userId,
                                             @Param("keyword") String keyword);

    @Select("<script>" +
            "SELECT t.*, ut.status AS user_status, ut.score, ut.completed_time AS user_completed_time, " +
            "(SELECT COUNT(*) FROM comments WHERE task_id = t.id) AS comment_count, " +
            "(SELECT COUNT(*) FROM sub_tasks WHERE task_id = t.id AND is_deleted = 0) AS sub_task_count " +
            "FROM tasks t " +
            "JOIN user_tasks ut ON t.id = ut.task_id " +
            "WHERE ut.user_id = #{userId} AND t.is_deleted = 0 AND ut.is_deleted = 0 " +
            "<if test='status != null'> AND ut.status = #{status} </if>" +
            "<if test='keyword != null'> AND (t.title LIKE CONCAT('%', #{keyword}, '%') OR t.description LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "ORDER BY t.created_time DESC" +
            "</script>")
    IPage<UserTaskVO> selectTasksByUserId(Page<UserTaskVO> page,
                                      @Param("userId") Long userId,
                                      @Param("status") String status,
                                      @Param("keyword") String keyword);
}