package com.volcano.visit.reservation.controller;

import com.volcano.visit.reservation.dao.OccupancyDAO;
import com.volcano.visit.reservation.dao.ReservationDAO;
import com.volcano.visit.reservation.entity.Occupancy;
import com.volcano.visit.reservation.entity.Reservation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public abstract class BaseControllerTest {

    protected abstract OccupancyDAO getOccupancyDAO();

    protected abstract ReservationDAO getReservationDAO();

    protected HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }


    protected void cleanUpDatabase() {
        if (getOccupancyDAO() != null) {
            for (Occupancy occupancy : getOccupancyDAO().findAll()) {
                getOccupancyDAO().delete(occupancy);
            }
        }
        if (getReservationDAO() != null) {
            for (Reservation reservation : getReservationDAO().findAll()) {
                getReservationDAO().delete(reservation);
            }
        }
    }
}