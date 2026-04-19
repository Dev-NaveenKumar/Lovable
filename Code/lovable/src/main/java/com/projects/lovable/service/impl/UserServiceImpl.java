package com.projects.lovable.service.impl;

import com.projects.lovable.dto.auth.UserProfileReponse;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserProfileReponse getProfile(Long userId) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("User",username));
    }
}
