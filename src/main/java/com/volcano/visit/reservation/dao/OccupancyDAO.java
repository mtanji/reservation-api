package com.volcano.visit.reservation.dao;

import com.volcano.visit.reservation.entity.Occupancy;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;

public interface OccupancyDAO extends CrudRepository<Occupancy, LocalDate> {

    Occupancy findByEpochDay(long epochDay);
}