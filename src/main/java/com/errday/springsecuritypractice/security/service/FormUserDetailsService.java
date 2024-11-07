package com.errday.springsecuritypractice.security.service;

import com.errday.springsecuritypractice.domain.dto.AccountContext;
import com.errday.springsecuritypractice.domain.dto.AccountDto;
import com.errday.springsecuritypractice.domain.entity.Account;
import com.errday.springsecuritypractice.domain.entity.Role;
import com.errday.springsecuritypractice.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("userDetailsService")
@RequiredArgsConstructor
@Transactional
public class FormUserDetailsService implements UserDetailsService {

    private final ModelMapper modelMapper = new ModelMapper();
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account findByUsername = userRepository.findByUsername(username);

        if (findByUsername == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        List<GrantedAuthority> authorities = findByUsername.getUserRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet())
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        AccountDto accountDto = modelMapper.map(findByUsername, AccountDto.class);

        return new AccountContext(accountDto, authorities);
    }
}
