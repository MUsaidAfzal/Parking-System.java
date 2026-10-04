package com.parking.controller;

import com.parking.entity.VehicleType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
public class VehicleTypeController {

    public record VehicleTypeView(VehicleType type, int ratePerHour) { }

    @GetMapping
    public List<VehicleTypeView> all() {
        return Arrays.stream(VehicleType.values())
                .map(t -> new VehicleTypeView(t, t.getRatePerHour()))
                .toList();
    }
}
