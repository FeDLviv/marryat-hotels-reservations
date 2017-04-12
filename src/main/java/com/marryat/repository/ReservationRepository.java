package com.marryat.repository;


import com.marryat.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r where r.startDate between ?1 and ?2")
    List<Reservation> findReservationsWithinDateRange(LocalDate startDate, LocalDate endDate);
}
