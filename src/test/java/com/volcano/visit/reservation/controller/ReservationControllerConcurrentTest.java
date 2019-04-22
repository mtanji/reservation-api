package com.volcano.visit.reservation.controller;

import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.ThreadedAfter;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;
import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.entity.Occupancy;
import com.volcano.visit.reservation.entity.Reservation;
import com.volcano.visit.reservation.service.OccupancyService;
import com.volcano.visit.reservation.service.ReservationService;
import java.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReservationControllerConcurrentTest {

    private static final long EPOCH_DAY = 20000;
    private static final int BASE_AMOUNT = 100;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private OccupancyService occupancyService;

    @Autowired
    private OccupancyDAO occupancyDAO;

    @Test
    public void testConcurrentReservations() {
        AnnotatedTestRunner runner = new AnnotatedTestRunner();
        runner.runTests(getClass(), ReservationService.class);
    }

    @ThreadedBefore
    public void before() {
        Occupancy occupancy = new Occupancy();
        occupancy.setEpochDay(EPOCH_DAY);
        occupancy.setBookedVisitors(0);
        occupancyDAO.save(occupancy);
    }

    @ThreadedMain
    public void mainThread() {
        Reservation reservation = buildReservation(1);
        reservationService.saveReservation(reservation);
    }

    @ThreadedSecondary
    public void secondThread() {
        Reservation reservation = buildReservation(2);
        reservationService.saveReservation(reservation);
    }

    @ThreadedAfter
    public void after() {
        int occupancy = occupancyService.getOccupancyByDate(LocalDate.ofEpochDay(EPOCH_DAY));
        Assert.assertTrue("", occupancy > 0 && occupancy < BASE_AMOUNT * 2);
    }

    @After
    public void tearDown() {
        occupancyDAO.deleteById(LocalDate.ofEpochDay(EPOCH_DAY));
    }

    private Reservation buildReservation(int parameter) {
        Reservation reservation = new Reservation();
        reservation.setArrivalDateEpochDays(EPOCH_DAY);
        reservation.setDepartureDateEpochDays(EPOCH_DAY);
        reservation.setEmail("email " + parameter);
        reservation.setFullName("full name " + parameter);
        reservation.setNumberOfPeople(BASE_AMOUNT + parameter);
        return reservation;
    }

}
