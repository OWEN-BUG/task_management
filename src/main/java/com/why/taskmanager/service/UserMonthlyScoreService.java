package com.why.taskmanager.service;

import com.why.taskmanager.DTO.UserScoreVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserMonthlyScoreService {

    @Transactional
    void calculateAndSaveMonthlyScore(Integer year, Integer month);

    List<UserScoreVO> getMonthlyScores(Integer year, Integer month);
}

