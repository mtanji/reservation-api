package com.volcano.visit.reservation.task;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.entity.Occupancy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Adds occupancy records in database to avoid Phantom read side effects on newly created Occupancy records
 */
@Component
public class AddOccupancyRecordTask {

    private static final Logger logger = LoggerFactory.getLogger(AddOccupancyRecordTask.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private OccupancyDAO occupancyDAO;

    //     * * * * * ? - every second
    //     0 * * * * ? - every minute
    //0-58/2 * * * * ? - every other second
    //     0 0 1 * * ? - 1:00AM everyday
    @PostConstruct
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleTaskWithCronExpression() {
        logger.info("Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        // TODO confirm if this cumbersome code is necessary
        LocalDate currentDate = LocalDate.now().plusDays(1);
        do {
            if (occupancyDAO.findByEpochDay(currentDate.toEpochDay()) == null) {
                occupancyDAO.save(new Occupancy(currentDate.toEpochDay(), 0));
                logger.info("Saved new empty occupancy for day [yyyy-MM-dd] " + currentDate);
            }
            currentDate = currentDate.plusDays(1);
        } while (ChronoUnit.DAYS.between(LocalDate.now(), currentDate) <= 31);
    }
}
