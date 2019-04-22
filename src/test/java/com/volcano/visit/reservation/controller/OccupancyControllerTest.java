package com.volcano.visit.reservation.controller;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.dao.ReservationDAO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class OccupancyControllerTest extends BaseControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(OccupancyControllerTest.class);
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
    private OccupancyController occupancyController;
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private OccupancyDAO occupancyDAO;
    @Autowired
    private ReservationDAO reservationDAO;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(occupancyController).build();
        addOccupancyRecordTask.scheduleTaskWithCronExpression();
    }

    @After
    public void tearDown() {
        cleanUpDatabase();
        memcachedService.flush();
    }

    @Test(timeout = 2000)
    public void givenOccupancyQueryWhenSubsequentQueryThenReturnFromCache() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Reservation reservationToSave = new Reservation(null, tomorrow.toEpochDay(), tomorrow.toEpochDay(),
                EMAIL_EMAIL_COM, FULL_NAME, NUMBER_OF_PEOPLE);
        ResponseEntity<String> reservationResponse = template
                .postForEntity("/api/reservation/", reservationToSave, String.class);

        // Act
        for (int i = 0; i < 5; i++) {
            // DEV profile causes db call to take reservation.occupancy.sleep milliseconds
            ResponseEntity<Integer> response = template
                    .exchange("/api/occupancy/date/" + tomorrow.toString(), HttpMethod.GET, null,
                            new ParameterizedTypeReference<Integer>() {
                            });
            logger.info("Occupancy for " + tomorrow + ": " + response.getBody());
        }

        // Assert
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
