package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.dto.UserDTO;

public interface IUserService {
    public UserDTO getUserByUsername(String username);
}