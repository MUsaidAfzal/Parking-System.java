package com.parking.service;

import com.parking.entity.SlotUsage;
import com.parking.entity.VehicleType;
import com.parking.repository.SlotUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsageService {

    private final SlotUsageRepository history;
    private final PricingService pricing;

    public UsageService(SlotUsageRepository history, PricingService pricing) {
        this.history = history;
        this.pricing = pricing;
    }

    public void timeIn(String slotNumber, String username, VehicleType vehicleType) {
        history.save(new SlotUsage(slotNumber, username, vehicleType, LocalDateTime.now()));
    }


    @Transactional
    public Optional<SlotUsage> timeOut(String slotNumber) {
        Optional<SlotUsage> open = history.findBySlotNumberAndTimeOutIsNull(slotNumber);
        open.ifPresent(r -> {
            LocalDateTime now = LocalDateTime.now();
            int hours = pricing.hoursCharged(r.getTimeIn(), now);
            r.checkOut(now, hours, pricing.amount(hours, r.getRatePerHour()));
        });
        return open;
    }

    public Optional<SlotUsage> current(String slotNumber) {
        return history.findBySlotNumberAndTimeOutIsNull(slotNumber);
    }

    public List<SlotUsage> records() {
        return history.findAllByOrderByTimeInDesc();
    }
}
