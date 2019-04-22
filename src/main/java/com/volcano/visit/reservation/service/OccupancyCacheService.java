package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.entity.Occupancy;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OccupancyCacheService implements IOccupancyCacheService {

    private static Logger logger = LoggerFactory.getLogger(OccupancyCacheService.class);

    @Autowired
    private MemcachedService memcache;

    @Override
    public void save(Occupancy occupancy) {
        String key = getKey(occupancy.getEpochDay());
        memcache.set(key, 3600, occupancy);
    }

    @Override
    public Object find(LocalDate date) {
        String key = getKey(date.toEpochDay());
        return memcache.get(key, Occupancy.class);
    }

    @Override
    public void invalidateCache(List<Occupancy> occupancies) {
        occupancies.forEach(occupancy -> {
            invalidateCache(occupancy);
            logger.info("Invalidated cache occupancy: " + occupancy);
        });
    }

    private void invalidateCache(Occupancy occupancy) {
        String key = getKey(occupancy.getEpochDay());
        memcache.delete(key);
    }

    private String getKey(long epochDay) {
        StringBuilder builder = new StringBuilder();
        return builder.append(epochDay).append(".Occupancy").toString();
    }
}