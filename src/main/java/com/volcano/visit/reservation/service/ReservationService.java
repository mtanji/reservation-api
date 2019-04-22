package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.dao.ReservationDAO;
import com.volcano.visit.reservation.dto.RevervationIdWithOccupancies;
import com.volcano.visit.reservation.entity.Occupancy;
import com.volcano.visit.reservation.entity.Reservation;
import com.volcano.visit.reservation.exception.InvalidAntecedenceException;
import com.volcano.visit.reservation.exception.InvalidDateRangeException;
import com.volcano.visit.reservation.exception.NoVacancyException;
import com.volcano.visit.reservation.exception.ReservationNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;

@Service
@Transactional
public class ReservationService implements IReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Value("${reservation.max-visitors}")
    private int maxVisitors;

    @Autowired
    private OccupancyService occupancyService;
    @Autowired
    private OccupancyCacheService occupancyCacheService;
    @Autowired
    private OccupancyDAO occupancyDAO;
    @Autowired
    private ReservationDAO reservationDAO;
    @Autowired
    private MemcachedService memcache;

    @Override
    public RevervationIdWithOccupancies saveReservation(Reservation reservation) {
        // execute validations
        validateDateRange(reservation);
        validateAntecedence(reservation);
        List<Occupancy> occupancies = getVacancyForPeriod(reservation.getArrivalDate(),
                reservation.getDepartureDate());
        Integer numberOfPeople = reservation.getNumberOfPeople();
        checkVacancy(numberOfPeople, occupancies);

        // update database
        incrementOccupanciesByNumberOfPeople(occupancies, numberOfPeople);
        Reservation savedReservation = reservationDAO.save(reservation);
        logger.info("saved: " + savedReservation);

        return new RevervationIdWithOccupancies(savedReservation.getId(), occupancies);
    }

    private void validateDateRange(final Reservation reservation) {
        long rangeInDays = ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate());
        if (rangeInDays < 0 || rangeInDays > 2) {
            throw new InvalidDateRangeException("Reservation period is invalid. It must be between 1 and 3 days.");
        }
    }

    private void validateAntecedence(final Reservation reservation) {
        LocalDate today = LocalDate.now();
        boolean isAtLeastOneDayAntecedence =
                ChronoUnit.DAYS.between(today, reservation.getArrivalDate().minusDays(1)) >= 0;
        if (!isAtLeastOneDayAntecedence) {
            throw new InvalidAntecedenceException("Arrival date must be at least one day after reservation date.");
        }
        boolean isAtMostOneMonthAntecedence =
                ChronoUnit.DAYS.between(today, reservation.getDepartureDate().minusMonths(1)) <= 0;
        if (!isAtMostOneMonthAntecedence) {
            throw new InvalidAntecedenceException("Departure date must be at most one month after reservation date.");
        }
    }

    private List<Occupancy> getVacancyForPeriod(final LocalDate arrivalDate, final LocalDate departureDate) {
        List<Occupancy> datesOccupancy = new ArrayList<>();
        LocalDate currentDate = arrivalDate;
        do {
            Occupancy occupancy = occupancyDAO.findByEpochDay(currentDate.toEpochDay());
            datesOccupancy.add(occupancy);
            currentDate = currentDate.plusDays(1);
        } while (ChronoUnit.DAYS.between(currentDate, departureDate) >= 0);
        return datesOccupancy;
    }

    private void checkVacancy(final int numberOfPeople, final List<Occupancy> datesOccupancy) {
        List<LocalDate> noVacancyDays = datesOccupancy.stream()
                .filter(occupancy -> occupancy.getBookedVisitors() + numberOfPeople > maxVisitors)
                .map(occupancy -> LocalDate.ofEpochDay(occupancy.getEpochDay()))
                .collect(Collectors.toList());
        if (!noVacancyDays.isEmpty()) {
            throw new NoVacancyException("Some day(s) have no vacancy: " + noVacancyDays);
        }
    }

    @Override
    public Reservation getReservationById(Integer reservationId) {
        Optional<Reservation> reservation = reservationDAO.findById(reservationId);
        if (reservation.isPresent()) {
            return reservation.get();
        } else {
            throw new ReservationNotFoundException("Reservation does not exist.");
        }
    }

    @Override
    public List<Occupancy> changeReservation(Reservation reservation) {
        final List<Occupancy> occupancies = new ArrayList<>();
        final int previousNumberOfPeople = getPreviousNumberOfPeople(reservation.getId(), occupancies);

        // update database
        decrementOccupanciesByNumberOfPeople(occupancies, previousNumberOfPeople);

        // save updated reservation
        saveReservation(reservation);

        return occupancies;
    }

    @Override
    public List<Occupancy> cancelReservation(Integer reservationId) {
        final List<Occupancy> occupancies = new ArrayList<>();
        final int previousNumberOfPeople = getPreviousNumberOfPeople(reservationId, occupancies);

        // update database
        decrementOccupanciesByNumberOfPeople(occupancies, previousNumberOfPeople);

        // delete reservation
        reservationDAO.deleteById(reservationId);

        return occupancies;
    }

    private int getPreviousNumberOfPeople(Integer reservationId, List<Occupancy> occupancies) {
        Reservation reservationFromDb = reservationDAO.findById(reservationId).get();
        int previousNumberOfPeople = reservationFromDb.getNumberOfPeople();
        occupancies.addAll(getVacancyForPeriod(reservationFromDb.getArrivalDate(),
                reservationFromDb.getDepartureDate()));
        return previousNumberOfPeople;
    }

    private void incrementOccupanciesByNumberOfPeople(final List<Occupancy> occupancies, final int numberOfPeople) {
        occupancies.forEach(occupancy -> {
            occupancy.setBookedVisitors(occupancy.getBookedVisitors() + numberOfPeople);
            occupancyDAO.save(occupancy);
        });
    }

    private void decrementOccupanciesByNumberOfPeople(final List<Occupancy> occupancies, final int numberOfPeople) {
        occupancies.forEach(occupancy -> {
            occupancy.setBookedVisitors(occupancy.getBookedVisitors() - numberOfPeople);
            occupancyDAO.save(occupancy);
        });
    }
}