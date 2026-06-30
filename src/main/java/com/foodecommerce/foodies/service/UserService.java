package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.io.UserRequest;
import com.foodecommerce.foodies.io.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest userRequest);

    String findByUserId();
    String findUserIdByEmail(String email);
}
