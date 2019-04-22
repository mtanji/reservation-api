package com.volcano.visit.reservation.controller;

import com.volcano.visit.reservation.service.OccupancyService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/occupancy")
public class OccupancyController {

    @Autowired
    OccupancyService service;

    @GetMapping(
            value = "date/{date}",
            produces = "application/json"
    )
    public ResponseEntity<Integer> getOccupancyInDate(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        Integer visitorInDate = service.getOccupancyByDate(date);

        // TODO add response header for Cache-Control

        return new ResponseEntity<>(visitorInDate, HttpStatus.OK);
    }
}
