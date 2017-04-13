package com.marryat.controller;


import com.marryat.MarryatHotelsReservationsApplication;
import com.marryat.domain.Reservation;
import com.marryat.repository.ReservationRepository;
import com.marryat.service.ReservationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ReservationController.
 *
 * @see ReservationController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MarryatHotelsReservationsApplication.class)
public class ReservationControllerIntTest {

    private static final String DEFAULT_FIRST_NAME = "John";
    private static final String UPDATED_FIRST_NAME = "Chuck";

    private static final String DEFAULT_LAST_NAME = "Doe";
    private static final String UPDATED_LAST_NAME = "Berry";

    private static final Integer DEFAULT_ROOM_NUMBER = 1;
    private static final Integer UPDATED_ROOM_NUMBER = 2;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2017, 11, 10);
    private static final LocalDate UPDATED_START_DATE = LocalDate.of(2017, 11, 15);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.of(2017, 11, 14);
    private static final LocalDate UPDATED_END_DATE = LocalDate.of(2017, 11, 18);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc reservationMockMvc;

    private Reservation reservation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReservationController reservationResource = new ReservationController(reservationService);
        this.reservationMockMvc = MockMvcBuilders.standaloneSetup(reservationResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        reservation = new Reservation();
        reservation.setFirstName(DEFAULT_FIRST_NAME);
        reservation.setLastName(DEFAULT_LAST_NAME);
        reservation.setRoomNumber(DEFAULT_ROOM_NUMBER);
        reservation.setStartDate(DEFAULT_START_DATE);
        reservation.setEndDate(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    public void shouldCreateReservation() throws Exception {
        int databaseSizeBeforeCreate = reservationRepository.findAll().size();
        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isCreated());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeCreate + 1);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testReservation.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testReservation.getRoomNumber()).isEqualTo(DEFAULT_ROOM_NUMBER);
        assertThat(testReservation.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testReservation.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    public void shouldFailOnCreateReservationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reservationRepository.findAll().size();

        // Create the Reservation with an existing ID
        reservation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void shouldFailOnFirstNameMissing() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().size();
        // set the field null
        reservation.setFirstName(null);

        // Create the Reservation, which fails.

        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isBadRequest());

        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void shouldFailOnLastNameMissing() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().size();
        reservation.setLastName(null);

        // Create the Reservation, which fails.

        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isBadRequest());

        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void shouldFailOnRoomNumberMissing() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().size();
        reservation.setRoomNumber(null);

        // Create the Reservation, which fails.

        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isBadRequest());

        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void shouldFailOnStartDateMissing() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().size();
        reservation.setStartDate(null);

        // Create the Reservation, which fails.

        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isBadRequest());

        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void shouldFailOnEndDateMissing() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().size();
        reservation.setEndDate(null);

        // Create the Reservation, which fails.

        reservationMockMvc.perform(post("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isBadRequest());

        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void shouldGetAllReservations() throws Exception {
        reservationRepository.saveAndFlush(reservation);

        // Get all the reservationList
        ResultActions resultActions = reservationMockMvc.perform(get("/reservations").accept(TestUtil
                .APPLICATION_JSON_UTF8));
        expectListContainsReservation(resultActions, reservation);
    }

    @Test
    @Transactional
    public void shouldFilterOutReservationsByStartDate() throws Exception {
        reservationRepository.saveAndFlush(reservation);
        Reservation bartsReservation = new Reservation();
        bartsReservation.setFirstName("Bart");
        bartsReservation.setLastName("Simpson");
        bartsReservation.setRoomNumber(3);
        bartsReservation.setStartDate(LocalDate.of(2015, 11, 10));
        bartsReservation.setEndDate(LocalDate.of(2015, 12, 11));
        reservationRepository.saveAndFlush(bartsReservation);


        Reservation bendersReservation = new Reservation();
        bendersReservation.setFirstName("Bender");
        bendersReservation.setLastName("Rodriguez");
        bendersReservation.setRoomNumber(4);
        bendersReservation.setStartDate(LocalDate.of(2018, 1, 5));
        bendersReservation.setEndDate(LocalDate.of(2018, 1, 12));
        reservationRepository.saveAndFlush(bendersReservation);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String from = reservation.getStartDate().format(formatter);
        String to = reservation.getStartDate().format(formatter);
        ResultActions response = reservationMockMvc.perform(get(String.format("/reservations?from=%s&to=%s", from,
                to)));

        expectListContainsReservation(response, reservation);
        expectListContainsNoReservation(response, bartsReservation);
        expectListContainsNoReservation(response, bendersReservation);

    }

    @Test
    @Transactional
    public void shouldGetExistingReservation() throws Exception {
        // Initialize the database
        reservationRepository.saveAndFlush(reservation);

        // Get the reservation
        ResultActions resultActions = reservationMockMvc.perform(get("/reservations/{id}", reservation.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        expectSingleReservation(resultActions, reservation);

    }

    @Test
    @Transactional
    public void shouldReturnNotFoundOnGetNonExistingReservation() throws Exception {
        // Get the reservation
        reservationMockMvc.perform(get("/reservations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void shouldUpdateExistingReservation() throws Exception {
        reservationService.save(reservation);

        int databaseSizeBeforeUpdate = reservationRepository.findAll().size();

        // Update the reservation
        Reservation updatedReservation = reservationRepository.findOne(reservation.getId());
        updatedReservation.setFirstName(UPDATED_FIRST_NAME);
        updatedReservation.setLastName(UPDATED_LAST_NAME);
        updatedReservation.setRoomNumber(UPDATED_ROOM_NUMBER);
        updatedReservation.setStartDate(UPDATED_START_DATE);
        updatedReservation.setEndDate(UPDATED_END_DATE);

       reservationMockMvc.perform(put("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedReservation)))
                .andExpect(status().isOk());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testReservation.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testReservation.getRoomNumber()).isEqualTo(UPDATED_ROOM_NUMBER);
        assertThat(testReservation.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testReservation.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void shouldCreateReservationOnUpdateNonExistingReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().size();

        // If the entity doesn't have an ID, it will be created instead of just being updated
        reservationMockMvc.perform(put("/reservations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reservation)))
                .andExpect(status().isCreated());

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void shouldDeleteReservation() throws Exception {
        reservationService.save(reservation);

        reservationMockMvc.perform(delete("/reservations/{id}", reservation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList).isEmpty();
    }

    @Test
    @Transactional
    public void shouldVerifyEquals() throws Exception {
        TestUtil.equalsVerifier(Reservation.class);
    }

    private void expectListContainsReservation(ResultActions resultActions, Reservation reservation) throws Exception {
        resultActions.andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(reservation.getFirstName())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(reservation.getLastName())))
                .andExpect(jsonPath("$.[*].roomNumber").value(hasItem(reservation.getRoomNumber())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(reservation.getStartDate().toString())))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(reservation.getEndDate().toString())));
    }

    private void expectListContainsNoReservation(ResultActions resultActions, Reservation reservation) throws Exception {
        resultActions.andExpect(jsonPath("$.[*].id").value(describedAs("Reservation with id " + reservation.getId() +
                " should not be present", not(hasItem(reservation.getId().intValue())))))
                .andExpect(jsonPath("$.[*].firstName").value(not(hasItem(reservation.getFirstName()))))
                .andExpect(jsonPath("$.[*].lastName").value(not(hasItem(reservation.getLastName()))))
                .andExpect(jsonPath("$.[*].roomNumber").value(not(hasItem(reservation.getRoomNumber()))))
                .andExpect(jsonPath("$.[*].startDate").value(not(hasItem(reservation.getStartDate().toString()))))
                .andExpect(jsonPath("$.[*].endDate").value(not(hasItem(reservation.getEndDate().toString()))));
    }

    private void expectSingleReservation(ResultActions resultActions, Reservation reservation) throws Exception {
        resultActions.andExpect(jsonPath("$.id").value(reservation.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
                .andExpect(jsonPath("$.roomNumber").value(DEFAULT_ROOM_NUMBER))
                .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

}
