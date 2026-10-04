package com.parking.controller;

import com.parking.dto.Receipt;
import com.parking.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    public record CardPayment(
            @NotBlank(message = "Card number must be 16 digits")
            @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits") String cardNumber) { }

    public record OnlinePayment(
            @NotBlank(message = "Phone number must be 11 digits")
            @Pattern(regexp = "\\d{11}", message = "Phone number must be 11 digits") String phone) { }

    private final PaymentService payments;

    public PaymentController(PaymentService payments) {
        this.payments = payments;
    }

    @GetMapping("/unpaid")
    public Receipt unpaid(Authentication auth) {
        return payments.unpaid(auth.getName());
    }

    @PostMapping("/{id}/cash")
    public Receipt cash(@PathVariable Long id, Authentication auth) {
        return payments.cash(id, auth.getName());
    }

    @PostMapping("/{id}/card")
    public Receipt card(@PathVariable Long id, @Valid @RequestBody CardPayment body, Authentication auth) {
        return payments.card(id, auth.getName(), body.cardNumber());
    }

    @PostMapping("/{id}/online")
    public Receipt online(@PathVariable Long id, @Valid @RequestBody OnlinePayment body, Authentication auth) {
        return payments.online(id, auth.getName(), body.phone());
    }

    @PatchMapping("/{id}/received")
    public void cashReceived(@PathVariable Long id, Authentication auth) {
        payments.cashReceived(id, auth.getName());
    }
}