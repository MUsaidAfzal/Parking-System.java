package com.parking.service;

import com.parking.dto.Receipt;
import com.parking.entity.PaymentMethod;
import com.parking.entity.PaymentStatus;
import com.parking.entity.SlotUsage;
import com.parking.repository.SlotUsageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.parking.util.AuditLog;

@Service
public class PaymentService {

    private final SlotUsageRepository history;
    private final AuditLog audit;

    public PaymentService(SlotUsageRepository history, AuditLog audit) {
        this.history = history;
        this.audit = audit;
    }

    public Receipt unpaid(String username) {
        return history.findFirstByUsernameAndPaymentStatusAndPaymentMethodIsNullOrderByTimeOutDesc(
                        username, PaymentStatus.PENDING)
                .map(Receipt::of)
                .orElse(null);
    }

    public Receipt cash(Long id, String username) {
        checkPayable(id, username);
        return pay(id, username, PaymentMethod.CASH, PaymentStatus.PENDING, null);
    }

    public Receipt card(Long id, String username, String cardNumber) {
        checkPayable(id, username);
        dummyCharge(cardNumber);
        return pay(id, username, PaymentMethod.CARD, PaymentStatus.PAID, cardNumber.substring(12));
    }

    public Receipt online(Long id, String username, String phone) {
        checkPayable(id, username);
        dummyCharge(phone);
        return pay(id, username, PaymentMethod.ONLINE, PaymentStatus.PAID, phone.substring(7));
    }

    // Stand-in for a real gateway: approves everything except numbers ending in 0000
    private void dummyCharge(String number) {
        if (number.endsWith("0000")) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Payment declined");
        }
    }


    private void checkPayable(Long id, String username) {
        SlotUsage u = history.findById(id)
                .filter(x -> x.getUsername().equals(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt not found"));
        if (u.getTimeOut() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Check out first");
        }
    }

    private Receipt pay(Long id, String username, PaymentMethod method, PaymentStatus status, String last4) {
        if (history.pay(id, username, method, status, last4) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This receipt is already paid");
        }
        return Receipt.of(history.findById(id).orElseThrow());
    }

    public void cashReceived(Long id, String admin) {
        if (history.markCashReceived(id) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not a pending cash payment");
        }
        audit.record("CASH RECEIVED", admin, history.findById(id).orElseThrow().getSlotNumber());
    }
}
