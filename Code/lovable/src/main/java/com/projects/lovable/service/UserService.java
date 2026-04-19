package com.projects.lovable.service;

import com.projects.lovable.dto.auth.UserProfileReponse;
import org.springframework.stereotype.Service;

public interface UserService {
    UserProfileReponse getProfile();
}
