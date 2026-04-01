package com.projects.lovable.service;

import com.projects.lovable.dto.auth.AuthResponse;
import com.projects.lovable.dto.auth.LoginRequest;
import com.projects.lovable.dto.auth.SignupRequest;
import org.springframework.stereotype.Service;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
