package com.why.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.taskmanager.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Update("UPDATE notifications SET is_read = true WHERE id = #{id}")
    void markAsRead(Long id);

    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND is_read = false")
    List<Notification> findUnreadByUserId(Long userId);
}
