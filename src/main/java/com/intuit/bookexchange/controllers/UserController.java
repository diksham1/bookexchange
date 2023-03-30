package com.intuit.bookexchange.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.bookexchange.dto.CreateUserRequest;
import com.intuit.bookexchange.dto.ErrorResponse;
import com.intuit.bookexchange.dto.UserResponse;
import com.intuit.bookexchange.entities.User;
import com.intuit.bookexchange.exceptions.InvalidCredentialsException;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.services.UserService;
import static com.intuit.bookexchange.common.UserHelper.buildUserResponse;
import static com.intuit.bookexchange.common.UserHelper.getUserURI;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);

        return ResponseEntity.created(getUserURI(user))
                .body(buildUserResponse(user));
    }

    @PostMapping("/login")
    ResponseEntity<UserResponse> loginUser(@RequestBody CreateUserRequest request) {
        User user = userService.loginUser(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(buildUserResponse(user));
    }

    @GetMapping("")
    ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream()
                .map(user -> buildUserResponse(user))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUser(@PathVariable int id) {
        return ResponseEntity.ok(buildUserResponse(userService.getUser(id)));
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            NotFoundException.class
    })
    ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus status;
        String errorMessage;

        if (e instanceof InvalidCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
            errorMessage = e.getMessage();
        } else if (e instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = e.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "Something went wrong";
        }

        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .message(errorMessage)
                        .build());
    }
}
