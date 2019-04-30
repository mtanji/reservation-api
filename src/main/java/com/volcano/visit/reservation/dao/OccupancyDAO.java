package com.volcano.visit.reservation.dao;

import com.volcano.visit.reservation.entity.Occupancy;
import org.springframework.data.repository.CrudRepository;

public interface OccupancyDAO extends CrudRepository<Occupancy, Long> {

    Occupancy findByEpochDay(long epochDay);
}