package com.marryat.service;

import com.marryat.domain.Reservation;
import com.marryat.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Create a reservation.
     *
     * @param reservation the entity to save
     * @return the persisted entity
     */
    public Reservation save(Reservation reservation) {
        LOGGER.debug("Creating Reservation : {}", reservation);
        return reservationRepository.save(reservation);
    }

    /**
     * Get all the reservations.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        LOGGER.debug("Getting all Reservations");

        return reservationRepository.findAll();
    }

    /**
     * Get one reservation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Reservation findOne(Long id) {
        LOGGER.debug("Getting Reservation with id : {}", id);
        return reservationRepository.findOne(id);
    }

    /**
     * Delete the reservation by id.
     *
     * @param id the id of the entity
     */
    public boolean delete(Long id) {
        boolean deleted = false;
        LOGGER.debug("Deleting Reservation with id : {}", id);
        if (reservationRepository.exists(id)) {
            reservationRepository.delete(id);
            deleted = true;
        }
        return deleted;
    }

    /**
     * Find reservations by date range
     *
     * @param from the start of the date range
     * @param to the end of the date range
     * @return the list of reservations that are valid within the specified range
     */
    public List<Reservation> findReservationsByDateRange(LocalDate from, LocalDate to) {
        return reservationRepository.findReservationsWithinDateRange(from, to);
    }
}
