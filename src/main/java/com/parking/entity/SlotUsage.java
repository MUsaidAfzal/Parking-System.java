package com.parking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SlotUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String slotNumber;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private Integer ratePerHour;

    @Column(nullable = false)
    private LocalDateTime timeIn;

    private LocalDateTime timeOut;        // null while the user is still parked
    private Integer hoursCharged;         // null until checkout
    private Integer amount;// whole rupees, null until checkout

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;     // null until the user picks one

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;     // PENDING from checkout until paid

    private String paymentLast4;             // last 4 digits only, for display

    protected SlotUsage() { }

    public SlotUsage(String slotNumber, String username, VehicleType vehicleType, LocalDateTime timeIn) {
        this.slotNumber = slotNumber;
        this.username = username;
        this.vehicleType = vehicleType;
        this.ratePerHour = vehicleType.getRatePerHour();
        this.timeIn = timeIn;
    }

    public String getSlotNumber() { return slotNumber; }
    public String getUsername() { return username; }
    public VehicleType getVehicleType() { return vehicleType; }
    public Integer getRatePerHour() { return ratePerHour; }
    public LocalDateTime getTimeIn() { return timeIn; }
    public LocalDateTime getTimeOut() { return timeOut; }
    public Integer getHoursCharged() { return hoursCharged; }
    public Integer getAmount() { return amount; }
    public Long getId() { return id; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getPaymentLast4() { return paymentLast4; }

    public void checkOut(LocalDateTime timeOut, int hoursCharged, int amount) {
        this.timeOut = timeOut;
        this.hoursCharged = hoursCharged;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
