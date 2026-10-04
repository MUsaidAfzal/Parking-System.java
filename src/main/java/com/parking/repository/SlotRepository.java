package com.parking.repository;

import com.parking.entity.ParkingSlot;
import com.parking.entity.User;
import com.parking.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByVehicleType(VehicleType type, Sort sort);

    boolean existsBySlotNumber(String slotNumber);

    Optional<ParkingSlot> findByAssignedToUsername(String username);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE ParkingSlot s SET s.available = false, s.assignedTo = :user " +
           "WHERE s.id = :id AND s.available = true")
    int assign(@Param("id") Long id, @Param("user") User user);
}
