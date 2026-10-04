package com.parking.repository;

import com.parking.entity.SlotUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import com.parking.entity.PaymentMethod;
import com.parking.entity.PaymentStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SlotUsageRepository extends JpaRepository<SlotUsage, Long> {

    Optional<SlotUsage> findBySlotNumberAndTimeOutIsNull(String slotNumber);   // the open stay

    List<SlotUsage> findAllByOrderByTimeInDesc();   // newest first

    // The user's latest checked-out stay that still has no payment option chosen
    Optional<SlotUsage> findFirstByUsernameAndPaymentStatusAndPaymentMethodIsNullOrderByTimeOutDesc(
            String username, PaymentStatus status);

    // Atomic, like the slot assignment: succeeds only if nothing has been chosen yet
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE SlotUsage u SET u.paymentMethod = :method, u.paymentStatus = :status, " +
            "u.paymentLast4 = :last4 " +
            "WHERE u.id = :id AND u.username = :username AND u.paymentMethod IS NULL " +
            "AND u.paymentStatus = com.parking.entity.PaymentStatus.PENDING")
    int pay(@Param("id") Long id, @Param("username") String username,
            @Param("method") PaymentMethod method, @Param("status") PaymentStatus status,
            @Param("last4") String last4);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE SlotUsage u SET u.paymentStatus = com.parking.entity.PaymentStatus.PAID " +
            "WHERE u.id = :id AND u.paymentMethod = com.parking.entity.PaymentMethod.CASH " +
            "AND u.paymentStatus = com.parking.entity.PaymentStatus.PENDING")
    int markCashReceived(@Param("id") Long id);
}
