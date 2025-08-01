package com.why.taskmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.why.taskmanager.DTO.UserScoreVO;
import com.why.taskmanager.entity.UserMonthlyScore;
import com.why.taskmanager.mapper.UserMapper;
import com.why.taskmanager.mapper.UserMonthlyScoreMapper;
import com.why.taskmanager.mapper.UserTaskMapper;
import com.why.taskmanager.service.UserMonthlyScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMonthlyScoreServiceImpl implements UserMonthlyScoreService {

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Autowired
    private UserMonthlyScoreMapper scoreMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public void calculateAndSaveMonthlyScore(Integer year, Integer month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        List<UserScoreVO> scores = userTaskMapper.selectUserScoresByDateRange(start, end);

        for (UserScoreVO vo : scores) {
            // 查询是否已存在
            UserMonthlyScore existing = scoreMapper.selectOne(
                    new QueryWrapper<UserMonthlyScore>()
                            .eq("user_id", vo.getUserId())
                            .eq("year", year)
                            .eq("month", month)
            );

            if (existing != null) {
                existing.setTotalScore(vo.getTotalScore());
                existing.setCompletedCount(vo.getCompletedCount());
                existing.setUpdatedTime(LocalDateTime.now());
                scoreMapper.updateById(existing);
            } else {
                UserMonthlyScore newScore = new UserMonthlyScore();
                newScore.setUserId(vo.getUserId());
                newScore.setYear(year);
                newScore.setMonth(month);
                newScore.setTotalScore(vo.getTotalScore());
                newScore.setCompletedCount(vo.getCompletedCount());
                newScore.setCreatedTime(LocalDateTime.now());
                newScore.setUpdatedTime(LocalDateTime.now());
                scoreMapper.insert(newScore);
            }
        }
    }

    @Override
    public List<UserScoreVO> getMonthlyScores(Integer year, Integer month) {
        List<UserMonthlyScore> scores = scoreMapper.selectList(
                new QueryWrapper<UserMonthlyScore>()
                        .eq("year", year)
                        .eq("month", month)
        );

        return scores.stream().map(s -> {
            UserScoreVO vo = new UserScoreVO();
            vo.setUserId(s.getUserId());
            vo.setUsername(userMapper.selectById(s.getUserId()).getUsername());
            vo.setTotalScore(s.getTotalScore());
            vo.setCompletedCount(s.getCompletedCount());
            return vo;
        }).collect(Collectors.toList());
    }
}

