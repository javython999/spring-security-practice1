package com.errday.springsecuritypractice1.users.repository;

import com.errday.springsecuritypractice1.users.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}
