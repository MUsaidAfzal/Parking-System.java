package com.parking.dto;

import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;

public record SlotView(Long id, String slotNumber, VehicleType vehicleType,
                       boolean available, boolean occupied, String assignedTo) {


    public static SlotView of(ParkingSlot s, boolean showHolder) {
        boolean occupied = s.getAssignedTo() != null;
        return new SlotView(s.getId(), s.getSlotNumber(), s.getVehicleType(), s.isAvailable(), occupied,
                occupied && showHolder ? s.getAssignedTo().getUsername() : null);
    }
}
