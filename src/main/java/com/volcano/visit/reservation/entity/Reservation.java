package com.volcano.visit.reservation.entity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date arrivalDate; // just to ease reading data in database
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date departureDate; // just to ease reading data in database
    @Column(nullable = false)
    private long arrivalDateEpochDays;
    @Column(nullable = false)
    private long departureDateEpochDays;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private Integer numberOfPeople;

    public Reservation() {

    }

    public Reservation(Integer id, long arrivalDateEpochDays, long departureDateEpochDays, String email,
            String fullName, Integer numberOfPeople) {
        this.id = id;
        this.arrivalDateEpochDays = arrivalDateEpochDays;
        this.departureDateEpochDays = departureDateEpochDays;
        this.arrivalDate = Date
                .from(LocalDate.ofEpochDay(arrivalDateEpochDays).atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.departureDate = Date
                .from(LocalDate.ofEpochDay(departureDateEpochDays).atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.email = email;
        this.fullName = fullName;
        this.numberOfPeople = numberOfPeople;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getArrivalDateEpochDays() {
        return arrivalDateEpochDays;
    }

    public void setArrivalDateEpochDays(long arrivalDateEpochDays) {
        this.arrivalDateEpochDays = arrivalDateEpochDays;
        this.arrivalDate = Date
                .from(LocalDate.ofEpochDay(arrivalDateEpochDays).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public LocalDate getArrivalDate() {
        return LocalDate.ofEpochDay(arrivalDateEpochDays);
    }

    public LocalDate getDepartureDate() {
        return LocalDate.ofEpochDay(departureDateEpochDays);
    }

    public long getDepartureDateEpochDays() {
        return departureDateEpochDays;
    }

    public void setDepartureDateEpochDays(long departureDateEpochDays) {
        this.departureDateEpochDays = departureDateEpochDays;
        this.departureDate = Date
                .from(LocalDate.ofEpochDay(departureDateEpochDays).atStartOfDay(ZoneId.systemDefault()).toInstant());
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
        return "Reservation{" +
                "id=" + id +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", arrivalDateEpochDays=" + arrivalDateEpochDays +
                ", departureDateEpochDays=" + departureDateEpochDays +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                '}';
    }
}