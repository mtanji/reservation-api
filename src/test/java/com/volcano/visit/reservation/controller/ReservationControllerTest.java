package com.volcano.visit.reservation.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.dao.ReservationDAO;
import com.volcano.visit.reservation.entity.Occupancy;
import com.volcano.visit.reservation.entity.Reservation;
import com.volcano.visit.reservation.service.MemcachedService;
import com.volcano.visit.reservation.task.AddOccupancyRecordTask;
import java.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReservationControllerTest extends BaseControllerTest {

    private static final String EMAIL_EMAIL_COM = "email@email.com";
    private static final String FULL_NAME = "Full Name";
    private static final int NUMBER_OF_PEOPLE = 10;

    @Rule
    public ErrorCollector collector = new ErrorCollector();
    MockMvc mockMvc;
    @Autowired
    AddOccupancyRecordTask addOccupancyRecordTask;
    @Autowired
    MemcachedService memcachedService;
    @Mock
    private ReservationController reservationController;
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private OccupancyDAO occupancyDAO;
    @Autowired
    private ReservationDAO reservationDAO;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        addOccupancyRecordTask.scheduleTaskWithCronExpression();
    }

    @After
    public void tearDown() {
        cleanUpDatabase();
        memcachedService.flush();
    }

    @Test
    public void givenHasReservationWhenGetReservationThenReturnReservation() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Reservation reservationToSave = new Reservation(null, tomorrow.toEpochDay(), tomorrow.toEpochDay(),
                EMAIL_EMAIL_COM, FULL_NAME, NUMBER_OF_PEOPLE);
        Reservation savedReservation = reservationDAO.save(reservationToSave);

        // Act
        ResponseEntity<Reservation> response = template
                .exchange("/api/reservation/" + savedReservation.getId(), HttpMethod.GET, null,
                        new ParameterizedTypeReference<Reservation>() {
                        });
        // Assert
        Reservation reservation = response.getBody();
        collector.checkThat(reservation, is(notNullValue()));
        collector.checkThat(reservation.getNumberOfPeople(), equalTo(NUMBER_OF_PEOPLE));
        collector.checkThat(reservation.getEmail(), equalTo(EMAIL_EMAIL_COM));
        collector.checkThat(reservation.getFullName(), equalTo(FULL_NAME));
    }

    @Test
    public void givenNoVisitorsWhenReservationThenReserveAndOccupancy() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Reservation reservationToSave = new Reservation(null, tomorrow.toEpochDay(), tomorrow.toEpochDay(),
                EMAIL_EMAIL_COM, FULL_NAME, NUMBER_OF_PEOPLE);
        // Act
        ResponseEntity<String> response = template
                .postForEntity("/api/reservation/", reservationToSave, String.class);
        // Assert
        Occupancy occupancy = occupancyDAO.findByEpochDay(tomorrow.toEpochDay());
        Reservation reservation = reservationDAO.findById(Integer.valueOf(response.getBody())).get();
        collector.checkThat(occupancy.getBookedVisitors(), equalTo(NUMBER_OF_PEOPLE));
        collector.checkThat(reservation, is(notNullValue()));
        collector.checkThat(reservation.getNumberOfPeople(), equalTo(NUMBER_OF_PEOPLE));
        collector.checkThat(reservation.getEmail(), equalTo(EMAIL_EMAIL_COM));
        collector.checkThat(reservation.getFullName(), equalTo(FULL_NAME));
    }

    @Override
    protected OccupancyDAO getOccupancyDAO() {
        return occupancyDAO;
    }

    @Override
    protected ReservationDAO getReservationDAO() {
        return reservationDAO;
    }

}
