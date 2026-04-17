package com.projects.lovable.mapper;

import com.projects.lovable.dto.member.MemberResponse;
import com.projects.lovable.entity.ProjectMember;
import com.projects.lovable.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "username", source = "user.username")
    MemberResponse toProjectMemberResponseFromProjectMember(ProjectMember projectMember);
}
