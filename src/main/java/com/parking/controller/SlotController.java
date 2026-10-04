package com.parking.controller;

import com.parking.dto.SlotView;
import com.parking.dto.Receipt;
import com.parking.service.SlotService;
import com.parking.service.UsageService;
import com.parking.entity.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.parking.dto.MySlot;
import com.parking.entity.SlotUsage;
import java.util.List;


@RestController
@RequestMapping("/api/slots")
public class SlotController {

    public record NewSlot(@NotBlank String slotNumber, @NotNull VehicleType vehicleType) { }

    private final SlotService service;
    private final UsageService usage;

    public SlotController(SlotService service, UsageService usage) {
        this.service = service;
        this.usage = usage;
    }

    @GetMapping
    public List<SlotView> all(Authentication auth, @RequestParam(required = false) VehicleType type) {
        return service.all(isAdmin(auth), type);
    }

    @GetMapping("/mine")
    public MySlot mine(Authentication auth) {
        return service.mine(auth.getName());
    }

    @GetMapping("/records")
    public List<SlotUsage> records() {
        return usage.records();
    }

    @PostMapping("/{id}/assign")
    public void assign(@PathVariable Long id, Authentication auth) {
        service.assign(id, auth.getName());
    }

    @PostMapping("/{id}/release")
    public Receipt release(@PathVariable Long id, Authentication auth) {
        return service.release(id, auth.getName(), isAdmin(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotView add(@Valid @RequestBody NewSlot body) {
        return service.add(body.slotNumber(), body.vehicleType());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/availability")
    public void setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        service.setAvailability(id, available);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
