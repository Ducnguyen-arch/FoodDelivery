package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.entity.UserEntity;
import com.foodecommerce.foodies.io.UserRequest;
import com.foodecommerce.foodies.io.UserResponse;
import com.foodecommerce.foodies.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationFacade authenticationFacade;

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        UserEntity newUser = convertUserRequestToUserEntity(userRequest);
        userRepository.save(newUser);
        return convertUserEntityToUserResponse(newUser);
    }


    @Override
    public String findByUserId() {
         String loggedInUserEmail = authenticationFacade.getAuthentication().getName();
         UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(
                 () -> new UsernameNotFoundException("Không tìm thấy tên người dùng"));
         return loggedInUser.getId();
    }

    @Override
    public String findUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email))
                .getId();
    }


    private UserEntity convertUserRequestToUserEntity(UserRequest userRequest) {
        return UserEntity.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .name(userRequest.getName())
                .build();
    }

    private UserResponse convertUserEntityToUserResponse(UserEntity registerUser) {
        return UserResponse.builder()
                .id(registerUser.getId())
                .name(registerUser.getName())
                .email(registerUser.getEmail())
                .build();
    }

}
