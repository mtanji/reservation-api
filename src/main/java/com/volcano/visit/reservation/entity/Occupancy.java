package com.volcano.visit.reservation.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.OptimisticLocking;

@Entity
@Table(name = "occupancy")
@OptimisticLocking
public class Occupancy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long epochDay;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date; // just to ease reading data in database
    @Column(nullable = false)
    private Integer bookedVisitors;
    @Version
    private int version;

    public Occupancy() {
    }

    public Occupancy(long epochDay, Integer bookedVisitors) {
        this.epochDay = epochDay;
        this.date = Date.from(LocalDate.ofEpochDay(epochDay).atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.bookedVisitors = bookedVisitors;
    }

    public Long getEpochDay() {
        return epochDay;
    }

    public void setEpochDay(Long epochDay) {
        this.epochDay = epochDay;
        this.date = Date.from(LocalDate.ofEpochDay(epochDay).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Integer getBookedVisitors() {
        return bookedVisitors;
    }

    public void setBookedVisitors(Integer bookedVisitors) {
        this.bookedVisitors = bookedVisitors;
    }

    @Override
    public String toString() {
        return "Occupancy{" +
                "epochDay=" + epochDay +
                ", date=" + date +
                ", bookedVisitors=" + bookedVisitors +
                '}';
    }
}