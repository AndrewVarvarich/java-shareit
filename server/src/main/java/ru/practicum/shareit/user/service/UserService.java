package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(UserDto user);

    User getUser(Long userId);

    void deleteUser(Long userId);

    User updateUser(Long id,UserDto user);

    List<User> getAllUsers();

    User findUserById(Long userId);
}
