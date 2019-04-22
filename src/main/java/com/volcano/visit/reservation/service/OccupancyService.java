package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.entity.Occupancy;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OccupancyService implements IOccupancyService {

    private static final int ZERO_VISITORS = 0;
    private static final Logger logger = LoggerFactory.getLogger(OccupancyService.class);

    @Value("${reservation.occupancy.sleep}")
    private String sleep;
    @Autowired
    private OccupancyDAO occupancyDAO;
    @Autowired
    private OccupancyCacheService occupancyCacheService;

    @Override
    public Integer getOccupancyByDate(LocalDate date) {

        // if exist in cache, return value
        Occupancy occupancy = (Occupancy) occupancyCacheService.find(date);
        if (occupancy != null) {
            logger.info("occupancy from cache: " + occupancy);
            return occupancy.getBookedVisitors();
        } else {
            occupancy = occupancyDAO.findByEpochDay(date.toEpochDay());
            if (occupancy != null) {
                simulateSlowDatabaseQuery();
                logger.info("occupancy from DB: " + occupancy);
                occupancyCacheService.save(occupancy);
                return occupancy.getBookedVisitors();
            }
        }
        Occupancy missingOccupancy = new Occupancy(date.toEpochDay(), ZERO_VISITORS);
        occupancyCacheService.save(missingOccupancy);
        logger.info("missing default occupancy: " + missingOccupancy);
        return ZERO_VISITORS;
    }

    private void simulateSlowDatabaseQuery() {
        if (sleep != null) {
            try {
                logger.info("Sleeping " + sleep + " milliseconds");
                Thread.sleep(Long.valueOf(sleep));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}