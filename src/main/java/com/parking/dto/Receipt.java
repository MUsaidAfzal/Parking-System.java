package com.parking.dto;

import com.parking.entity.PaymentMethod;
import com.parking.entity.PaymentStatus;
import com.parking.entity.SlotUsage;
import com.parking.entity.VehicleType;

import java.time.LocalDateTime;

public record Receipt(Long id, String slotNumber, VehicleType vehicleType,
                      LocalDateTime timeIn, LocalDateTime timeOut,
                      int hoursCharged, int ratePerHour, int amount,
                      PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                      String paymentLast4) {

    public static Receipt of(SlotUsage u) {
        return new Receipt(u.getId(), u.getSlotNumber(), u.getVehicleType(),
                u.getTimeIn(), u.getTimeOut(),
                u.getHoursCharged(), u.getRatePerHour(), u.getAmount(),
                u.getPaymentMethod(), u.getPaymentStatus(),
                u.getPaymentLast4());
    }
}
