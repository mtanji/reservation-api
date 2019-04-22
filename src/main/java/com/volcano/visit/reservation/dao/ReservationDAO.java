package com.volcano.visit.reservation.dao;

import com.volcano.visit.reservation.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationDAO extends CrudRepository<Reservation, Integer> {
}