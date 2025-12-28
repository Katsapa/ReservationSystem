package katsapa.spring.reservation_system.resrvations;


import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}/{value}")
    public ResponseEntity<Reservation> getReservationByID(@PathVariable Long id){
        log.info("Called getReservationByID by id: " + id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.getReservationByID(id));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
            ){
        log.info("Called getAllReservations");
        var filter = new ReservationSearchFilter(
                roomId,
                userId,
                pageSize,
                pageNumber
                );
        return ResponseEntity.ok(reservationService.searchAllByFilter(filter));
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody @Valid Reservation reservationToCreate
    ){
        log.info("Called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody @Valid Reservation reservationToUpdate
    ){
        log.info("Called update method id={}, reseravationToUpdate={}", id, reservationToUpdate);
        var update =  reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(update);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id
    ){
        log.info("Called delete method id={}", id);
        reservationService.cancelReservation(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable Long id
    ) {
        log.info("Called approveReservation : id = {}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
