package com.parking.service;

import com.parking.dto.MySlot;
import com.parking.dto.SlotView;
import com.parking.dto.Receipt;
import com.parking.entity.ParkingSlot;
import com.parking.entity.User;
import com.parking.entity.VehicleType;
import com.parking.entity.SlotUsage;
import com.parking.repository.SlotRepository;
import com.parking.repository.UserRepository;
import com.parking.util.AuditLog;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SlotService {

    private final SlotRepository slots;
    private final UserRepository users;
    private final UsageService usage;
    private final AuditLog audit;

    public SlotService(SlotRepository slots, UserRepository users, UsageService usage, AuditLog audit) {
        this.slots = slots;
        this.users = users;
        this.usage = usage;
        this.audit = audit;
    }

    public List<SlotView> all(boolean admin, VehicleType type) {
        Sort byNumber = Sort.by("slotNumber");
        List<ParkingSlot> list = type == null ? slots.findAll(byNumber) : slots.findByVehicleType(type, byNumber);
        return list.stream().map(s -> SlotView.of(s, admin)).toList();
    }


    public MySlot mine(String username) {
        return slots.findByAssignedToUsername(username)
                .flatMap(s -> usage.current(s.getSlotNumber())
                        .map(u -> new MySlot(s.getId(), s.getSlotNumber(), u.getTimeIn())))
                .orElse(null);
    }

    @Transactional
    public void assign(Long id, String username) {
        User user = users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (slots.findByAssignedToUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a slot");
        }
        if (slots.assign(id, user) == 0) {
            find(id); // 404 if the slot does not exist
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot is not available");
        }
        ParkingSlot slot = find(id);
        usage.timeIn(slot.getSlotNumber(), username, slot.getVehicleType());
        audit.record("ASSIGN", username, slot.getSlotNumber());
    }

    @Transactional
    public Receipt release(Long id, String username, boolean admin) {
        ParkingSlot slot = find(id);
        if (slot.getAssignedTo() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot is not assigned");
        }
        if (!admin && !slot.getAssignedTo().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your slot");
        }
        Optional<SlotUsage> closed = usage.timeOut(slot.getSlotNumber());
        slot.setAvailable(true);
        slot.setAssignedTo(null);
        audit.record("RELEASE", username, slot.getSlotNumber());
        return closed.map(Receipt::of).orElse(null);
    }

    @Transactional
    public void setAvailability(Long id, boolean available) {
        ParkingSlot slot = find(id);
        slot.setAvailable(available);
        if (available) {
            usage.timeOut(slot.getSlotNumber());
            slot.setAssignedTo(null);
        }
    }

    public SlotView add(String slotNumber, VehicleType type) {
        if (slots.existsBySlotNumber(slotNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot number already exists");
        }
        return SlotView.of(slots.save(new ParkingSlot(slotNumber, type)), true);
    }

    @Transactional
    public void delete(Long id) {
        ParkingSlot slot = find(id);
        usage.timeOut(slot.getSlotNumber());
        slots.delete(slot);
    }

    private ParkingSlot find(Long id) {
        return slots.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
    }
}
