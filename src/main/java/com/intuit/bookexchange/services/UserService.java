package com.intuit.bookexchange.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.intuit.bookexchange.dto.CreateUserRequest;
import com.intuit.bookexchange.entities.User;
import com.intuit.bookexchange.exceptions.InvalidCredentialsException;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.repositories.UsersRepository;

@Service
public class UserService {
    private final UsersRepository userRepository;

    public UserService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(CreateUserRequest request) {
        User user = userRepository.save(User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .rewardPoints(0)
                .rating(5.0)
                .build());
        return user;
    }

    public User loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }

    public User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
