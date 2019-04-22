package com.volcano.visit.reservation.dto;

import com.volcano.visit.reservation.entity.Reservation;
import java.io.Serializable;
import java.time.LocalDate;

public class ReservationDTO implements Serializable {

    private Integer id;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String email;
    private String fullName;
    private Integer numberOfPeople;
    // Constructor, Getter, Setters

    public ReservationDTO() {

    }

    public ReservationDTO(Integer id, LocalDate arrivalDate, LocalDate departureDate, String email,
            String fullName, Integer numberOfPeople) {
        this.id = id;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.email = email;
        this.fullName = fullName;
        this.numberOfPeople = numberOfPeople;
    }

    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.arrivalDate = LocalDate.ofEpochDay(reservation.getArrivalDateEpochDays());
        this.departureDate = LocalDate.ofEpochDay(reservation.getDepartureDateEpochDays());
        this.email = reservation.getEmail();
        this.fullName = reservation.getFullName();
        this.numberOfPeople = reservation.getNumberOfPeople();
    }

    public Reservation toEntity() {
        return new Reservation(id, arrivalDate.toEpochDay(), departureDate.toEpochDay(), email, fullName,
                numberOfPeople);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "id=" + id +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                '}';
    }
}