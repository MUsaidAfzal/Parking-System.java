package com.parking.entity;

public enum VehicleType {
    CAR(50), BIKE(30), TRUCK(100);

    private final int ratePerHour;

    VehicleType(int ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public int getRatePerHour() {
        return ratePerHour;
    }
}
