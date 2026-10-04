package com.parking.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AuditLog {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    private AuditLog() { }

    private static class Holder {
        private static final AuditLog INSTANCE = new AuditLog();
    }

    public static AuditLog getInstance() {
        return Holder.INSTANCE;
    }

    public void record(String action, String username, String slotNumber) {
        System.out.println(LocalDateTime.now().format(FORMAT)
                + " | " + username + " | " + action + " | slot " + slotNumber);
    }
}
