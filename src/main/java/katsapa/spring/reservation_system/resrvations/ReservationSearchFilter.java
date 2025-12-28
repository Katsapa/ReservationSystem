package katsapa.spring.reservation_system.resrvations;

public record ReservationSearchFilter(
        Long roomId,
        Long userId,
        Integer pageSize,
        Integer pageNumber
) {

}
