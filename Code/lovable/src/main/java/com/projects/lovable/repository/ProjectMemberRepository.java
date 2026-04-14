package com.projects.lovable.repository;

import com.projects.lovable.entity.ProjectMember;
import com.projects.lovable.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    //select p from ProjectMember p where p.id.projectId = :projectId
    //ProjectMember -> id -> projectId
    List<ProjectMember> findByIdProjectId(Long projectId);
}
