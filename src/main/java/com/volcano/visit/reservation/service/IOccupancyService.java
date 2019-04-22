package com.volcano.visit.reservation.service;

import java.time.LocalDate;

public interface IOccupancyService {
    Integer getOccupancyByDate(LocalDate date);
}