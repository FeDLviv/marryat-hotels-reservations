package com.marryat.service;

import com.marryat.domain.Reservation;
import com.marryat.repository.ReservationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTest {
    private static final long RESERVATION_ID = 1L;
    private static final LocalDate FROM = LocalDate.of(2017, 11, 10);
    private static final LocalDate TO = LocalDate.of(2017, 11, 15);

    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationService reservationService;
    private Reservation reservation = mock(Reservation.class);

    @Before
    public void setUp() {
        when(reservation.toString()).thenReturn("{\n" +
                "\"firstName\" :\"Ievgenii\",\n" +
                "\"lastName\" :\"Lopushen\",\n" +
                "\"roomNumber\": 1,\n" +
                "\"startDate\": \"2017-11-10\",\n" +
                "\"endDate\": \"2017-11-11\"\n" +
                "}");
        when(reservation.getId()).thenReturn(RESERVATION_ID);
    }

    @Test
    public void shouldSaveReservation() {
            reservationService.save(reservation);
            verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    public void shouldGetExistingReservation() {
        reservationService.findOne(RESERVATION_ID);
        verify(reservationRepository, times(1)).findOne(RESERVATION_ID);
    }

    @Test
    public void shouldGetGetAllReservations() {
        reservationService.findAll();
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    public void shouldDeleteExistingReservation() {
        when(reservationRepository.exists(RESERVATION_ID)).thenReturn(Boolean.TRUE);
        boolean deleted = reservationService.delete(RESERVATION_ID);
        verify(reservationRepository, times(1)).delete(RESERVATION_ID);
        assertTrue(deleted);
    }

    @Test
    public void shouldNotDeleteNonExistingReservation() {
        when(reservationRepository.exists(RESERVATION_ID)).thenReturn(Boolean.FALSE);
        boolean deleted = reservationService.delete(RESERVATION_ID);
        verify(reservationRepository, never()).delete(RESERVATION_ID);
        assertFalse(deleted);
    }

    @Test
    public void shouldFilterReservationsByStartDate() {
        reservationService.findReservationsByDateRange(FROM, TO);
        verify(reservationRepository, times(1)).findReservationsWithinDateRange(FROM, TO);
    }
}
