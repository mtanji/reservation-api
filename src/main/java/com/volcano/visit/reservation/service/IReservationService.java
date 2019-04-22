package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.dto.RevervationIdWithOccupancies;
import com.volcano.visit.reservation.entity.Occupancy;
import com.volcano.visit.reservation.entity.Reservation;
import java.util.List;

public interface IReservationService {
    RevervationIdWithOccupancies saveReservation(Reservation reservation);
    Reservation getReservationById(Integer reservationId);
    List<Occupancy> changeReservation(Reservation reservation);
    List<Occupancy> cancelReservation(Integer reservationId);
}