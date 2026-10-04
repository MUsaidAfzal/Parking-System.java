package com.parking.entity;

import jakarta.persistence.*;

@Entity
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    private boolean available = true;

    @ManyToOne
    private User assignedTo;

    protected ParkingSlot() { }

    public ParkingSlot(String slotNumber, VehicleType vehicleType) {
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
    }

    public Long getId() { return id; }
    public String getSlotNumber() { return slotNumber; }
    public VehicleType getVehicleType() { return vehicleType; }
    public boolean isAvailable() { return available; }
    public User getAssignedTo() { return assignedTo; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
}
