package com.why.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.taskmanager.entity.UserMonthlyScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMonthlyScoreMapper extends BaseMapper<UserMonthlyScore> {
    @Select("SELECT * FROM user_monthly_scores WHERE user_id = #{userId} AND month = #{month}")
    UserMonthlyScore findByUserAndMonth(@Param("userId") Long userId, @Param("month") String month);
}

