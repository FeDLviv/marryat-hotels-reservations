package com.marryat.controller;

import com.marryat.domain.Reservation;
import com.marryat.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class ReservationController {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * POST  /reservations : Create a new reservation.
     *
     * @param reservation the reservation to save
     * @return the ResponseEntity with status 201 (Created) and with body the new reservation, or with status 400 (Bad Request) if the reservation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to save Reservation : {}", reservation);
        if (reservation.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        Reservation result = reservationService.save(reservation);
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
        Reservation result = reservationService.save(reservation);
        return ok().body(result);
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
     * @return the ResponseEntity with status 200 (OK) or with status 404 (Not Found)
     */
    @DeleteMapping("/reservations/{id}")

    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        return reservationService.delete(id) ? ok().build() : notFound().build();
    }

    /**
     * GET  /reservations : get all the reservations.
     *
     * @param from the date the startDate should be later or equal to
     * @param to   the date the startDate should be earlier or equal to
     * @return the ResponseEntity with status 200 (OK) and the list of reservations in body
     */
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getReservations(
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDate to) {
        List<Reservation> reservations;
        if (from != null && to != null) {
            reservations = reservationService.findReservationsByDateRange(from, to);
        } else if (from != null || to != null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            reservations = reservationService.findAll();
        }
        return wrapOrNotFound(Optional.ofNullable(reservations));
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(response -> ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
