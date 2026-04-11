package com.projects.lovable.service.impl;

import com.projects.lovable.dto.auth.UserProfileReponse;
import com.projects.lovable.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileReponse getProfile(Long userId) {
        return null;
    }
}
