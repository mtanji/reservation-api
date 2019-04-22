package com.volcano.visit.reservation.dao;

import com.volcano.visit.reservation.entity.User;

public interface IUserDAO {
    User getUser(String username);
}