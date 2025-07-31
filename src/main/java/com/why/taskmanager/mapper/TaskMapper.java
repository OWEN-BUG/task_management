package com.why.taskmanager.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.taskmanager.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    @Select("<script>" +
            "SELECT * FROM tasks " +
            "WHERE user_id = #{userId} AND is_deleted = 0 " +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='keyword != null'> AND (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "</script>")
    IPage<Task> selectTasksByUserId(Page<Task> page, @Param("userId") Long userId, @Param("status") String status, @Param("keyword") String keyword);
}
