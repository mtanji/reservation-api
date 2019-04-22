package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.entity.Occupancy;
import java.time.LocalDate;
import java.util.List;

public interface IOccupancyCacheService {
    void invalidateCache(List<Occupancy> occupancies);
    Object find(LocalDate date);
    void save(Occupancy occupancy);
}