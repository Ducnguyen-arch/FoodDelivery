package com.foodecommerce.foodies.controller;

import com.foodecommerce.foodies.io.UserRequest;
import com.foodecommerce.foodies.io.UserResponse;
import com.foodecommerce.foodies.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserRequest userRequest) {
        return userService.registerUser(userRequest);
    }
}
