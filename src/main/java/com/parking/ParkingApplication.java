package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.entity.Role;
import com.parking.entity.User;
import com.parking.entity.VehicleType;
import com.parking.util.AuditLog;
import com.parking.repository.SlotRepository;
import com.parking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class ParkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingApplication.class, args);
    }


    @Bean
    CommandLineRunner seed(UserRepository users, SlotRepository slots, PasswordEncoder encoder) {
        return args -> {
            if (users.count() == 0) {
                users.save(new User("admin", encoder.encode("admin123"), Role.ADMIN));
            }
            if (slots.count() == 0) {
                for (String number : List.of("A1", "A2", "A3", "A4", "A5")) {
                    slots.save(new ParkingSlot(number, VehicleType.CAR));
                }
                for (String number : List.of("B1", "B2", "B3")) {
                    slots.save(new ParkingSlot(number, VehicleType.BIKE));
                }
                for (String number : List.of("T1", "T2")) {
                    slots.save(new ParkingSlot(number, VehicleType.TRUCK));
                }
            }
        };
    }

    @Bean
    AuditLog auditLog() {
        return AuditLog.getInstance();
    }
}
