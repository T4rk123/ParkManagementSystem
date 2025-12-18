package com.amusementpark.services;

import com.amusementpark.repositories.AttractionRepository;
import com.amusementpark.repositories.UserRepository;

public class StatisticsService {
    private UserRepository userRepo = new UserRepository();
    private AttractionRepository attrRepo = new AttractionRepository();

    public Long getUserCount() {
        return userRepo.countAll();
    }

    public Double getAverageWaitingTime() {
        return attrRepo.averageWaitingTime();
    }
}