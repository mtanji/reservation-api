package com.volcano.visit.reservation.dto;

import com.volcano.visit.reservation.entity.Occupancy;
import java.util.List;

public class RevervationIdWithOccupancies {

    private int reservationId;
    private List<Occupancy> changedOccupancies;

    public RevervationIdWithOccupancies(int reservationId, List<Occupancy> changedOccupancies) {
        this.reservationId = reservationId;
        this.changedOccupancies = changedOccupancies;
    }

    public int getReservationId() {
        return reservationId;
    }

    public List<Occupancy> getChangedOccupancies() {
        return changedOccupancies;
    }
}
