package katsapa.spring.reservation_system.resrvations;

import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public Reservation toDomain(
            ReservationEntity it
    ){
        return new Reservation(
                it.getId(),
                it.getUserId(),
                it.getRoomId(),
                it.getStartDate(),
                it.getEndDate(),
                it.getStatus()
        );
    }

    public ReservationEntity toEntity(
            Reservation it
    ){
        return new ReservationEntity(
                it.id(),
                it.userId(),
                it.roomId(),
                it.startDate(),
                it.endDate(),
                it.status()
        );
    }
}
