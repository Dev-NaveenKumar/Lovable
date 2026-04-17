package com.projects.lovable.mapper;

import com.projects.lovable.dto.auth.SignupRequest;
import com.projects.lovable.dto.auth.UserProfileReponse;
import com.projects.lovable.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileReponse toUserProfileReponse(User user);
}
