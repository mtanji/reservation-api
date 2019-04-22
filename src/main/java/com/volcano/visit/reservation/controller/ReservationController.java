package com.volcano.visit.reservation.controller;

import com.volcano.visit.reservation.dto.ReservationDTO;
import com.volcano.visit.reservation.dto.RevervationIdWithOccupancies;
import com.volcano.visit.reservation.entity.Occupancy;
import com.volcano.visit.reservation.entity.Reservation;
import com.volcano.visit.reservation.service.OccupancyCacheService;
import com.volcano.visit.reservation.service.ReservationService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    ReservationService service;
    @Autowired
    OccupancyCacheService occupancyCacheService;

    @GetMapping(
            value = "{reservationId}",
            produces = "application/json"
    )
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable String reservationId) {
        Reservation reservation = service.getReservationById(Integer.valueOf(reservationId));
        ReservationDTO reservationDTO = new ReservationDTO(reservation);
        return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> saveReservation(@RequestBody ReservationDTO reservation) {
        logger.info(reservation.toString());
        RevervationIdWithOccupancies revervationIdWithOccupancies = service.saveReservation(reservation.toEntity());
        int reservationId = revervationIdWithOccupancies.getReservationId();

        // invalidate memcached only after database transaction has finished successfully
        occupancyCacheService.invalidateCache(revervationIdWithOccupancies.getChangedOccupancies());

        return new ResponseEntity<>(String.format("%010d", reservationId), HttpStatus.OK);
    }

    @PutMapping("{reservationId}")
    public ResponseEntity<Void> changeReservation(@PathVariable Integer reservationId,
            @RequestBody ReservationDTO reservationDTO) {
        reservationDTO.setId(reservationId);
        List<Occupancy> occupancies = service.changeReservation(reservationDTO.toEntity());

        // invalidate memcached only after database transaction has finished successfully
        occupancyCacheService.invalidateCache(occupancies);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Integer reservationId) {
        List<Occupancy> occupancies = service.cancelReservation(reservationId);

        // invalidate memcached only after database transaction has finished successfully
        occupancyCacheService.invalidateCache(occupancies);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

