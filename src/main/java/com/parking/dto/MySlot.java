package com.parking.dto;

import java.time.LocalDateTime;

public record MySlot(Long id, String slotNumber, LocalDateTime timeIn) { }
