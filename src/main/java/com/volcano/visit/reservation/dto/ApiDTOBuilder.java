package com.volcano.visit.reservation.dto;

import com.volcano.visit.reservation.entity.Reservation;
import com.volcano.visit.reservation.entity.User;

public class ApiDTOBuilder {

    public static UserDTO userToUserDTO(User user) {
        return new UserDTO(user.getUsername(), "", user.getUserType(),
                user.getEmail(), user.getName(), user.getLastName(),
                user.getTel(), user.getBornDate());
    }

//    public static ReservationDTO reservationToReservationDTO(Reservation reservation) {
//        return new ReservationDTO(reservation.getId(), reservation.getArrivalDate(), reservation.getDepartureDate(),
//                reservation.getEmail(), reservation.getFullName());
//    }
}