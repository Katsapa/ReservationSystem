package katsapa.spring.reservation_system.resrvations;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationMapper mapper;

    ReservationService(ReservationRepository repository, ReservationMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    public Reservation getReservationByID(Long id)  {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        return mapper.toDomain(reservationEntity);
    }

    public List<Reservation> findAllReservations() {
        List<ReservationEntity> allReservations = repository.findAll();
        return allReservations.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if(reservationToCreate.status() != null){
            throw new IllegalArgumentException("Status should by empty");
        }

        if(!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())){
            throw new IllegalArgumentException("start date must be 1 day earlier then and date");
        }

        var entityToSave = mapper.toEntity(reservationToCreate);
        entityToSave.setStatus(ReservationStatus.PENDING);

        ReservationEntity savedEntity = repository.save(entityToSave);

        return mapper.toDomain(savedEntity);
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if(reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("Cannot modify reservation with status = " + reservationEntity.getStatus());
        }

        var reservation = mapper.toEntity(reservationToUpdate);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setId(reservationEntity.getId());

        var updateReservation = repository.save(reservation);

        return mapper.toDomain(updateReservation);
    }

    public void cancelReservation(
            Long id
    ) {
        var reservation = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        if(!reservation.getStatus().equals(ReservationStatus.PENDING)){
            throw new IllegalStateException("Cannot cancel approved or already cancelled reservation");
        }
        repository.deleteById(id);
        log.info("Successfully cancelled reservation: id={}", id);
    }

    public Reservation approveReservation(Long id) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if(reservationEntity.getStatus().equals(ReservationStatus.PENDING)){
            throw new IllegalArgumentException("Can not approve reservation with status = " + reservationEntity.getStatus());
        }

        boolean conflict = isConflictReservation(
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate()
        );

        if(conflict){
            throw new IllegalArgumentException("Can not approve reservation because of conflict");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);
        var updateReservation = repository.save(reservationEntity);
        return mapper.toDomain(updateReservation);
    }

    private boolean isConflictReservation(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ){
         List<Long> conflictingIds = repository.findConflictReservationIds(
                 roomId,
                 startDate,
                 endDate,
                 ReservationStatus.APPROVED
         );
         if (conflictingIds.isEmpty()) return false;
         log.info("Conflicting with ids = {}", conflictingIds);
         return true;
    }
}
