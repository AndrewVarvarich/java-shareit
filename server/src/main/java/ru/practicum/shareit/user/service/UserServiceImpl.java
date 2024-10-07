package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.exception.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public User createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException("User with email " + user.getEmail() + " already exists");
        }
        User newUser = mapper.toUser(user);
        return userRepository.save(newUser);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " +
                userId + " not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(Long userid, UserDto user) {
        User exsistingUser = userRepository.findById(userid).orElseThrow(() -> new NotFoundException("User " +
                "with id " + user.getId() + " not found"));
        if (user.getEmail() != null) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new ConflictException("User with email " + user.getEmail() + " already exists");
            }
            exsistingUser.setEmail(user.getEmail());
        }

        if (user.getName() == null) {
            user.setName(exsistingUser.getName());
        }
        return userRepository.save(exsistingUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " +
                userId + " not found"));
    }
}
