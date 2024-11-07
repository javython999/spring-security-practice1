package com.errday.springsecuritypractice.users.repository;

import com.errday.springsecuritypractice.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}
