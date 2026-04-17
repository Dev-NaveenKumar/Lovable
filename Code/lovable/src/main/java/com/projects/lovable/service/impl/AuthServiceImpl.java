package com.projects.lovable.service.impl;

import com.projects.lovable.dto.auth.AuthResponse;
import com.projects.lovable.dto.auth.LoginRequest;
import com.projects.lovable.dto.auth.SignupRequest;
import com.projects.lovable.entity.User;
import com.projects.lovable.error.BadRequestException;
import com.projects.lovable.mapper.UserMapper;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupRequest request) {

        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new BadRequestException("User already exists with username :" + request.username());});

        User entity = userMapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity = userRepository.save(entity);
        return new AuthResponse("Sample",userMapper.toUserProfileReponse(entity));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
