package com.projects.lovable.repository;

import com.projects.lovable.entity.ProjectMember;
import com.projects.lovable.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
}
