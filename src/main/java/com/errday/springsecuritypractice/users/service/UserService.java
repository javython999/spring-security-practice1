package com.errday.springsecuritypractice.users.service;

import com.errday.springsecuritypractice.admin.repository.RoleRepository;
import com.errday.springsecuritypractice.domain.entity.Account;
import com.errday.springsecuritypractice.domain.entity.Role;
import com.errday.springsecuritypractice.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Account createUser(Account account) {
        Role role = roleRepository.findByRoleName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        account.setUserRoles(roles);
        return userRepository.save(account);
    }
}
