package com.volcano.visit.reservation.service;

import com.volcano.visit.reservation.dao.IUserDAO;
import com.volcano.visit.reservation.dto.ApiDTOBuilder;
import com.volcano.visit.reservation.dto.UserDTO;
import com.volcano.visit.reservation.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Service
@Component
public class UserService implements IUserService {
    @Autowired
    private IUserDAO userDAO;

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userDAO.getUser(username);
        return ApiDTOBuilder.userToUserDTO(user);
    }
}