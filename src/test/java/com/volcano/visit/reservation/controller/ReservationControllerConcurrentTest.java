package com.volcano.visit.reservation.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.dao.ReservationDAO;
import com.volcano.visit.reservation.dto.RevervationIdWithOccupancies;
import com.volcano.visit.reservation.entity.Reservation;
import com.volcano.visit.reservation.service.MemcachedService;
import com.volcano.visit.reservation.service.OccupancyService;
import com.volcano.visit.reservation.service.ReservationService;
import com.volcano.visit.reservation.task.AddOccupancyRecordTask;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReservationControllerConcurrentTest extends BaseControllerTest {

    private static final long EPOCH_DAY = LocalDate.now().plusDays(3).toEpochDay();
    private static final int BASE_AMOUNT = 10;

    @Autowired
    private AddOccupancyRecordTask addOccupancyRecordTask;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private OccupancyService occupancyService;

    @Autowired
    private MemcachedService memcachedService;

    @Autowired
    private OccupancyDAO occupancyDAO;

    @Autowired
    private ReservationDAO reservationDAO;

//    @Test
//    public void testConcurrentReservations() {
//        AnnotatedTestRunner runner = new AnnotatedTestRunner();
//        runner.runTests(getClass(), ReservationService.class);
//    }
//
//    @ThreadedBefore
//    public void before() {
//        Occupancy occupancy = new Occupancy();
//        occupancy.setEpochDay(EPOCH_DAY);
//        occupancy.setBookedVisitors(0);
//        occupancyDAO.save(occupancy);
//    }
//
//    @ThreadedMain
//    public void mainThread() {
//        Reservation reservation = buildReservation(1);
//        reservationService.saveReservation(reservation);
//    }
//
//    @ThreadedSecondary
//    public void secondThread() {
//        Reservation reservation = buildReservation(2);
//        reservationService.saveReservation(reservation);
//    }
//
//    @ThreadedAfter
//    public void after() {
//        int occupancy = occupancyService.getOccupancyByDate(LocalDate.ofEpochDay(EPOCH_DAY));
//        Assert.assertTrue("", occupancy > 0 && occupancy < BASE_AMOUNT * 2);
//    }

    @Before
    public void setup() {
        addOccupancyRecordTask.scheduleTaskWithCronExpression();
    }

    @After
    public void tearDown() {
        cleanUpDatabase();
        memcachedService.flush();
    }

    private Reservation buildReservation(int parameter) {
        Reservation reservation = new Reservation();
        reservation.setArrivalDateEpochDays(EPOCH_DAY);
        reservation.setDepartureDateEpochDays(EPOCH_DAY);
        reservation.setEmail("email " + parameter);
        reservation.setFullName("full name " + parameter);
        reservation.setNumberOfPeople(BASE_AMOUNT);
        return reservation;
    }

    /**
     * Based on this article https://dzone.com/articles/how-i-test-my-java-classes-for-thread-safety
     * @throws Exception
     */
    @Test
    public void testParallelReservations() throws Exception {
        final int threads = 5; // want overlaps to be larger than zero when threads is small
        final ExecutorService service = Executors.newFixedThreadPool(threads);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean running = new AtomicBoolean();
        final AtomicInteger overlaps = new AtomicInteger();
        final Collection<Future<RevervationIdWithOccupancies>> futures = new ArrayList<>(threads);

        // build all threads as Callable and add them in the ExecutorService
        for (int t = 0; t < threads; t++) {
            final int reservationId = t;
            futures.add(
                    service.submit(
                            () -> {
                                latch.await();
                                if (running.get()) {
                                    overlaps.incrementAndGet();
                                }
                                running.set(true);
                                Reservation reservation = buildReservation(reservationId);
                                RevervationIdWithOccupancies revervationIdWithOccupancies = reservationService
                                        .saveReservation(reservation);
                                running.set(false);
                                return revervationIdWithOccupancies;
                            }
                    )
            );
        }
        // triggers all threads at the same time so that overlapping is easy to happen
        latch.countDown();
        final Set<RevervationIdWithOccupancies> occupancies = new HashSet<>();

        // forces test to wait all callables to complete
        for (Future<RevervationIdWithOccupancies> f : futures) {
            occupancies.add(f.get());
        }
//        for (RevervationIdWithOccupancies x : occupancies) {
//            for (Occupancy o : x.getChangedOccupancies()) {
//                System.out.println(x.getReservationId() + ":" + o.getBookedVisitors());
//            }
//        }
//        System.out.println("overlaps.get():" + overlaps.get());

        // assert that threads have been overlapped executions
        assertThat(overlaps.get(), greaterThan(0));
        LocalDate date = LocalDate.ofEpochDay(EPOCH_DAY);
        assertThat(occupancyService.getOccupancyByDate(date), equalTo(threads * BASE_AMOUNT));
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
