package com.parking.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService {


    public int hoursCharged(LocalDateTime timeIn, LocalDateTime timeOut) {
        long minutes = Duration.between(timeIn, timeOut).toMinutes();
        return (int) Math.max(1, (minutes + 59) / 60);
    }

    public int amount(int hours, int ratePerHour) {
        return hours * ratePerHour;
    }
}
