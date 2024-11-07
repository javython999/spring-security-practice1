package com.errday.springsecuritypractice.admin.repository;

import com.errday.springsecuritypractice.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<Account, Long> {
}
