package com.marryat.controller;

import com.marryat.domain.Reservation;
import com.marryat.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
public class ReservationController {
    private final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * POST  /reservations : Create a new reservation.
     *
     * @param reservation the reservation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reservation, or with status 400 (Bad Request) if the reservation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to create Reservation : {}", reservation);
        if (reservation.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        Reservation result = reservationService.create(reservation);
        return ResponseEntity.created(new URI("/api/reservations/" + result.getId())).body(result);
    }

    /**
     * PUT  /reservations : Updates an existing reservation.
     *
     * @param reservation the reservation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reservation,
     * or with status 400 (Bad Request) if the reservation is not valid,
     * or with status 500 (Internal Server Error) if the reservation couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reservations")
    public ResponseEntity<Reservation> updateReservation(@Valid @RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to update Reservation : {}", reservation);
        if (reservation.getId() == null) {
            return createReservation(reservation);
        }
        Reservation result = reservationService.create(reservation);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /reservations : get all the reservations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of reservations in body
     */
    @GetMapping("/reservations")

    public List<Reservation> getAllReservations() {
        log.debug("REST request to get all Reservations");
        return reservationService.findAll();
    }

    /**
     * GET  /reservations/:id : get the "id" reservation.
     *
     * @param id the id of the reservation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reservation, or with status 404 (Not Found)
     */
    @GetMapping("/reservations/{id}")

    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        log.debug("REST request to get Reservation : {}", id);
        return wrapOrNotFound(Optional.ofNullable(reservationService.findOne(id)));
    }

    /**
     * DELETE  /reservations/:id : delete the "id" reservation.
     *
     * @param id the id of the reservation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reservations/{id}")

    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        reservationService.delete(id);
        return ResponseEntity.ok().build();
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
