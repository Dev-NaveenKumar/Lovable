package com.projects.lovable.service.impl;

import com.projects.lovable.dto.auth.AuthResponse;
import com.projects.lovable.dto.auth.LoginRequest;
import com.projects.lovable.dto.auth.SignupRequest;
import com.projects.lovable.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
