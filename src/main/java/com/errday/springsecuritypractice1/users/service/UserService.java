package com.errday.springsecuritypractice1.users.service;

import com.errday.springsecuritypractice1.users.domain.entity.Account;
import com.errday.springsecuritypractice1.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public Account createUser(Account account) {
        return userRepository.save(account);
    }
}
