package com.errday.springsecuritypractice.admin.repository;

import com.errday.springsecuritypractice.domain.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
}
