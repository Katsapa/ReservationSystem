package katsapa.spring.reservation_system.resrvations.availability;

import java.util.Stack;

public record CheckAvailabilityResponse(
        String message,
        AvailabilityStatus status
) {
}
